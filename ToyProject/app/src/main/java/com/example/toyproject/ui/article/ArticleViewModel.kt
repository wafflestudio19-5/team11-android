package com.example.toyproject.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.BoardService
import com.example.toyproject.network.dto.ArticleContent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val service : BoardService
) : ViewModel() {

    private val _result = MutableLiveData<ArticleContent>()
    val result: LiveData<ArticleContent> = _result

    fun getArticle(boardId : Int, articleId : Int) {
        viewModelScope.launch {
            _result.value = service.getArticleContent(boardId, articleId)
        }
    }
}