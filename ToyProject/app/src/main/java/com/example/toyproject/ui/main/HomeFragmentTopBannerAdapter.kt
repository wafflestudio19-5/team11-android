package com.example.toyproject.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.toyproject.R


class HomeFragmentTopBannerAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        val fragment = HomeFragmentTopBannerObjectFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt(ARG_OBJECT, position + 1)
        }
        return fragment
    }
}

private const val ARG_OBJECT = "object"

// Instances of this class are fragments representing a single
// object in our collection.
class HomeFragmentTopBannerObjectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home_top_banner_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {

            if(getInt(ARG_OBJECT)==1) {
                view.findViewById<TextView>(R.id.home_fragment_banner_title).text = "시간표마법사로 플랜B 준비"
                view.findViewById<TextView>(R.id.home_fragment_banner_content).text = "수강신청 망했을 때 당황하지 않기!"
            }
            else if(getInt(ARG_OBJECT)==1) {
                view.findViewById<TextView>(R.id.home_fragment_banner_title).text = "오늘의 할일"
                view.findViewById<TextView>(R.id.home_fragment_banner_content).text = "1월 5일 (수)"
                view.findViewById<TextView>(R.id.home_fragment_banner_content).setTextColor(Color.parseColor("#84BBFF"))
            }
            else if(getInt(ARG_OBJECT)==2) {
                view.findViewById<TextView>(R.id.home_fragment_banner_title).text = "방학에 뭐하지?"
                view.findViewById<TextView>(R.id.home_fragment_banner_content).text = "유익한 공모전, 대외활동 찾아보기"
                view.findViewById<TextView>(R.id.home_fragment_banner_content).setTextColor(Color.parseColor("#FFD982"))
            }
        }
    }
}