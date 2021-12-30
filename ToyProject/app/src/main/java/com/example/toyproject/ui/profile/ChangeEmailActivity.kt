package com.example.toyproject.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.toyproject.databinding.ActivityChangeEmailBinding
import com.example.toyproject.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChangeEmailActivity:AppCompatActivity() {
    private lateinit var binding: ActivityChangeEmailBinding

    private val viewModel: EmailViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
           binding= ActivityChangeEmailBinding.inflate(layoutInflater)
           setContentView(binding.root)
           binding.emailInputLayout.hint = intent.getStringExtra("email")

           binding.changeEmailButton.setOnClickListener{
               if(binding.newEmail.text.toString() == binding.emailInputLayout.hint){
                   Toast.makeText(this, "기존의 이메일과 동일합니다.", Toast.LENGTH_SHORT).show()
               }
               else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.newEmail.text.toString()).matches())
               {
                   Toast.makeText(this,"이메일 형식이 아닙니다",Toast.LENGTH_SHORT).show()
               }
               else{
                   viewModel.changeEmail(binding.newEmail.text.toString())
               }
           }
           //이렇게 되면 비번입력이 의미가 있나
           viewModel.response.observe(this, {
               if(it.success==true){
                   //mGoogleSignInClient.signOut()
                   val intent  = Intent(this, LoginActivity::class.java)
                   //intent.putExtra("logout", true)
                   sharedPreferences.edit {
                       this.remove("token")
                   }
                   startActivity(intent)
                   finish()
               }
           })

           viewModel.result.observe(this,{
               Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
           })
    }
}