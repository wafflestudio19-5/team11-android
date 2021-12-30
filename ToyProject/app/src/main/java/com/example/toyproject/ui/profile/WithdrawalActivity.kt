package com.example.toyproject.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.toyproject.databinding.ActivityWithdrawalBinding
import com.example.toyproject.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WithdrawalActivity: AppCompatActivity() {
    private lateinit var binding: ActivityWithdrawalBinding
    private val viewModel: WithdrawalViewModel by viewModels()
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.withdrawalButton.setOnClickListener{
            var password = binding.curPassword.text.toString().replace("!", "%21")
            password = password.replace("@", "%40")
            password = password.replace("#", "%23")
            password = password.replace("$", "%24")
            password = password.replace("%", "%25")
            password = password.replace("^", "%5e")
            password = password.replace("$", "%26")
            password = password.replace("*", "%2A")
            viewModel.withdrawal(binding.curPassword.text.toString())
        }

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