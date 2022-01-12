package com.example.toyproject.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.BoardService
import com.example.toyproject.network.dto.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ArticleSearchViewModel @Inject constructor(
    private val service : BoardService
) : ViewModel(){

    private val _articleList = MutableLiveData<MutableList<Article>>()
    val articleList: LiveData<MutableList<Article>> = _articleList

    private val _listSize = MutableLiveData<Int>(1)
    val listSize: LiveData<Int> = _listSize

    fun getArticleList(board_id: Int, offset: Int, limit: Int, search: String) {
        viewModelScope.launch{
            try{
                if(_listSize.value!! < offset) return@launch
                _articleList.value = service.getArticlesByKeyword(board_id, offset, limit, search).results!!
                _listSize.value = service.getArticlesByKeyword(board_id, offset, limit, search).count
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }





}