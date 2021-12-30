package com.example.toyproject.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.BoardService
import com.example.toyproject.network.dto.Board
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val service: BoardService
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

    private val defaultTempList = mutableListOf<Board>()
    private val careerTempList = mutableListOf<Board>()
    private val promotionTempList = mutableListOf<Board>()
    private val organizationTempList = mutableListOf<Board>()
    private val departmentTempList = mutableListOf<Board>()
    private val generalTempList = mutableListOf<Board>()

    fun getBoardList(){
        viewModelScope.launch {
            _boardList.value = service.getBoardList().boards
            classifyBoardList()
            setBoardList()
        }
    }

    private fun classifyBoardList(){
        for(board in _boardList.value!!){
            when (board.type) {
                0 -> {
                    defaultTempList.add(board)
                }
                1 -> {
                    careerTempList.add(board)
                }
                2 -> {
                    promotionTempList.add(board)
                }
                3 -> {
                    organizationTempList.add(board)
                }
                4 -> {
                    departmentTempList.add(board)
                }
                5 -> {
                    generalTempList.add(board)
                }
                else ->{
                    Timber.d("error")
                }
            }


        }
    }

    private fun setBoardList(){
        _defaultBoardList.value = defaultTempList
        _careerBoardList.value = careerTempList
        _promotionBoardList.value = promotionTempList
        _organizationBoardList.value = organizationTempList
        _departmentBoardList.value = departmentTempList
        _generalBoardList.value = generalTempList


    }


}