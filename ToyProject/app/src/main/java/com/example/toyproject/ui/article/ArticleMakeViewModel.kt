package com.example.toyproject.ui.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.BoardService
import com.example.toyproject.network.dto.MultiMap
import okhttp3.RequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException
import timber.log.Timber
import java.lang.NullPointerException
import javax.inject.Inject

@HiltViewModel
class ArticleMakeViewModel @Inject constructor(
    private val service: BoardService
): ViewModel() {

    fun createArticle(id: Int, hashMap: HashMap<String, RequestBody>,texts: MutableList<MultipartBody.Part>, images: MutableList<MultipartBody.Part>){
        viewModelScope.launch {
            try {
                service.createArticle(id, hashMap, texts, images)
            } catch(e: HttpException){
                Timber.e(e)
            } catch(e: NullPointerException){
                Timber.e(e)
            }
        }
    }

}