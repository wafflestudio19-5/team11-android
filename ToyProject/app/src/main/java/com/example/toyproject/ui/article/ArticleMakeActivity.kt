package com.example.toyproject.ui.article

import android.content.Intent
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityArticleMakeBinding
import com.example.toyproject.databinding.DialogAddDescriptionBinding
import com.example.toyproject.ui.board.BoardActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream


@AndroidEntryPoint
class ArticleMakeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleMakeBinding
    private val viewModel: ArticleMakeViewModel by viewModels()
    var uriList: ArrayList<Uri?> = ArrayList()
    var descriptionList: ArrayList<String> = ArrayList()

    private lateinit var articleMakeAdapter: ArticleMakeAdapter
    private lateinit var articleMakeLayoutManager: LinearLayoutManager

    private var articleTitleTemp: String = ""
    private var articleTextTemp: String = ""
    private var boardId: Int = 0
    private var isAnonymous: Boolean = false
    private var isQuestion: Boolean = false

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

            if(result.data == null){
                //...
            }
            else{
                val imageUri: Uri? = result.data?.data
                uriList.add(imageUri)
                val dialogBinding = DialogAddDescriptionBinding.inflate(layoutInflater)
                val dialogBuilder = AlertDialog.Builder(this)
                    .setTitle("이미지 설명")
                    .setView(dialogBinding.root)
                    .setPositiveButton("완료") { _, _ ->
                        if(dialogBinding.textContent.text != null){
                            descriptionList.add(dialogBinding.textContent.text.toString())
                        }else{
                            descriptionList.add("")
                        }
                        articleMakeAdapter = ArticleMakeAdapter(this)
                        articleMakeLayoutManager = LinearLayoutManager(this).also {
                            it.orientation = LinearLayoutManager.HORIZONTAL
                        }
                        binding.articleImageUploadView.apply {
                            adapter = articleMakeAdapter
                            layoutManager = articleMakeLayoutManager
                        }
                        val list: ArrayList<ArticleImageInfo> = arrayListOf()
                        if(uriList.size!=0){
                            for(i in 0 until uriList.size){
                                list.add(ArticleImageInfo(uriList[i], descriptionList[i]))
                            }
                        }
                        articleMakeAdapter.setImages(list)
                        articleMakeAdapter.notifyDataSetChanged()
                        //binding.warningView.visibility = View.GONE
                    }
                val dialog = dialogBuilder.create()
                dialog.show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArticleMakeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 실행할 때 아래에서 올라오도록
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_nothing)

        articleMakeAdapter = ArticleMakeAdapter(this)
        articleMakeLayoutManager = LinearLayoutManager(this).also {
            it.orientation = LinearLayoutManager.HORIZONTAL
        }

        boardId = intent.getIntExtra("board_id", 0)

        binding.backButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_left)
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
            if(articleMakeAdapter.itemCount > 5){
                Toast.makeText(this, "사진은 5장 이하까지만 첨부할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }else{
                selectGallery()
            }
        }

        binding.apply{
            makeArticleButton.setOnClickListener{
                if(articleTitleTemp.replace(" ", "")==""){
                    Toast.makeText(this@ArticleMakeActivity, "제목을 입력해주세요.", Toast.LENGTH_LONG).show()
                } else if(articleTextTemp.replace(" ", "")==""){
                    Toast.makeText(this@ArticleMakeActivity, "내용을 입력해주세요.", Toast.LENGTH_LONG).show()
                } else{
                    val list: MutableList<MultipartBody.Part> = mutableListOf()
                    for(uri in uriList){
                        //val realPath = uri?.let { getFullPathFromUri(it) }
                        lateinit var imageBitmap: Bitmap
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
                            imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri!!))
                        } else{
                            imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        }
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)
                        val requestBody: RequestBody = byteArrayOutputStream.toByteArray()
                            .toRequestBody()
                        val fileName = System.currentTimeMillis().toString() + ".jpg"
                        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", fileName, requestBody)
                        list.add(body)
                    }
                    val titleRequestBody: RequestBody = articleTitleTemp.toPlainRequestBody()
                    val textRequestBody: RequestBody = articleTextTemp.toPlainRequestBody()
                    val anonymousBody : RequestBody = isAnonymous.toString().toPlainRequestBody()
                    val questionBody : RequestBody = isQuestion.toString().toPlainRequestBody()
                    val textHashMap = hashMapOf<String, RequestBody>()
                    textHashMap["title"] = titleRequestBody
                    textHashMap["text"] = textRequestBody
                    textHashMap["is_anonymous"] = anonymousBody
                    textHashMap["is_question"] = questionBody
                    val textsList = mutableListOf<MultipartBody.Part>()
                    for(texts in descriptionList){
                        val body: MultipartBody.Part = MultipartBody.Part.createFormData("texts", texts)
                        textsList.add(body)
                    }

                    viewModel.createArticle(boardId, textHashMap, textsList, list)
                    setResult(RESULT_OK)
                    finish()
                    overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
                }
            }
        }

    }

    private fun String?.toPlainRequestBody() = requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())

    fun onCheckboxClicked(view: View){
        if(view is CheckBox){
            val checked: Boolean = view.isChecked

            when(view.id){
                R.id.anonymous_checkbox -> {
                    isAnonymous = checked

                }
                R.id.question_checkbox ->{
                    if(checked) binding.questionLayout.visibility = View.VISIBLE
                    else binding.questionLayout.visibility = View.GONE
                    isQuestion = checked

                }
            }
        }
    }

    private fun selectGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        intent.type = "image/*"
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        getContent.launch(intent)

    }

    override fun onBackPressed() {
        finish()
        // 끝낼 땐 아래로 내려가기
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
    }
}