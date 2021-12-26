package com.example.toyproject.ui.main

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityMainBinding
import com.example.toyproject.network.Service
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.IllegalStateException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var service: Service

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


    }
}