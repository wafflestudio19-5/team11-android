package com.example.toyproject.ui.main

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityMainBinding
import com.example.toyproject.network.Service
import com.example.toyproject.ui.article.ArticleActivity
import com.example.toyproject.ui.board.BoardActivity
import com.example.toyproject.ui.main.homeFragment.HomeSettingActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
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

        // 로그인 화면에서 진입할 때 페이드 인 효과
        overridePendingTransition(R.anim.slide_hold_fade_in, R.anim.slide_nothing)

        //하단 바
        viewPager = binding.pager
        tabLayout = binding.tabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) { tab?.position?.let { viewPager?.setCurrentItem(it, false) } }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

        val pagerAdapter = ViewpagerFragmentAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager){
                tab: TabLayout.Tab, i: Int ->
            tab.icon = when(i){
                0-> ResourcesCompat.getDrawable(resources, R.drawable.icn_tab_home_active, null)
                1-> ResourcesCompat.getDrawable(resources, R.drawable.icn_tab_timetable_active, null)
                2-> ResourcesCompat.getDrawable(resources, R.drawable.icn_tab_board_active, null)
                3-> ResourcesCompat.getDrawable(resources, R.drawable.icn_tab_notification_active, null)
                4-> ResourcesCompat.getDrawable(resources, R.drawable.icn_tab_campuspick_active, null)
                else-> throw IllegalStateException("no tab")
            }
        }.attach()

    }
    // 홈 화면에서 각 셀의 "더 보기"를 눌렀을 때 그 탭으로 이동(viewPager)
    fun moveToTab(idx : Int) {
        viewPager.setCurrentItem(idx, false)
    }

    // 준비중입니다 띄우는 함수(임시)
    fun preparing() {
        Toast.makeText(this, "준비중입니다", Toast.LENGTH_SHORT).show()
    }

    // homeFragment setting 결과 listener
    private val homeSettingResultListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                viewPager.adapter?.notifyDataSetChanged()
                bridge.update()
            }
        }
    // homeFragment 에서 setting 창 띄우기
    fun openHomeSetting() {
        val intent = Intent(this, HomeSettingActivity::class.java)
        homeSettingResultListener.launch(intent)
    }
    // homeFragment 에서 article item 선택하면 ArticleActivity 시작
    fun openArticle(board_id : Int, article_id : Int, board_name : String) {
        Intent(this, ArticleActivity::class.java).apply{
            putExtra("board_id", board_id)
            putExtra("article_id", article_id)
            putExtra("board_name", board_name)
        }.run{
            startActivity(this)
        }
    }

    // homeFragment 에서 즐겨찾는 게시판 item 선택하면 BoardActivity 시작
    fun openBoard(board_id : Int, board_name : String) {
        Intent(this, BoardActivity::class.java).apply{
            putExtra("board_id", board_id)
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

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)						// 태스크를 백그라운드로 이동
        finishAndRemoveTask()						// 액티비티 종료 + 태스크 리스트에서 지우기
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}