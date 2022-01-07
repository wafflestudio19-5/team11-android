package com.example.toyproject.ui.profile


import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.bumptech.glide.Glide
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityUserBinding
import com.example.toyproject.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest

import com.amazonaws.services.s3.AmazonS3Client

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import java.net.URL
import java.util.*
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.bumptech.glide.request.RequestOptions
import com.example.toyproject.network.ProfileImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


@AndroidEntryPoint
class UserActivity: AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var file: File
    val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            binding.profileImageView.setImageURI(result.data?.data)
            file = File(result.data?.dataString)
        }
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

        viewModel.imageResult.observe(this,{
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        viewModel.profile.observe(this, {
            val credentials: BasicAWSCredentials
            val key = getString(R.string.AWS_ACCESS_KEY_ID)
            val secret = getString(R.string.AWS_SECRET_ACCESS_KEY)
            val objectKey = it.profile_image?.substring(52)
            credentials = BasicAWSCredentials(key, secret)
            val s3 = AmazonS3Client(
                credentials, com.amazonaws.regions.Region.getRegion(
                    Regions.AP_NORTHEAST_2
                )
            )
            val expires = Date(Date().getTime() + 1000 * 60) // 1 minute to expire
            val generatePresignedUrlRequest =
                GeneratePresignedUrlRequest("team11bucket", objectKey) //generating the signatured url
            generatePresignedUrlRequest.expiration = expires
            val url: URL = s3.generatePresignedUrl(generatePresignedUrlRequest)
            nickname = it.nickname.toString()
            email = it.email.toString()
            binding.userId.text = it.user_id
            binding.userProfile.text = "${it.name} / ${it.nickname}"
            binding.userProfile2.text = "${it.university} ${it.admission_year?.minus(2000)}학번"
            Glide.with(this)
                .setDefaultRequestOptions(
                    RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .placeholder(R.drawable.anonymous_photo)
                        .fitCenter()
                )
                .load(url.toString())
                .into(findViewById(R.id.profileImageView))
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
            intent.putExtra("logout", true)
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

        binding.univCertify.setOnClickListener {
            val intent = Intent(this, UnivCertifyActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
            setResult(RESULT_OK, Intent())
            finish()
        }

        binding.changeImage.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.change_image_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

            val  mAlertDialog = mBuilder.show()

            val changeButton = mDialogView.findViewById<Button>(R.id.changeImageButton)
            changeButton.setOnClickListener {
                selectGallery()
                var fileName = binding.userProfile.text.toString()
                fileName += ".png"
                val requestBody : RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body : MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file",fileName,requestBody)
                viewModel.putImage(ProfileImage(body))
                mAlertDialog.dismiss()
            }

            val deleteButton = mDialogView.findViewById<Button>(R.id.deleteImageButton)
            deleteButton.setOnClickListener {
                binding.profileImageView.setImageResource(R.drawable.anonymous_photo)
                mAlertDialog.dismiss()
            }
        }
    }

    private fun selectGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        intent.type = "image/*"
        getContent.launch(intent)

    }


}