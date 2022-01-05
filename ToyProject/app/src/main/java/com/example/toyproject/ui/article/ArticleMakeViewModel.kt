package com.example.toyproject.ui.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.BoardService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.lang.NullPointerException
import javax.inject.Inject

@HiltViewModel
class ArticleMakeViewModel @Inject constructor(
    private val service: BoardService
): ViewModel() {

    fun createArticle(board_id: Int, title: String, text: String, is_anonymous: Boolean, is_question: Boolean){
        viewModelScope.launch {
            try {
                service.createArticle(board_id, title, text, is_anonymous, is_question)
            } catch(e: HttpException){

            } catch(e: NullPointerException){
                Timber.e(e)
            }
        }
    }

}