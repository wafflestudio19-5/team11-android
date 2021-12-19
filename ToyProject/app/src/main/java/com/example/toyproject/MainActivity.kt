package com.example.toyproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.toyproject.databinding.ActivityMainBinding
import com.example.toyproject.network.Service
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var service: Service

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scope = CoroutineScope(Dispatchers.IO)

        var response: String = "Hi"

        /*
        val coroutine = scope.launch {
            try{
                response = service.getStatusCode().detail
                Timber.d("This is response from server: $response")
            } catch(e: Exception) {
                Timber.e(e)
            }

        }

         */

        //binding.statusCodeText.text = response

    }
}