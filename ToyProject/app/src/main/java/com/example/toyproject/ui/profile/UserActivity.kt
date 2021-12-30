package com.example.toyproject.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.toyproject.databinding.ActivityUserBinding
import com.example.toyproject.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserActivity: AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private val viewModel: UserViewModel by viewModels()
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lateinit var nickname: String
        lateinit var email: String

        viewModel.getUser()

        viewModel.result.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        viewModel.profile.observe(this, {
            nickname = it.nickname.toString()
            email = it.email.toString()
            binding.userId.text = it.user_id
            binding.userProfile.text = "${it.name} / ${it.nickname}"
            binding.userProfile2.text = "${it.university} ${it.admission_year?.minus(2000)}학번"
        })

        binding.changeEmail.setOnClickListener{
            val intent = Intent(this, ChangeEmailActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
            setResult(RESULT_OK, Intent())
            finish()
        }

        binding.changeNick.setOnClickListener{
            val intent = Intent(this, ChangeNicknameActivity::class.java)
            intent.putExtra("nickname", nickname)
            startActivity(intent)
            setResult(RESULT_OK, Intent())
            finish()
        }

        binding.changePw.setOnClickListener{
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
            setResult(RESULT_OK, Intent())
            finish()
        }

        binding.logout.setOnClickListener {
            //mGoogleSignInClient.signOut()
            val intent  = Intent(this, LoginActivity::class.java)
            //intent.putExtra("logout", true)
            sharedPreferences.edit {
                this.remove("token")
            }
            startActivity(intent)
            finish()
        }

        binding.deleteAccount.setOnClickListener {
            val intent = Intent(this, WithdrawalActivity::class.java)
            startActivity(intent)
            setResult(RESULT_OK, Intent())
            finish()
        }
    }
}