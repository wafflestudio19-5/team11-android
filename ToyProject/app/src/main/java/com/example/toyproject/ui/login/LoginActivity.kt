package com.example.toyproject.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.strictmode.GetRetainInstanceUsageViolation
import com.example.toyproject.App
import com.example.toyproject.R
import com.example.toyproject.ui.univsearch.UnivSearchActivity
import com.example.toyproject.ui.main.MainActivity
import com.example.toyproject.databinding.ActivityLoginBinding
import com.example.toyproject.network.dto.Login
import com.example.toyproject.network.dto.LoginSocial
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.KakaoSDK
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.usermgmt.response.model.User
import com.kakao.util.exception.KakaoException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity:AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel : LoginViewModel by viewModels()
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    // Google 로그인
    private lateinit var auth : FirebaseAuth
    private lateinit var mGoogleSignInClient : GoogleSignInClient


    // 카카오 로그인 콜백(로그인 할 때)
    internal val callback : (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Timber.e("로그인 실패- $error")
            Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
        }
        else if (token != null) {
            // 로그인 성공해서 토큰 받고, 추가 동의 사항(이메일) 받기
            val scopes = mutableListOf<String>()
            scopes.add("account_email")
            UserApiClient.instance.loginWithNewScopes(context = this, scopes) {
                    token, error ->
                // 추가 동의 사항 받았으면 유저 정보 재요청
                UserApiClient.instance.me { user, error ->
                    Timber.d(user!!.kakaoAccount.toString())
                    // 유저 정보를 취합해서 서버에 전달
                    viewModel.kakaoLogin(LoginSocial(token!!.accessToken), user.kakaoAccount?.email)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel

        // signup 이 완료되지 않았으면 뒤로가기 버튼으로 다시 이 화면으로 돌아올 수 있고, 이후 과정이 모두 끝날 때 이 액티비티를 종료
        val resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == RESULT_OK) {
                    finish()
                }
            }
        val resultListenerSocial =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == RESULT_OK) {
                    finish()
                }
            }

        // token 있을 때
        if(sharedPreferences.contains("token")) {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.loginToken()
            }
            viewModel.tokenResult.observe(this, {
                // token 으로 로그인 성공
                // TODO : 로그인 로딩 창 필요
                if(it=="success") {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                // token 만료 혹은 기타 에러
                else {
                    // 에러 메시지 띄우고,
                    Toast.makeText(this, viewModel.errorMessage, Toast.LENGTH_LONG).show()
                    sharedPreferences.edit {
                        this.remove("token")
                    }

                    // 평소처럼 진행
                    binding.loginButton.setOnClickListener {
                        val param = Login(binding.emailEdit.text.toString(), binding.passwordEdit.text.toString())
                        viewModel.login(param)
                    }
                    viewModel.result.observe(this, {
                        if(it=="success") {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else {
                            Toast.makeText(this, viewModel.errorMessage, Toast.LENGTH_LONG).show()
                        }
                    })
                    binding.signupButton.setOnClickListener {
                        val intent = Intent(this, UnivSearchActivity::class.java)
                        resultListener.launch(intent)
                    }
                }
            })
        }
        // token 없음
        else {
            // Google 로그인 초기 설정
            auth = FirebaseAuth.getInstance()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            // 구글 로그인
            val googleResultListener =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result : ActivityResult ->
                    if(result.resultCode == RESULT_OK) {
                        val intent : Intent = result.data!!
                        val task : Task<GoogleSignInAccount> =
                            GoogleSignIn.getSignedInAccountFromIntent(intent)
                        try {
                            val account = task.getResult(ApiException::class.java)!!
                            firebaseAuthWithGoogle(account.idToken!!, account.email.toString())
                        } catch (e : Exception) {
                            Timber.d(e)
                        }
                    }
                    else {
                        Timber.d(result.resultCode.toString())
                        Timber.d("에러에러")
                    }
                }
            binding.googleButton.setOnClickListener {
                googleResultListener.launch(mGoogleSignInClient.signInIntent)
            }
            // 구글 로그인 서버 통신
            viewModel.googleLoginResult.observe(this, {
                if(it=="success") {
                    Toast.makeText(this, "로그인에 성공하였습니다",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else if(it=="fail") {
                    Toast.makeText(this, "서버와의 통신에 실패하였습니다", Toast.LENGTH_SHORT).show()
                }
                else {
                    // 소셜 회원가입에 필요한 추가 정보(대학, 연도 등등)을 얻으러 UnivSearchActivity 를 실행
                    Toast.makeText(this, "구글 계정으로 회원가입을 진행합니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UnivSearchActivity::class.java)
                    intent.putExtra("mode", "social")
                    intent.putExtra("socialType", "fire")
                    intent.putExtra("email", viewModel.registerInfoEmail)
                    intent.putExtra("access_token", viewModel.registerInfoToken)
                    resultListenerSocial.launch(intent)
                }
            })



            // 카카오 로그인 버튼
            binding.kakaoButton.setOnClickListener {
                UserApiClient.instance.loginWithKakaoAccount(this, callback=callback)
            }
            viewModel.kakaoLoginResult.observe(this, {
                if(it=="success") {
                    Toast.makeText(this, "로그인에 성공하였습니다",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else if(it=="fail") {
                    Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
                else if(it=="register") {
                    // 소셜 회원가입에 필요한 추가 정보(대학, 연도 등등)을 얻으러 UnivSearchActivity 를 실행
                    Toast.makeText(this, "카카오 계정으로 회원가입을 진행합니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UnivSearchActivity::class.java)
                    intent.putExtra("mode", "social")
                    intent.putExtra("socialType", "kakao")
                    intent.putExtra("email", viewModel.registerInfoEmail.toString())
                    intent.putExtra("access_token", viewModel.registerInfoToken.toString())
                    resultListenerSocial.launch(intent)
                }
            })

            // 로그인 버튼 눌렀을 때 (일반 로그인)
            binding.loginButton.setOnClickListener {
                val param = Login(binding.emailEdit.text.toString(), binding.passwordEdit.text.toString())
                viewModel.login(param)
            }
            viewModel.result.observe(this, {
                if(it=="success") {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    Toast.makeText(this, viewModel.errorMessage, Toast.LENGTH_LONG).show()
                }
            })
            binding.signupButton.setOnClickListener {
                val intent = Intent(this, UnivSearchActivity::class.java)
                resultListener.launch(intent)
            }
        }
    }


    // Google 로그인 부분(firebase)
    private fun firebaseAuthWithGoogle(idToken : String, email :String) {
        val credential : AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            task ->
            if(task.isSuccessful) {
                // 유저가 구글 로그인에 성공하면 토큰을 우리 서버에 보낸다.
                FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val param = LoginSocial(task.result.token.toString())
                        viewModel.googleLogin(param, email)
                    }
                }
            }
            else {
                Toast.makeText(this, "구글 로그인에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

