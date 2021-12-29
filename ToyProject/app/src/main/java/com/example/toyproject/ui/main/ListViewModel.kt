package com.example.toyproject.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.BoardService
import com.example.toyproject.network.dto.Board
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val service: BoardService,
) : ViewModel(){

    private val _boardList = MutableLiveData<List<Board>>()
    val boardList: LiveData<List<Board>> = _boardList

    private val _defaultBoardList = MutableLiveData<MutableList<Board>>()
    val defaultBoardList: LiveData<MutableList<Board>> = _defaultBoardList

    private val _careerBoardList = MutableLiveData<MutableList<Board>>()
    val careerBoardList: LiveData<MutableList<Board>> = _careerBoardList

    private val _promotionBoardList = MutableLiveData<MutableList<Board>>()
    val promotionBoardList: LiveData<MutableList<Board>> = _promotionBoardList

    private val _organizationBoardList = MutableLiveData<MutableList<Board>>()
    val organizationBoardList: LiveData<MutableList<Board>> = _organizationBoardList

    private val _departmentBoardList = MutableLiveData<MutableList<Board>>()
    val departmentBoardList: LiveData<MutableList<Board>> = _departmentBoardList

    private val _generalBoardList = MutableLiveData<MutableList<Board>>()
    val generalBoardList: LiveData<MutableList<Board>> = _generalBoardList



    fun getBoardList(){
        viewModelScope.launch {
            _boardList.value = service.getBoardList()
        }
    }

    fun classifyBoardList(){
        for(board in _boardList.value!!){
            when (board.type) {
                0 -> {
                    _defaultBoardList.value?.add(board)
                }
                1 -> {
                    _careerBoardList.value?.add(board)
                }
                2 -> {
                    _careerBoardList.value?.add(board)
                }
                3 -> {
                    _careerBoardList.value?.add(board)
                }
                4 -> {
                    _careerBoardList.value?.add(board)
                }
                5 -> {
                    _careerBoardList.value?.add(board)
                }
            }


        }
    }


}