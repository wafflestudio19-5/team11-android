package com.example.toyproject.ui.main.homeFragment

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
class HomeFragmentViewModel @Inject constructor(
    private val service: BoardService
) : ViewModel() {

    private val _hotArticleList = MutableLiveData<MutableList<MyArticle>>()
    val hotArticleList: LiveData<MutableList<MyArticle>> = _hotArticleList

    private val _issueArticleList = MutableLiveData<MutableList<MyArticle>>()
    val issueArticleList: LiveData<MutableList<MyArticle>> = _issueArticleList

    fun loadFavorite() {

    }

    // 실시간 인기 글 불러오기(2개)
    fun loadIssue() {
        viewModelScope.launch {
            try {
                _issueArticleList.value = service.getHotBestArticle(0, 2, "live").results!!
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

    // 핫게 게시글 (5개만)불러오기
    fun loadHot(offset: Int, limit: Int, interest: String) {
        viewModelScope.launch{
            try{
                _hotArticleList.value = service.getHotBestArticle(offset, limit, interest).results!!
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
    }

}