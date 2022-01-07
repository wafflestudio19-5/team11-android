package com.example.toyproject.ui.article

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityArticleMakeBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.lang.Exception

@AndroidEntryPoint
class ArticleMakeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleMakeBinding
    private val viewModel: ArticleMakeViewModel by viewModels()
    var uriList: ArrayList<Uri?> = ArrayList()

    private lateinit var articleMakeAdapter: ArticleMakeAdapter
    private lateinit var articleMakeLayoutManager: LinearLayoutManager

    private var articleTitleTemp: String = ""
    private var articleTextTemp: String = ""
    private var boardId: Int = 0
    private var isAnonymous: Boolean = true
    private var isQuestion: Boolean = false

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

            if(result.data?.clipData == null){
                val imageUri: Uri? = result.data?.data
                uriList.add(imageUri)
                articleMakeAdapter = ArticleMakeAdapter(this)
                articleMakeLayoutManager = LinearLayoutManager(this).also { it.orientation = LinearLayoutManager.HORIZONTAL }
                binding.articleImageUploadView.apply {
                    adapter = articleMakeAdapter
                    layoutManager = articleMakeLayoutManager
                }
                articleMakeAdapter.setImages(uriList)
                articleMakeAdapter.notifyDataSetChanged()
                binding.warningView.visibility = View.INVISIBLE
            }else{
                val clipData = result.data?.clipData
                if(clipData?.itemCount!! > 5){
                    Toast.makeText(this, "사진은 5장 이하까지만 첨부할 수 있습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri // 선택한 이미지들의 uri를 가져온다.
                        try {
                            uriList.add(imageUri) //uri를 list에 담는다.
                            articleMakeAdapter = ArticleMakeAdapter(this)
                            articleMakeLayoutManager = LinearLayoutManager(this).also { it.orientation = LinearLayoutManager.HORIZONTAL }
                            binding.articleImageUploadView.apply {
                                adapter = articleMakeAdapter
                                layoutManager = articleMakeLayoutManager
                            }
                            articleMakeAdapter.setImages(uriList)
                            articleMakeAdapter.notifyDataSetChanged()
                            binding.warningView.visibility = View.INVISIBLE
                        } catch (e: Exception) {
                            //...
                        }
                    }
                }
            }

/*

            if (uri != null) {
                val realPath = getFullPathFromUri(uri)
                if (realPath != null) {
                    //이미지 데이터를 비트맵으로 받아옴
                    lateinit var imageBitmap: Bitmap
                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.P){
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
                        //upload(requestBody)
                        //binding.p.setImageURI(result.data?.data)
                    }
                }
            }
            */
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArticleMakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        articleMakeAdapter = ArticleMakeAdapter(this)
        articleMakeLayoutManager = LinearLayoutManager(this)

        boardId = intent.getIntExtra("board_id", 0)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.articleTitle.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                articleTitleTemp = binding.articleTitle.text.toString()
            }
        })

        binding.articleText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                articleTextTemp = binding.articleText.text.toString()
            }
        })

        //image upload
        binding.galleryButton.setOnClickListener {
            selectGallery()
        }

        binding.apply{
            makeArticleButton.setOnClickListener{
                if(articleTitleTemp==""){
                    Toast.makeText(this@ArticleMakeActivity, "제목을 입력해주세요.", Toast.LENGTH_LONG).show()
                } else if(articleTextTemp==""){
                    Toast.makeText(this@ArticleMakeActivity, "내용을 입력해주세요.", Toast.LENGTH_LONG).show()
                } else{
                    viewModel.createArticle(boardId, articleTitleTemp, articleTextTemp, isAnonymous, isQuestion)
                    finish()
                }
            }
        }

    }

    fun onCheckboxClicked(view: View){
        if(view is CheckBox){
            val checked: Boolean = view.isChecked

            when(view.id){
                R.id.anonymous_checkbox -> {
                    isAnonymous = checked
                }
                R.id.question_checkbox ->{
                    isQuestion = checked
                }
            }
        }
    }

    private fun selectGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        getContent.launch(intent)

    }

    private fun upload(requestBody: RequestBody) {
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("profile_image", fileName, requestBody)
        //viewModel.putImage(body)
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
}