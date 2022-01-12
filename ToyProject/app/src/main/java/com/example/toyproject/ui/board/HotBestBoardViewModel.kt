package com.example.toyproject.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.BoardService
import com.example.toyproject.network.dto.MyArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HotBestBoardViewModel @Inject constructor(
    private val service: BoardService
): ViewModel() {

    private val _articleList = MutableLiveData<MutableList<MyArticle>>()
    val articleList: LiveData<MutableList<MyArticle>> = _articleList

    private val _listSize = MutableLiveData<Int>(1)
    val listSize: LiveData<Int> = _listSize

    fun getArticleList(offset: Int, limit: Int, interest: String) {
        viewModelScope.launch{
            try{
                if(_listSize.value!! < offset) return@launch
                _articleList.value = service.getHotBestArticle(offset, limit, interest).results!!
                _listSize.value = service.getHotBestArticle(offset, limit, interest).count
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

}