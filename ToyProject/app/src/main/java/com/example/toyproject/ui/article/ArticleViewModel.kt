package com.example.toyproject.ui.article

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.BoardService
import com.example.toyproject.network.dto.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val service : BoardService,
    private val retrofit: Retrofit
) : ViewModel() {

    private val _result = MutableLiveData<ArticleContent>()
    val result: LiveData<ArticleContent> = _result

    var reload : Boolean = false

    private val _likeResult = MutableLiveData<String>()
    val likeResult : LiveData<String> = _likeResult

    private val _deleteResult = MutableLiveData<String>()
    val deleteResult : LiveData<String> = _deleteResult

    private val _scrapResult = MutableLiveData<String>()
    val scrapResult : LiveData<String> = _scrapResult

    private val _subscribeResult = MutableLiveData<String>()
    val subscribeResult : LiveData<String> = _subscribeResult

    private val _subscribeCommentResult = MutableLiveData<String>()
    val subscribeCommentResult : LiveData<String> = _subscribeCommentResult

    private val _deleteArticleResult = MutableLiveData<String>()
    val deleteArticleResult : LiveData<String> = _deleteArticleResult

    fun getArticle(boardId : Int, articleId : Int) {
        viewModelScope.launch {
            reload = false
            _result.value = service.getArticleContent(boardId, articleId)
        }
    }

    fun addComment(board_id : Int, article_id : Int, param : CommentCreate) {
        viewModelScope.launch {
            reload = true
            service.addComment(board_id, article_id, param)
            _result.value = service.getArticleContent(board_id, article_id)
        }
    }
    // ?????? ?????????
    fun likeComment(comment_id : Int) {
        service.likeComment(comment_id).clone().enqueue(object : Callback<LikeResponse> {
            override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                _likeResult.value = "???????????? ????????? ?????????????????????."
            }
            override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {

                if(response.isSuccessful) {
                    // ??? ?????? ?????? ?????? ??????
                    _likeResult.value = "success_comment"
                }
                else {
                    // ?????? ??????
                    val error = retrofit.responseBodyConverter<ErrorMessage>(
                        ErrorMessage::class.java,
                        ErrorMessage::class.java.annotations
                    ).convert(response.errorBody())
                    _likeResult.value = error!!.detail.toString()
                }

            }
        })
    }

    // ????????? ?????????
    fun likeArticle(article_id : Int) {
        service.likeArticle(article_id).clone().enqueue(object : Callback<LikeResponse> {
            override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                _likeResult.value = "???????????? ????????? ?????????????????????."
            }
            override fun onResponse( call: Call<LikeResponse>, response: Response<LikeResponse>) {
                if(response.isSuccessful) {
                    // ????????? ????????? ??????
                    _likeResult.value = "success_article"
                }
                else {
                    // ??????
                    val error = retrofit.responseBodyConverter<ErrorMessage>(
                        ErrorMessage::class.java,
                        ErrorMessage::class.java.annotations
                    ).convert(response.errorBody())
                    _likeResult.value = error!!.detail.toString()
                }
            }
        })
    }
    // ????????? ?????????
    fun scrapArticle(article_id: Int) {
        service.scrapArticle(article_id).clone().enqueue(object : Callback<ScrapResponse> {
            override fun onFailure(call: Call<ScrapResponse>, t: Throwable) {
                _scrapResult.value = "???????????? ????????? ?????????????????????."
            }

            override fun onResponse(call: Call<ScrapResponse>, response: Response<ScrapResponse>) {
                if(response.isSuccessful) {
                    // ????????? ????????? ??????
                    _scrapResult.value = response.body()!!.detail.toString()
                }
                else {
                    // ??????
                    val error = retrofit.responseBodyConverter<ErrorMessage>(
                        ErrorMessage::class.java,
                        ErrorMessage::class.java.annotations
                    ).convert(response.errorBody())
                    _scrapResult.value = error!!.detail.toString()
                }
            }
        })
    }

    //????????? ?????? ??????
    fun subscribeArticle(article_id: Int) {
        service.subscribeArticle(article_id).clone().enqueue(object : Callback<ScrapResponse> {
            override fun onFailure(call: Call<ScrapResponse>, t: Throwable) {
                _subscribeResult.value = "???????????? ????????? ?????????????????????."
            }

            override fun onResponse(call: Call<ScrapResponse>, response: Response<ScrapResponse>) {
                if(response.isSuccessful){
                    //????????? ?????? ?????? ??????
                    _subscribeResult.value = response.body()!!.detail.toString()
                }
                else{
                    //??????
                    val error = retrofit.responseBodyConverter<ErrorMessage>(
                        ErrorMessage::class.java,
                        ErrorMessage::class.java.annotations
                    ).convert(response.errorBody())
                    _subscribeResult.value = error!!.detail.toString()
                }
            }
        })
    }

    //?????? ?????? ??????
    fun subscribeComment(comment_id: Int) {
        service.subscribeComment(comment_id).clone().enqueue(object : Callback<ScrapResponse> {
            override fun onFailure(call: Call<ScrapResponse>, t: Throwable) {
                _subscribeCommentResult.value = "???????????? ????????? ?????????????????????."
            }

            override fun onResponse(call: Call<ScrapResponse>, response: Response<ScrapResponse>) {
                if(response.isSuccessful){
                    //?????? ?????? ?????? ??????
                    _subscribeCommentResult.value = response.body()!!.detail.toString()
                }
                else{
                    //??????
                    val error = retrofit.responseBodyConverter<ErrorMessage>(
                        ErrorMessage::class.java,
                        ErrorMessage::class.java.annotations
                    ).convert(response.errorBody())
                    _subscribeCommentResult.value = error!!.detail.toString()
                }
            }
        })
    }

    // ?????? ??????
    fun deleteComment(board_id : Int, article_id : Int, comment_id : Int) {
        service.deleteComment(board_id, article_id, comment_id).enqueue(object : Callback<CommentDeleteResponse> {
            override fun onFailure(call: Call<CommentDeleteResponse>, t: Throwable) {
                _deleteResult.value = "????????? ????????? ?????????????????????"
            }

            override fun onResponse(call: Call<CommentDeleteResponse>, response: Response<CommentDeleteResponse>) {
                if(response.isSuccessful) {
                    _deleteResult.value = "success"
                }
                else {
                    // ??????
                    val error = retrofit.responseBodyConverter<ErrorMessage>(
                        ErrorMessage::class.java,
                        ErrorMessage::class.java.annotations
                    ).convert(response.errorBody())
                    _deleteResult.value = error!!.detail.toString()
                }
            }
        })
    }

    fun deleteArticle(board_id: Int, article_id: Int){

        try{
            service.deleteArticle(board_id, article_id).clone().enqueue(object : Callback<Success>{
                override fun onFailure(call: Call<Success>, t: Throwable) {
                    _deleteArticleResult.value = "???????????? ????????? ?????????????????????."
                }

                override fun onResponse(call: Call<Success>, response: Response<Success>) {
                    if(response.isSuccessful) {
                        // ????????? ?????? ??????
                        _deleteArticleResult.value = "success"
                    }
                    else {
                        // ??????
                        val error = retrofit.responseBodyConverter<ErrorMessage>(
                            ErrorMessage::class.java,
                            ErrorMessage::class.java.annotations
                        ).convert(response.errorBody())
                        _deleteArticleResult.value = error!!.detail.toString()
                    }
                }
            })
        } catch(e: Exception){
            Timber.e(e)
        }

    }



}