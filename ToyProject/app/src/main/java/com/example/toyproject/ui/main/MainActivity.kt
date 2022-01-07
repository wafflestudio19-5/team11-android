package com.example.toyproject.ui.main

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityMainBinding
import com.example.toyproject.network.Service
import com.example.toyproject.ui.article.ArticleActivity
import com.example.toyproject.ui.login.LoginActivity
import com.example.toyproject.ui.main.homeFragment.HomeSettingActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.lang.IllegalStateException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var service: Service

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var bridge: SettingUpdate

    private lateinit var binding: ActivityMainBinding
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
        viewPager.isUserInputEnabled = false

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
    // 홈 화면에서 더보기 눌렀을 때 그곳으로 이동(viewPager)
    fun moveToTab(idx : Int) {
        viewPager.setCurrentItem(idx, false)
    }

    // 준비중입니다 띄우는 함수(임시)
    fun preparing() {
        Toast.makeText(this, "준비중입니다", Toast.LENGTH_SHORT).show()
    }

    private val resultListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                viewPager.adapter?.notifyDataSetChanged()
                bridge.update()
            }
        }

    // homeFragment 에서 setting 창 띄우기
    fun openHomeSetting() {
        val intent = Intent(this, HomeSettingActivity::class.java)
        resultListener.launch(intent)
    }
    // homeFragment 에서 article item 선택하면 ArticleActivity 시작
    fun openArticle(board_id : Int, article_id : Int, board_name : String) {
        Intent(this, ArticleActivity::class.java).apply{
            putExtra("board_id", board_id)
            putExtra("article_id", article_id)
            putExtra("board_name", board_name)
        }.run{startActivity(this)}
    }

    // HomeFragment setting 부분
    interface SettingUpdate {
        fun update()
    }
    fun updater(bridge : SettingUpdate) {
        this.bridge = bridge
    }
}