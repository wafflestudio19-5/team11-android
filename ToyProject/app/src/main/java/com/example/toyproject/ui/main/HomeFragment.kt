package com.example.toyproject.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.toyproject.R
import com.example.toyproject.databinding.FragmentHomeBinding
import com.example.toyproject.network.dto.MyArticle
import com.example.toyproject.ui.board.HotBestBoardActivity
import com.example.toyproject.ui.main.homeFragment.*
import com.example.toyproject.ui.profile.UserActivity
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: FragmentHomeBinding
    private lateinit var banner : ViewPager2

    private val viewModel: HomeFragmentViewModel by activityViewModels()

    private lateinit var hotAdapter : HomeHotRecyclerViewAdapter
    private lateinit var hotLayoutManager: LinearLayoutManager

    private lateinit var favorAdapter : HomeFavoriteRecyclerViewAdapter
    private lateinit var favorLayoutManager: LinearLayoutManager

    private lateinit var issueAdapter : HomeIssueRecyclerViewAdapter
    private lateinit var issueLayoutManager: LinearLayoutManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeScroll.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

        // setting 불러오기
        val defaultJsonArray  = JSONArray()
        for(i in 0..9) defaultJsonArray.put(true)
        val settingArr = sharedPreferences.getString("setting", defaultJsonArray.toString())
        val setting : ArrayList<String> = ArrayList()
        val arrJson = JSONArray(settingArr)
        for(i in 0 until arrJson.length()) {
            setting.add(arrJson.optString(i))
        }

        // 최상단 툴바
        val resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == AppCompatActivity.RESULT_OK) {
                    // (activity as MainActivity).finish()
                }
            }
        val toolbar = binding.toolbar
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_button -> {
                    true
                }
                R.id.profile_button -> {
                    val intent = Intent(activity, UserActivity::class.java)
                    resultListener.launch(intent)
                    true
                }
                else -> false
            }
        }
        // 맨 위 배너 부분
        binding.homeTopBanner.clipToPadding = false
        binding.homeTopBanner.offscreenPageLimit = 1
        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx //+ currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            val width = Resources.getSystem().displayMetrics.widthPixels
            page.translationX = (-width * 0.23 * position).toFloat()
        }
        binding.homeTopBanner.setPageTransformer(pageTransformer)
        banner = binding.homeTopBanner
        val bannerAdapter = HomeFragmentTopBannerAdapter(this)
        banner.adapter = bannerAdapter


        // 아이콘들
        binding.homeFragmentSchoolhomeButton.setOnClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra("url", "https://my.snu.ac.kr")
            intent.putExtra("title", "서울대학교")   // TODO
            startActivity(intent)
        }
        binding.homeFragmentStudyroomButton.setOnClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra("url", "http://libseat.snu.ac.kr/")
            intent.putExtra("title", "열람실별 실시간 좌석 정보")
            startActivity(intent)
        }
        binding.homeFragmentShuttleButton.setOnClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra("url", "https://www.snu.ac.kr/about/gwanak/shuttles/campus_shuttles")
            intent.putExtra("title", "교내순환 셔틀버스")
            startActivity(intent)
        }
        binding.homeFragmentNewsButton.setOnClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra("url", "https://www.snu.ac.kr/snunow/notice/genernal")
            intent.putExtra("title", "일반공지-공지사항-SNU NOW")
            startActivity(intent)
        }
        binding.homeFragmentCalenderButton.setOnClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra("url", "https://www.snu.ac.kr/academics/resources/calendar")
            intent.putExtra("title", "학사일정-학사행정-교육-서울대학교")
            startActivity(intent)
        }
        binding.homeFragmentLibraryButton.setOnClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra("url", "https://library.snu.ac.kr/")
            intent.putExtra("title", "SNUL")
            startActivity(intent)
        }

        // 즐겨찾는 게시판 (커스텀 뷰)
        if(setting[0] =="true") binding.homeFragmentCellFavorite.visibility = View.VISIBLE
        else binding.homeFragmentCellFavorite.visibility = View.GONE
        binding.homeFragmentCellFavorite.topLayout.setOnClickListener {
            (activity as MainActivity).moveToTab(2)
        }
        favorAdapter = HomeFavoriteRecyclerViewAdapter()
        favorLayoutManager = LinearLayoutManager(activity)
        binding.homeFragmentCellFavorite.recyclerView.apply {
            adapter = favorAdapter
            layoutManager = favorLayoutManager
        }
        // 즐겨찾는 게시판 내용 채우기
        viewModel.loadFavorite()
        viewModel.favorBoards.observe(this, {
            if(it.isEmpty()) binding.homeFragmentCellFavorite.visibility = View.GONE
            else viewModel.loadFavoriteTitles(it)
        })
        viewModel.favorBoardsTitle.observe(this, {
            favorAdapter.setFavoriteBoard(it, viewModel.boardIds, viewModel.articleIds, viewModel.boardNames)
        })
        // 즐겨찾는 게시판 아이템 클릭
        favorAdapter.setItemClickListener(object : HomeFavoriteRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(v: View, board_id : Int, article_id : Int, board_name : String, position: Int) {
                // -1 이라는 것은, 즐겨찾기 한 게시판에 아무 글도 없다는 뜻 (HomeFragmentViewModel 의 loadTitle() 참고)
                if(article_id!=-1) (activity as MainActivity).openBoard(board_id, board_name)
            }
        })


        // 실시간 인기 글 게시판
        if(setting[1] =="true") binding.homeFragmentCellIssue.visibility = View.VISIBLE
        else binding.homeFragmentCellIssue.visibility = View.GONE
        issueAdapter = HomeIssueRecyclerViewAdapter()
        issueLayoutManager = LinearLayoutManager(activity)
        binding.homeFragmentCellIssue.recyclerView.apply {
            adapter = issueAdapter
            layoutManager = issueLayoutManager
        }
        // 실시간 인기 글 게시판 내용 채우기
        viewModel.loadIssue()
        viewModel.issueArticleList.observe(this, {
            viewModel.loadIssueBoardName(it)
        })
        viewModel.issueArticleBoardNameList.observe(this, {
            issueAdapter.setIssue(viewModel.issueArticleList.value!!, it)
        })
        // 실시간 인기 글 게시판 아이템 클릭
        issueAdapter.setItemClickListener(object : HomeIssueRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: MyArticle, position: Int) {
                (activity as MainActivity).openArticle(data.board_id, data.id, viewModel.issueBoardNames[position])
            }
        })


        // 핫게 게시판
        if(setting[2] =="true") binding.homeFragmentCellHot.visibility = View.VISIBLE
        else binding.homeFragmentCellHot.visibility = View.GONE
        binding.homeFragmentCellHot.setOnClickListener {
            Intent(activity, HotBestBoardActivity::class.java).apply {
                putExtra("board_name", "HOT 게시판")
                putExtra("board_interest", "hot")
            }.run { startActivity(this) }
        }
        hotAdapter = HomeHotRecyclerViewAdapter()
        hotLayoutManager = LinearLayoutManager(activity)
        binding.homeFragmentCellHot.recyclerView.apply {
            adapter = hotAdapter
            layoutManager = hotLayoutManager
        }
        viewModel.loadHot(0, 4, "hot")
        viewModel.hotArticleList.observe(this, {
            hotAdapter.setHotArticles(it)
        })
        hotAdapter.setItemClickListener(object : HomeHotRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: MyArticle, position: Int) {
                (activity as MainActivity).openArticle(data.board_id, data.id, "HOT 게시판")
            }
        })


        // 교내 소식
        if(setting[3] =="true") binding.homeFragmentCellNews.visibility = View.VISIBLE
        else binding.homeFragmentCellNews.visibility = View.GONE

        // 진로
        if(setting[4] =="true") binding.homeFragmentCellCareer.visibility = View.VISIBLE
        else binding.homeFragmentCellCareer.visibility = View.GONE

        // 판매 중인 책
        if(setting[5] =="true") binding.homeFragmentCellBook.visibility = View.VISIBLE
        else binding.homeFragmentCellBook.visibility = View.GONE

        // 교내 홍보
        if(setting[6] =="true") binding.homeFragmentCellPromotion.visibility = View.VISIBLE
        else binding.homeFragmentCellPromotion.visibility = View.GONE

        // 최근 강의평
        if(setting[7] =="true") binding.homeFragmentCellLecture.visibility = View.VISIBLE
        else binding.homeFragmentCellLecture.visibility = View.GONE
        binding.homeFragmentCellLecture.setOnClickListener {
            // TODO
            (activity as MainActivity).preparing()
        }
        // 주변 맛집?

        // 답변을 기다리는 질문
        if(setting[9] =="true") binding.homeFragmentCellQuestion.visibility = View.VISIBLE
        else binding.homeFragmentCellQuestion.visibility = View.GONE


        // 맨 밑, 홈 화면 설정 버튼
        binding.homeFragmentSettingButton.setOnClickListener {
            (activity as MainActivity).openHomeSetting()
        }


        // 설정 변경되었으면 바로 적용하기
        (activity as MainActivity).updater(object : MainActivity.SettingUpdate {
            override fun update() {
                // setting 불러오기
                val defaultJsonArray2  = JSONArray()
                for(i in 0..9) defaultJsonArray2.put(true)
                val settingArr2 = sharedPreferences.getString("setting", defaultJsonArray2.toString())
                val setting2 : ArrayList<String> = ArrayList()
                val arrJson2 = JSONArray(settingArr2)
                for(i in 0 until arrJson2.length()) {
                    setting2.add(arrJson2.optString(i))
                }
                // 즐겨찾는 게시판
                if(setting2[0] =="true") binding.homeFragmentCellFavorite.visibility = View.VISIBLE
                else binding.homeFragmentCellFavorite.visibility = View.GONE

                // 실시간 인기 글
                if(setting2[1] =="true") binding.homeFragmentCellIssue.visibility = View.VISIBLE
                else binding.homeFragmentCellIssue.visibility = View.GONE

                // 핫게
                if(setting2[2] =="true") binding.homeFragmentCellHot.visibility = View.VISIBLE
                else binding.homeFragmentCellHot.visibility = View.GONE

                // 교내 소식
                if(setting2[3] =="true") binding.homeFragmentCellNews.visibility = View.VISIBLE
                else binding.homeFragmentCellNews.visibility = View.GONE

                // 진로
                if(setting2[4] =="true") binding.homeFragmentCellCareer.visibility = View.VISIBLE
                else binding.homeFragmentCellCareer.visibility = View.GONE

                // 판매 중인 책
                if(setting2[5] =="true") binding.homeFragmentCellBook.visibility = View.VISIBLE
                else binding.homeFragmentCellBook.visibility = View.GONE

                // 교내 홍보
                if(setting2[6] =="true") binding.homeFragmentCellPromotion.visibility = View.VISIBLE
                else binding.homeFragmentCellPromotion.visibility = View.GONE

                // 최근 강의평
                if(setting2[7] =="true") binding.homeFragmentCellLecture.visibility = View.VISIBLE
                else binding.homeFragmentCellLecture.visibility = View.GONE

                // 답변을 기다리는 질문
                if(setting2[9] =="true") binding.homeFragmentCellQuestion.visibility = View.VISIBLE
                else binding.homeFragmentCellQuestion.visibility = View.GONE

                binding.homeScroll.scrollTo(0, binding.homeTopBanner.top)
            }
        })

        binding.homeScroll.animation = null
        binding.homeScroll.layoutAnimation = null
        binding.homeScroll.isSmoothScrollingEnabled = false
    }
}