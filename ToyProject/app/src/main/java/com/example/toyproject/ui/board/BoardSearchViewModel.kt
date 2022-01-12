package com.example.toyproject.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.BoardService
import com.example.toyproject.network.dto.Board
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BoardSearchViewModel @Inject constructor(
    private val service: BoardService
): ViewModel() {

    private val _boards = MutableLiveData<List<Board>>()
    val boards : LiveData<List<Board>> = _boards

    private val _error = MutableLiveData<String>()
    val error : LiveData<String> = _error

    private val _noBoard = MutableLiveData<Boolean>()
    val noBoard : LiveData<Boolean> = _noBoard

    fun searchBoard(keyword: String){
        viewModelScope.launch {
            try {
                _boards.value = service.searchBoard(keyword).boards!!
                _noBoard.value = false
            } catch(e: HttpException){
                Timber.e(e)
                _noBoard.value = true
            }
        }
    }



}