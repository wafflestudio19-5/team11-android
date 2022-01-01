package com.example.toyproject.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.BoardService
import com.example.toyproject.network.dto.ArticleContent
import com.example.toyproject.network.dto.CommentCreate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val service : BoardService
) : ViewModel() {

    private val _result = MutableLiveData<ArticleContent>()
    val result: LiveData<ArticleContent> = _result

    var reload : Boolean = false

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
}