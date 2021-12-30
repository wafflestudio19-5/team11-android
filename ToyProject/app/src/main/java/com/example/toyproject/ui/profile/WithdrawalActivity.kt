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
            else{
                Toast.makeText(this, it.detail, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.result.observe(this,{
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }
}