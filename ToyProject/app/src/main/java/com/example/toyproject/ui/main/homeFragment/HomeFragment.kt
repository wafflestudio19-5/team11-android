package com.example.toyproject.ui.main.homeFragment

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.toyproject.ui.main.MainActivity
import com.example.toyproject.ui.profile.UserActivity
import com.example.toyproject.ui.review.ReviewActivity
import com.example.toyproject.ui.review.ReviewRecentAdapter
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

    private lateinit var recentAdapter : HomeRecentRecyclerViewAdapter
    private lateinit var recentLayoutManager: LinearLayoutManager


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


        // setting ????????????
        val defaultJsonArray  = JSONArray()
        for(i in 0..9) defaultJsonArray.put(true)
        val settingArr = sharedPreferences.getString("setting", defaultJsonArray.toString())
        val setting : ArrayList<String> = ArrayList()
        val arrJson = JSONArray(settingArr)
        for(i in 0 until arrJson.length()) {
            setting.add(arrJson.optString(i))
        }

        // ????????? ??????
        val resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == AppCompatActivity.RESULT_OK) {
                    (activity as MainActivity).finish()
                }
            }
        binding.homeFragmentUser.setOnClickListener {
            val intent = Intent(activity, UserActivity::class.java)
            resultListener.launch(intent)
        }


        // ??? ??? ?????? ??????
        binding.homeTopBanner.clipToPadding = false
        binding.homeTopBanner.offscreenPageLimit = 1

        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            val width = Resources.getSystem().displayMetrics.widthPixels
            page.translationX = (-width * 0.23 * position).toFloat()
        }
        binding.homeTopBanner.setPageTransformer(pageTransformer)
        banner = binding.homeTopBanner
        val bannerAdapter = HomeFragmentTopBannerAdapter(this)
        banner.adapter = bannerAdapter


        // ????????????
        binding.homeFragmentSchoolhomeButton.setOnClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra("url", "https://my.snu.ac.kr")
            intent.putExtra("title", "???????????????")   // TODO
            startActivity(intent)
        }
        binding.homeFragmentStudyroomButton.setOnClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra("url", "http://libseat.snu.ac.kr/")
            intent.putExtra("title", "???????????? ????????? ?????? ??????")
            startActivity(intent)
        }
        binding.homeFragmentShuttleButton.setOnClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra("url", "https://www.snu.ac.kr/about/gwanak/shuttles/campus_shuttles")
            intent.putExtra("title", "???????????? ????????????")
            startActivity(intent)
        }
        binding.homeFragmentNewsButton.setOnClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra("url", "https://www.snu.ac.kr/snunow/notice/genernal")
            intent.putExtra("title", "????????????-????????????-SNU NOW")
            startActivity(intent)
        }
        binding.homeFragmentCalenderButton.setOnClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra("url", "https://www.snu.ac.kr/academics/resources/calendar")
            intent.putExtra("title", "????????????-????????????-??????-???????????????")
            startActivity(intent)
        }
        binding.homeFragmentLibraryButton.setOnClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra("url", "https://library.snu.ac.kr/")
            intent.putExtra("title", "SNUL")
            startActivity(intent)
        }

        // ???????????? ????????? (????????? ???)
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
        // ???????????? ????????? ?????? ?????????
        viewModel.loadFavorite()
        viewModel.favorBoards.observe(viewLifecycleOwner) {
            if (it.isEmpty()) binding.homeFragmentCellFavorite.visibility = View.GONE
            else viewModel.loadFavoriteTitles(it)
        }
        viewModel.favorBoardsTitle.observe(viewLifecycleOwner) {
            favorAdapter.setFavoriteBoard(
                it, viewModel.boardIds, viewModel.articleIds, viewModel.boardNames
            )
        }
        // ???????????? ????????? ????????? ??????
        favorAdapter.setItemClickListener(object : HomeFavoriteRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(v: View, board_id : Int, article_id : Int, board_name : String, position: Int) {
                // -1 ????????? ??????, ???????????? ??? ???????????? ?????? ?????? ????????? ??? (HomeFragmentViewModel ??? loadTitle() ??????)
                if(article_id!=-1) (activity as MainActivity).openBoard(board_id, board_name)
            }
        })


        // ????????? ?????? ??? ?????????
        if(setting[1] =="true") binding.homeFragmentCellIssue.visibility = View.VISIBLE
        else binding.homeFragmentCellIssue.visibility = View.GONE
        issueAdapter = HomeIssueRecyclerViewAdapter()
        issueLayoutManager = LinearLayoutManager(activity)
        binding.homeFragmentCellIssue.recyclerView.apply {
            adapter = issueAdapter
            layoutManager = issueLayoutManager
        }
        // ????????? ?????? ??? ????????? ?????? ?????????
        viewModel.loadIssue()
        viewModel.issueArticleList.observe(viewLifecycleOwner) {
            viewModel.loadIssueBoardName(it)
        }
        viewModel.issueArticleBoardNameList.observe(viewLifecycleOwner) {
            issueAdapter.setIssue(viewModel.issueArticleList.value!!, it)
        }
        // ????????? ?????? ??? ????????? ????????? ??????
        issueAdapter.setItemClickListener(object : HomeIssueRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: MyArticle, position: Int) {
                (activity as MainActivity).openArticle(data.board_id, data.id, viewModel.issueBoardNames[position])
            }
        })

        // ?????? ?????????
        if(setting[2] =="true") binding.homeFragmentCellHot.visibility = View.VISIBLE
        else binding.homeFragmentCellHot.visibility = View.GONE
        binding.homeFragmentCellHot.topLayout.setOnClickListener {
            Intent(activity, HotBestBoardActivity::class.java).apply {
                putExtra("board_name", "HOT ?????????")
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
        viewModel.hotArticleList.observe(viewLifecycleOwner) {
            hotAdapter.setHotArticles(it)
        }
        hotAdapter.setItemClickListener(object : HomeHotRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: MyArticle, position: Int) {
                (activity as MainActivity).openArticle(data.board_id, data.id, "HOT ?????????")
            }
        })


        // ?????? ??????
        if(setting[3] =="true") binding.homeFragmentCellNews.visibility = View.VISIBLE
        else binding.homeFragmentCellNews.visibility = View.GONE

        // ??????
        if(setting[4] =="true") binding.homeFragmentCellCareer.visibility = View.VISIBLE
        else binding.homeFragmentCellCareer.visibility = View.GONE

        // ?????? ?????? ???
        if(setting[5] =="true") binding.homeFragmentCellBook.visibility = View.VISIBLE
        else binding.homeFragmentCellBook.visibility = View.GONE

        // ?????? ??????
        if(setting[6] =="true") binding.homeFragmentCellPromotion.visibility = View.VISIBLE
        else binding.homeFragmentCellPromotion.visibility = View.GONE

        // ?????? ?????????
        if(setting[7] =="true") binding.homeFragmentCellLecture.visibility = View.VISIBLE
        else binding.homeFragmentCellLecture.visibility = View.GONE
        binding.homeFragmentCellLecture.topLayout.setOnClickListener {
            val intent = Intent(activity, ReviewActivity::class.java)
            startActivity(intent)
        }
        recentAdapter = HomeRecentRecyclerViewAdapter(requireActivity())
        recentLayoutManager = LinearLayoutManager(activity)
        binding.homeFragmentCellLecture.recyclerView.apply {
            adapter = recentAdapter
            layoutManager = recentLayoutManager
        }
        viewModel.loadRecentReview()
        viewModel.recentReviewList.observe(viewLifecycleOwner) {
            recentAdapter.setReview(it.toMutableList())
        }


        // ?????? ???????

        // ????????? ???????????? ??????
        if(setting[9] =="true") binding.homeFragmentCellQuestion.visibility = View.VISIBLE
        else binding.homeFragmentCellQuestion.visibility = View.GONE


        // ??? ???, ??? ?????? ?????? ??????
        binding.homeFragmentSettingButton.setOnClickListener {
            (activity as MainActivity).openHomeSetting()
        }


        // ?????? ?????????????????? ?????? ????????????
        (activity as MainActivity).updater(object : MainActivity.SettingUpdate {
            override fun update() {
                // setting ????????????
                val defaultJsonArray2  = JSONArray()
                for(i in 0..9) defaultJsonArray2.put(true)
                val settingArr2 = sharedPreferences.getString("setting", defaultJsonArray2.toString())
                val setting2 : ArrayList<String> = ArrayList()
                val arrJson2 = JSONArray(settingArr2)
                for(i in 0 until arrJson2.length()) {
                    setting2.add(arrJson2.optString(i))
                }
                // ???????????? ?????????
                if(setting2[0] =="true") binding.homeFragmentCellFavorite.visibility = View.VISIBLE
                else binding.homeFragmentCellFavorite.visibility = View.GONE

                // ????????? ?????? ???
                if(setting2[1] =="true") binding.homeFragmentCellIssue.visibility = View.VISIBLE
                else binding.homeFragmentCellIssue.visibility = View.GONE

                // ??????
                if(setting2[2] =="true") binding.homeFragmentCellHot.visibility = View.VISIBLE
                else binding.homeFragmentCellHot.visibility = View.GONE

                // ?????? ??????
                if(setting2[3] =="true") binding.homeFragmentCellNews.visibility = View.VISIBLE
                else binding.homeFragmentCellNews.visibility = View.GONE

                // ??????
                if(setting2[4] =="true") binding.homeFragmentCellCareer.visibility = View.VISIBLE
                else binding.homeFragmentCellCareer.visibility = View.GONE

                // ?????? ?????? ???
                if(setting2[5] =="true") binding.homeFragmentCellBook.visibility = View.VISIBLE
                else binding.homeFragmentCellBook.visibility = View.GONE

                // ?????? ??????
                if(setting2[6] =="true") binding.homeFragmentCellPromotion.visibility = View.VISIBLE
                else binding.homeFragmentCellPromotion.visibility = View.GONE

                // ?????? ?????????
                if(setting2[7] =="true") binding.homeFragmentCellLecture.visibility = View.VISIBLE
                else binding.homeFragmentCellLecture.visibility = View.GONE

                // ????????? ???????????? ??????
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