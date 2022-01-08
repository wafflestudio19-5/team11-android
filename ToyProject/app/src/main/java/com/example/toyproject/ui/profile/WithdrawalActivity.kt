package com.example.toyproject.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
            showDialog()
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

    private fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("탈퇴 확인")
            .setMessage("정말로 탈퇴하시겠습니까?")
            .setPositiveButton("네") { _, _ ->
                viewModel.withdrawal(binding.curPasswordInput.text.toString())
            }
            .setNegativeButton("아니오") { _, _ ->
                Toast.makeText(applicationContext, "취소되었습니다.", Toast.LENGTH_SHORT).show()
            }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, UserActivity::class.java)
        startActivity(intent)
        setResult(RESULT_OK, Intent())
        finish()
    }
}