package com.example.toyproject.ui.main.homeFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.BoardService
import com.example.toyproject.network.dto.Board
import com.example.toyproject.network.dto.FetchAllBoard
import com.example.toyproject.network.dto.MyArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val service: BoardService
) : ViewModel() {

    private val _hotArticleList = MutableLiveData<MutableList<MyArticle>>()
    val hotArticleList: LiveData<MutableList<MyArticle>> = _hotArticleList

    private val _issueArticleList = MutableLiveData<MutableList<MyArticle>>()
    val issueArticleList: LiveData<MutableList<MyArticle>> = _issueArticleList

    private val _favorBoards = MutableLiveData<List<Board>>()
    val favorBoards : LiveData<List<Board>> = _favorBoards

    private val _favorBoardsTitle = MutableLiveData<List<String>>()
    val favorBoardsTitle : LiveData<List<String>> = _favorBoardsTitle

    var names :  MutableList<String> = mutableListOf()
    var titles : MutableList<String> = mutableListOf()

    // 즐겨찾기한 게시판 불러오기
    // 1. 먼저 즐겨찾기한 board 목록을 불러온다.
    fun loadFavorite() {
        viewModelScope.launch {
            try {
                _favorBoards.value = service.searchFavoriteBoard().boards!!
            }catch (e:IOException) {
                Timber.d(e)
            }
        }
    }
    // 2. 불러온 board 목록에서 id 만 추출해서, 첫 글의 제목만 로딩한다.
    fun loadFavoriteTitles(list : List<Board>) {
        names = mutableListOf()
        titles = mutableListOf()
        viewModelScope.launch {
            val iterator = list.iterator()
            while(iterator.hasNext()) {
                val board = iterator.next()
                names.add(board.name)
                loadTitle(board.id)
            }
        }
    }
    private fun loadTitle(id : Int){
        viewModelScope.launch {
            try {
                val titleGot : String? = service.getArticlesByBoard(id, 0, 1).results?.get(0)?.title
                if(titleGot==null) titles.add("게시글이 없습니다")
                else titles.add(titleGot)
            } catch (e : Exception) {
                titles.add("게시글이 없습니다.")
            }
            if(titles.size==_favorBoards.value!!.size) {
                _favorBoardsTitle.value = titles
            }
        }
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