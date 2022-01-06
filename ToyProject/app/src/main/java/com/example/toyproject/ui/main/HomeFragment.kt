package com.example.toyproject.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.toyproject.R
import com.example.toyproject.databinding.FragmentHomeBinding
import com.example.toyproject.ui.board.HotBestBoardActivity
import com.example.toyproject.ui.profile.UserActivity
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.abs


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var banner : ViewPager2

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

        val toolbar = binding.toolbar

        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.search_button->{
                    true
                }
                R.id.profile_button->{
                    val intent = Intent(activity, UserActivity::class.java)
                    startActivity(intent)
                    true
                }
                else->false
            }
        }
        // 맨 위 배너 부분
        binding.homeTopBanner.clipToPadding = false
        binding.homeTopBanner.offscreenPageLimit = 1
        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
        }
        binding.homeTopBanner.setPageTransformer(pageTransformer)
        banner = binding.homeTopBanner
        val bannerAdapter = HomeFragmentTopBannerAdapter(this)
        banner.adapter = bannerAdapter

        // 즐겨찾는 게시판 더보기 (누르면 세 번째 탭으로 이동)
        binding.homeFragmentGotoFavoriteButton.setOnClickListener {
            (activity as MainActivity).moveToTab(2)
        }

        // 핫게 더보기 (누르면 핫게 액티비티 실행)
        binding.homeFragmentGotoHotButton.setOnClickListener {
            Intent(activity, HotBestBoardActivity::class.java).apply{
                putExtra("board_name", "HOT 게시판")
                putExtra("board_interest", "hot")
            }.run{startActivity(this)}
        }

        // 강의평 이동
        binding.homeFragmentGotoLecturesButton.setOnClickListener {
            // TODO
            (activity as MainActivity).preparing()
        }

    }



}