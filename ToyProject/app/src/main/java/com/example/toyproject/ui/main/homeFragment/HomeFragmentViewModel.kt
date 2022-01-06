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

    fun loadFavorite() {

    }

    fun loadIssue() {

    }

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