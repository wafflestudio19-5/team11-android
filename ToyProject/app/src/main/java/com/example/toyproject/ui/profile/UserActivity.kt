package com.example.toyproject.ui.profile


import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import android.os.Build

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream


@AndroidEntryPoint
class UserActivity: AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private val viewModel: UserViewModel by viewModels()
    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val uri: Uri? = result.data?.data
            if (uri != null) {
                val realPath = getFullPathFromUri(uri)
                if (realPath != null) {
                    //이미지 데이터를 비트맵으로 받아옴
                        lateinit var imageBitmap: Bitmap
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
                            imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
                        } else{
                            imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        }
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)
                    val requestBody: RequestBody = byteArrayOutputStream.toByteArray()
                        .toRequestBody()

                        if(imageBitmap.height>4000||imageBitmap.width>4000){
                            Toast.makeText(this, "4000px*4000px 이하의 이미지만 업로드할 수 있습니다.", Toast.LENGTH_SHORT).show()
                        }else{
                            upload(requestBody)
                            binding.profileImageView.setImageURI(result.data?.data)
                        }
                }
            }
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
            if(it.profile_image!=null){
                val credentials: BasicAWSCredentials
                val key = getString(R.string.AWS_ACCESS_KEY_ID)
                val secret = getString(R.string.AWS_SECRET_ACCESS_KEY)
                val objectKey = it.profile_image.substring(52)
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
                Glide.with(this)
                    .setDefaultRequestOptions(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .placeholder(R.drawable.anonymous_photo)
                            .fitCenter()
                    )
                    .load(url.toString())
                    .centerCrop()
                    .into(findViewById(R.id.profileImageView))
            }
            nickname = it.nickname.toString()
            email = it.email.toString()
            binding.userId.text = it.user_id
            binding.userProfile.text = "${it.name} / ${it.nickname}"
            binding.userProfile2.text = "${it.university} ${it.admission_year?.toString()?.substring(2)}학번"
        })


        // RESULT_OK : 새로고침 (사용처: 닉네임 재설정)
        // 999 : 상위 액티비티 싹 종료 (사용처: 비번, 이메일 재설정 완료, 계정 탈퇴)
        val resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == RESULT_OK) viewModel.getUser()
                if(it.resultCode == 999) {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        binding.changeEmail.setOnClickListener{
            val intent = Intent(this, ChangeEmailActivity::class.java)
            intent.putExtra("email", email)
            resultListener.launch(intent)
        }

        binding.changeNick.setOnClickListener{
            val intent = Intent(this, ChangeNicknameActivity::class.java)
            intent.putExtra("nickname", nickname)
            resultListener.launch(intent)
        }

        binding.changePw.setOnClickListener{
            val intent = Intent(this, ChangePasswordActivity::class.java)
            resultListener.launch(intent)
        }

        binding.deleteAccount.setOnClickListener {
            val intent = Intent(this, WithdrawalActivity::class.java)
            resultListener.launch(intent)
        }

        binding.univCertify.setOnClickListener {
            val intent = Intent(this, UnivCertifyActivity::class.java)
            intent.putExtra("email", email)
            resultListener.launch(intent)
        }

        binding.changeImage.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.change_image_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)

            val  mAlertDialog = mBuilder.show()

            val changeButton = mDialogView.findViewById<Button>(R.id.changeImageButton)
            changeButton.setOnClickListener {
                selectGallery()
                mAlertDialog.dismiss()
            }

            val deleteButton = mDialogView.findViewById<Button>(R.id.deleteImageButton)
            deleteButton.setOnClickListener {
                //viewModel.putImage(null)
                binding.profileImageView.setImageResource(R.drawable.anonymous_photo)
                mAlertDialog.dismiss()
            }
        }

        // 로그아웃 버튼
        binding.logout.setOnClickListener {
            // 구글 로그아웃
            Firebase.auth.signOut()

            //카카오 로그아웃
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    //...
                } else {
                    Toast.makeText(this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }


            val intent  = Intent(this, LoginActivity::class.java)
            intent.putExtra("logout", true)
            sharedPreferences.edit {
                this.remove("token")
            }
            applicationContext.cacheDir.deleteRecursively()
            baseContext.cacheDir.deleteRecursively()
            startActivity(intent)
            setResult(RESULT_OK, Intent())
            finish()
        }
    }

    private fun selectGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        intent.type = "image/*"
        getContent.launch(intent)

    }

    private fun upload(requestBody: RequestBody) {
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("profile_image", fileName, requestBody)
        viewModel.putImage(body)
    }

    private fun getFullPathFromUri(fileUri: Uri): String? {
        var fullPath: String? = null
        val column = "_data"
        var cursor: Cursor? = contentResolver.query(fileUri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            var documentId = cursor.getString(0)
            if (documentId == null) {
                for (i in 0 until cursor.columnCount) {
                    if (column.equals(cursor.getColumnName(i), ignoreCase = true)) {
                        fullPath = cursor.getString(i)
                        break
                    }
                }
            } else {
                documentId = documentId.substring(documentId.lastIndexOf(":") + 1)
                cursor.close()
                val projection = arrayOf(column)
                try {
                    cursor = contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.Media._ID + " = ? ",
                        arrayOf(documentId),
                        null
                    )
                    if (cursor != null) {
                        cursor.moveToFirst()
                        fullPath = cursor.getString(cursor.getColumnIndexOrThrow(column))
                    }
                } finally {
                    cursor.close()
                }
            }
        }
        return fullPath
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_left)
    }
}