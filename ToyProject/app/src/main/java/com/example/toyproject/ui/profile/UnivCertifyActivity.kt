package com.example.toyproject.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityUnivCertifyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UnivCertifyActivity: AppCompatActivity() {
    private lateinit var binding: ActivityUnivCertifyBinding

    private val viewModel: UnivCertifyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUnivCertifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.emailInputLayout.hint = intent.getStringExtra("email")

        binding.sendCodeButton.setOnClickListener{
            viewModel.sendCode(binding.emailInputLayout.hint.toString())
        }

        viewModel.result.observe(this, {
            if(it==201){
                Toast.makeText(this, "코드가 전송되었습니다.", Toast.LENGTH_SHORT).show()
                binding.sendCodeButton.text = "재전송"
            }
        })

        viewModel.error.observe(this,{
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        binding.univCertifyButton.setOnClickListener{
            viewModel.compareCode(binding.emailInputLayout.hint.toString(), Integer.parseInt(binding.codeInput.text.toString()))
        }

        viewModel.resultComp.observe(this, {
            if(it.Result=="Correct Code"){
                Toast.makeText(this, "인증되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
                setResult(RESULT_OK, Intent())
                finish()
            }
        })

        viewModel.errorComp.observe(this,{
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }
}