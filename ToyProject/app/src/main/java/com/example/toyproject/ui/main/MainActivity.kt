package com.example.toyproject.ui.main

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityMainBinding
import com.example.toyproject.network.Service
import com.example.toyproject.ui.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.IllegalStateException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var service: Service

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var mGoogleSignInClient : GoogleSignInClient
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //하단 바
        viewPager = binding.pager
        tabLayout = binding.tabLayout

        val pagerAdapter = ViewpagerFragmentAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager){
                tab: TabLayout.Tab, i: Int ->
            tab.icon = when(i){
                0-> ResourcesCompat.getDrawable(resources, R.drawable.outline_home_24, null)
                1-> ResourcesCompat.getDrawable(resources, R.drawable.outline_table_chart_24, null)
                2-> ResourcesCompat.getDrawable(resources, R.drawable.outline_list_alt_24, null)
                3-> ResourcesCompat.getDrawable(resources, R.drawable.outline_notifications_24, null)
                4-> ResourcesCompat.getDrawable(resources, R.drawable.outline_policy_24, null)
                else-> throw IllegalStateException("no tab")
            }
        }.attach()

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