package com.example.toyproject.ui.main.homeFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.BoardService
import com.example.toyproject.network.RecentReviewItem
import com.example.toyproject.network.ReviewService
import com.example.toyproject.network.dto.Board
import com.example.toyproject.network.dto.MyArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val service: BoardService,
    private val boardService: ReviewService
) : ViewModel() {

    private val _hotArticleList = MutableLiveData<MutableList<MyArticle>>()
    val hotArticleList: LiveData<MutableList<MyArticle>> = _hotArticleList

    // 실시간 인기 글 관련
    private val _issueArticleList = MutableLiveData<MutableList<MyArticle>>()
    val issueArticleList: LiveData<MutableList<MyArticle>> = _issueArticleList
    var issueBoardNames :  MutableList<String> = mutableListOf()
    private val _issueArticleBoardNameList = MutableLiveData<MutableList<String>>()
    val issueArticleBoardNameList: LiveData<MutableList<String>> = _issueArticleBoardNameList


    // 즐겨찾기 한 board 서버 응답 저장
    private val _favorBoards = MutableLiveData<List<Board>>()
    val favorBoards : LiveData<List<Board>> = _favorBoards

    // 즐겨찾기 한 board 의 id 별로 불러온 첫 번째 게시글의 title 저장
    private val _favorBoardsTitle = MutableLiveData<List<String>>()
    val favorBoardsTitle : LiveData<List<String>> = _favorBoardsTitle

    // 최근 강의평
    private val _recentReviewList = MutableLiveData<List<RecentReviewItem>>()
    val recentReviewList : LiveData<List<RecentReviewItem>> = _recentReviewList

    var boardNames :  MutableList<String> = mutableListOf()
    var articleIds : MutableList<Int> = mutableListOf()
    var boardIds : MutableList<Int> = mutableListOf()
    var titles : MutableList<String> = mutableListOf()

    // 즐겨찾기한 게시판 불러오기
    // 1. 즐겨찾기한 board 목록을 불러온다.
    fun loadFavorite() {
        viewModelScope.launch {
            try {
                val favorBoards : List<Board>? = service.searchFavoriteBoard().boards
                _favorBoards.value = favorBoards!!
            // 만약 즐겨찾기 한 게시판이 없어서 404 에러 나면, 빈 List 를 liveData 에 저장
            } catch (e: Exception) { _favorBoards.value = listOf() }}
    }
    // 2. 불러온 board 목록에서 id 만 추출해서, 첫 글의 제목만 로딩한다.
    fun loadFavoriteTitles(list : List<Board>) {
        boardNames = mutableListOf()
        articleIds = mutableListOf()
        boardIds = mutableListOf()
        titles = mutableListOf()
        for(i in list.indices) {
            boardNames.add("")
            articleIds.add(-1)
            boardIds.add(-1)
            titles.add("")
        }
        // 각 id 마다 게시글 로드 (새 coroutine 열어서)
        viewModelScope.launch {
            var idx = 0
            val iterator = list.iterator()
            while(iterator.hasNext()) {
                val board = iterator.next()
                boardNames[idx] = board.name
                boardIds[idx] = board.id
                loadTitle(idx, board.id)
                idx += 1
            }
        }
    }
    // 3. 각 id 마다 게시판의 첫 게시글 제목 로드
    private fun loadTitle(idx : Int, id : Int){
        viewModelScope.launch {
            try {
                val gotArticle = service.getArticlesByBoard(id, 0, 1).results?.get(0)
                val titleGot : String? = gotArticle?.title
                val idGot : Int = gotArticle!!.id
                if(titleGot==null) {
                    titles[idx] = "게시글이 없습니다"
                    articleIds[idx] = -1
                }
                else {
                    titles[idx] = titleGot
                    articleIds[idx] = idGot
                }
            } catch (e : Exception) {
                // 게시판이 비어있어서 404 에러 뜨면 문구 삽입
                titles[idx] = "게시글이 없습니다."
                articleIds[idx] = -1
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
            } catch (e: Exception) {
                // 해당 글이 없어서 404 에러 시 빈 리스트
                _issueArticleList.value = mutableListOf()
            }
        }
    }
    // 각 이슈 글마다 board id 를 이용해서 board name 을 불러온다.
    fun loadIssueBoardName(issueArticles : MutableList<MyArticle>) {
        issueBoardNames = mutableListOf()
        for(issue in issueArticles) {
            viewModelScope.launch {
                issueBoardNames.add(service.getBoardInfo(issue.board_id).name)
                // 다 불러왔으면 liveDate 변경
                if(issueBoardNames.size==_issueArticleList.value?.size) {
                    _issueArticleBoardNameList.value = issueBoardNames
                }
            }
        }
    }

    // 핫게 게시글 (5개만)불러오기
    fun loadHot(offset: Int, limit: Int, interest: String) {
        viewModelScope.launch{
            try{
                _hotArticleList.value = service.getHotBestArticle(offset, limit, interest).results!!
            } catch (e: Exception) {
                // 해당 글이 없어서 404 에러 시 빈 리스트
                _hotArticleList.value = mutableListOf()
            }
        }
    }

    // 최근 강의평 (4개) 불러오기
    fun loadRecentReview() {
        viewModelScope.launch {
            try{
                _recentReviewList.value = boardService.getRecentReview(0, 4).results
            } catch (e : HttpException) {

            }

        }

    }

}