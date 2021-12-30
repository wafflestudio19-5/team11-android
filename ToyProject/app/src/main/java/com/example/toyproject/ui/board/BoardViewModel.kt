package com.example.toyproject.ui.board

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
class BoardViewModel @Inject constructor(
    private val service: BoardService
): ViewModel(){

    private val _articleList = MutableLiveData<MutableList<Article>>()
    val articleList: LiveData<MutableList<Article>> = _articleList

    private val _listSize = MutableLiveData<Int>(0)
    val listSize: LiveData<Int> = _listSize

    fun getArticleList(board_id: Int, offset: Int, limit: Int) {
        viewModelScope.launch{
            try{
                if(_listSize.value!! < offset) return@launch
                _articleList.value = service.getArticlesByBoard(board_id, offset, limit).results!!
                _listSize.value = service.getArticlesByBoard(board_id, offset, limit).count
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }


}