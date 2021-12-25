package com.example.toyproject

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityLoginBinding
import com.example.toyproject.network.dto.Login
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

        // token 있음
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
                        val intent = Intent(this, UnivCertifyActivity::class.java)
                        resultListener.launch(intent)
                    }
                }
            })
        }
        // token 없음
        else {

            // Google 로그인
            auth = FirebaseAuth.getInstance()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_web_client_id))
                .requestEmail()
                .build()
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            // 기존 구글 로그인 돼있으면 바로 로그인
            // TODO : 로그인 로딩 창 필요
            val preAccount : GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
            if (preAccount != null) {
                if(preAccount.idToken!=null) {
                    try {
                        firebaseAuthWithGoogle(preAccount.idToken!!)
                    } catch (e: Exception) {
                        Toast.makeText(this, "다시 로그인 해 주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            // 없으면 새로 구글 로그인
            val googleResultListener =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result : ActivityResult ->
                    if(result.resultCode == RESULT_OK) {
                        val intent : Intent = result.data!!
                        val task : Task<GoogleSignInAccount> =
                            GoogleSignIn.getSignedInAccountFromIntent(intent)
                        try {
                            val account = task.getResult(ApiException::class.java)!!
                            firebaseAuthWithGoogle(account.idToken!!)
                        } catch (e : Exception) {
                            Timber.d(e)
                        }
                    }
                }
            binding.googleButton.setOnClickListener {
                googleResultListener.launch(mGoogleSignInClient.signInIntent)
            }


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
                val intent = Intent(this, UnivCertifyActivity::class.java)
                resultListener.launch(intent)
            }
        }
    }

    // Google 로그인 부분
    private fun firebaseAuthWithGoogle(idToken : String) {
        val credential : AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            task ->
            if(task.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "로그인 되었습니다", Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                Toast.makeText(this, "구글 로그인에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

