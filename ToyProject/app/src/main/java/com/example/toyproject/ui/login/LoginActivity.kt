package com.example.toyproject.ui.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
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

    // Google ?????????
    private lateinit var auth : FirebaseAuth
    private lateinit var mGoogleSignInClient : GoogleSignInClient


    // ????????? ????????? ??????(????????? ??? ???)
    internal val callback : (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Timber.e("????????? ??????- $error")
            Toast.makeText(this, "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
        }
        else if (token != null) {
            // ????????? ???????????? ?????? ??????, ?????? ?????? ??????(?????????) ??????
            val scopes = mutableListOf<String>()
            scopes.add("account_email")
            UserApiClient.instance.loginWithNewScopes(context = this, scopes) {
                    token, error ->
                // ?????? ?????? ?????? ???????????? ?????? ?????? ?????????
                UserApiClient.instance.me { user, error ->
                    Timber.d(user!!.kakaoAccount.toString())
                    // ?????? ????????? ???????????? ????????? ??????
                    viewModel.kakaoLogin(LoginSocial(token!!.accessToken), user.kakaoAccount?.email)
                }
            }
        }
    }
    // Google ????????? ??????(firebase)
    private fun firebaseAuthWithGoogle(idToken : String, email :String) {
        val credential : AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
                task ->
            if(task.isSuccessful) {
                // ????????? ?????? ???????????? ???????????? ????????? ?????? ????????? ?????????.
                FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val param = LoginSocial(task.result.token.toString())
                        viewModel.googleLogin(param, email)
                    }
                }
            }
            else {
                Toast.makeText(this, "?????? ???????????? ?????????????????????", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkSelfPermission()
        viewModel


        // signup ??? ???????????? ???????????? ???????????? ???????????? ?????? ??? ???????????? ????????? ??? ??????, ?????? ????????? ?????? ?????? ??? ??? ??????????????? ??????
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

        // token ?????? ???
        if(sharedPreferences.contains("token")) {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.loginToken()
            }
            viewModel.tokenResult.observe(this, {
                // token ?????? ????????? ??????
                // TODO : ????????? ?????? ??? ??????
                if(it=="success") {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                // token ?????? ?????? ?????? ??????
                else {
                    // ?????? ????????? ?????????,
                    Toast.makeText(this, viewModel.errorMessage, Toast.LENGTH_LONG).show()
                    sharedPreferences.edit {
                        this.remove("token")
                    }

                    // ???????????? ??????
                    binding.loginButton.setOnClickListener {
                        val param = Login(binding.emailEdit.text.toString(), binding.passwordEdit.text.toString())
                        viewModel.login(param)
                        var token : String = "ohmygod"

                        Firebase.messaging.getToken().addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w("LoginActivity", "Fetching FCM registration token failed", task.exception)
                                return@OnCompleteListener
                            }

                            // Get new FCM registration token
                            token = task.result
                            Toast.makeText(this, "my token is : " + token, Toast.LENGTH_SHORT).show()
                            viewModel.fcmToken(token)

                        })

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
        // token ??????
        else {
            // Google ????????? ?????? ??????
            auth = FirebaseAuth.getInstance()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            // ?????? ?????????
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
                        Timber.d("????????????")
                    }
                }
            binding.googleButton.setOnClickListener {
                googleResultListener.launch(mGoogleSignInClient.signInIntent)
            }
            // ?????? ????????? ?????? ??????
            viewModel.googleLoginResult.observe(this, {
                if(it=="success") {
                    Toast.makeText(this, "???????????? ?????????????????????",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)

                    startActivity(intent)
                    finish()
                }
                else if(it=="fail") {
                    Toast.makeText(this, "???????????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show()
                }
                else {
                    // ?????? ??????????????? ????????? ?????? ??????(??????, ?????? ??????)??? ????????? UnivSearchActivity ??? ??????
                    Toast.makeText(this, "?????? ???????????? ??????????????? ???????????????.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UnivSearchActivity::class.java)
                    intent.putExtra("mode", "social")
                    intent.putExtra("socialType", "fire")
                    intent.putExtra("email", viewModel.registerInfoEmail)
                    intent.putExtra("access_token", viewModel.registerInfoToken)
                    resultListenerSocial.launch(intent)
                }
            })



            // ????????? ????????? ??????
            binding.kakaoButton.setOnClickListener {
                UserApiClient.instance.loginWithKakaoAccount(this, callback=callback)
            }
            viewModel.kakaoLoginResult.observe(this, {
                if(it=="success") {
                    Toast.makeText(this, "???????????? ?????????????????????",Toast.LENGTH_SHORT).show()
                    intent.putExtra("socialType", "kakao")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else if(it=="fail") {
                    Toast.makeText(this, "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
                }
                else if(it=="register") {
                    // ?????? ??????????????? ????????? ?????? ??????(??????, ?????? ??????)??? ????????? UnivSearchActivity ??? ??????
                    Toast.makeText(this, "????????? ???????????? ??????????????? ???????????????.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UnivSearchActivity::class.java)
                    intent.putExtra("mode", "social")
                    intent.putExtra("socialType", "kakao")
                    intent.putExtra("email", viewModel.registerInfoEmail.toString())
                    intent.putExtra("access_token", viewModel.registerInfoToken.toString())
                    resultListenerSocial.launch(intent)
                }
            })

            // ????????? ?????? ????????? ??? (?????? ?????????)
            binding.loginButton.setOnClickListener {

                val param = Login(binding.emailEdit.text.toString(), binding.passwordEdit.text.toString())
                viewModel.login(param)




            }
            viewModel.result.observe(this, {
                if(it=="success") {

                    Firebase.messaging.getToken().addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("LoginActivity", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        var token = task.result
                        //Toast.makeText(this, "my token is : " + token, Toast.LENGTH_SHORT).show()

                        val msg = "FCM registration Token: " + token
                        Log.d("LoginActivity", msg)

                        viewModel.fcmToken(token)
                    })

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("socialType", "normal")
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

    //????????? ?????? ????????? ????????? ???????????? ??????
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            val length = permissions.size
            for (i in 0 until length) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkSelfPermission() {
        var temp = ""
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " "
        }
        if (!TextUtils.isEmpty(temp)) {
            ActivityCompat.requestPermissions(
                this,
                temp.trim { it <= ' ' }.split(" ").toTypedArray(),
                1
            )
        } else {
            // Toast.makeText(this, "????????? ?????? ??????", Toast.LENGTH_SHORT).show()
        }
    }
}

