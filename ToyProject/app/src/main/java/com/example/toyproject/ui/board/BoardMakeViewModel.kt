package com.example.toyproject.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.BoardService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class BoardMakeViewModel @Inject constructor(
    private val service: BoardService
) : ViewModel(){

    private val _error = MutableLiveData<String>()
    val error : LiveData<String> = _error

    fun createBoard(name: String, desc: String, isAnonymous: Boolean){
        viewModelScope.launch {
            try{
                _error.value = service.createBoard(name, desc, isAnonymous, 5).error!!
            } catch(e: HttpException){

            } catch(e: NullPointerException){
                _error.value = ""
            }
        }
    }

}