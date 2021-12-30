package com.example.toyproject.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.toyproject.databinding.ActivityChangePasswordBinding
import com.example.toyproject.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class ChangePasswordActivity:AppCompatActivity() {
    private lateinit var binding:ActivityChangePasswordBinding
    private val viewModel: PasswordViewModel by viewModels()
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.changePwButton.setOnClickListener{
            if(binding.passwordEdit.text.toString() != binding.passwordConfirm.text.toString()){
                Toast.makeText(this, "비밀번호가 불일치합니다.", Toast.LENGTH_SHORT).show()
            }
            else if(!Pattern.matches("^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#\$%^&*])(?=.*[0-9!@#\$%^&*]).{8,20}\$", binding.passwordConfirm.text.toString()))
                {
                    Toast.makeText(this,"비밀번호 형식을 지켜주세요.",Toast.LENGTH_SHORT).show();
            }
            else{
                viewModel.changePassword(binding.passwordEdit.text.toString())
            }
        }

        viewModel.response.observe(this, {
            if(it.success==true){
                //mGoogleSignInClient.signOut()
                val intent  = Intent(this, LoginActivity::class.java)
                intent.putExtra("logout", true)
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