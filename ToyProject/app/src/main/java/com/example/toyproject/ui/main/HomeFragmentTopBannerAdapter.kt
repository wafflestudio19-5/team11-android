package com.example.toyproject.ui.main

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.toyproject.R
import java.sql.Time
import java.time.LocalDate
import java.util.*


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

    val weekDay = listOf("", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home_top_banner_fragment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {

            if(getInt(ARG_OBJECT)==1) {
                view.findViewById<TextView>(R.id.home_fragment_banner_title).text = "시간표마법사로 플랜B 준비"
                view.findViewById<TextView>(R.id.home_fragment_banner_content).text = "수강신청 망했을 때 당황하지 않기!"
                view.findViewById<ImageView>(R.id.home_fragment_banner_icon_image).setImageResource(R.drawable.icn_mcb_home_bulb)
            }
            else if(getInt(ARG_OBJECT)==2) {
                view.findViewById<TextView>(R.id.home_fragment_banner_title).text = "오늘의 할일"
                val date = StringBuilder()
                date.append(LocalDate.now().month.value, "월 ",   LocalDate.now().dayOfMonth, "일 ", weekDay[LocalDate.now().dayOfWeek.value])
                view.findViewById<TextView>(R.id.home_fragment_banner_content).text = date

                view.findViewById<TextView>(R.id.home_fragment_banner_content).setTextColor(Color.parseColor("#84BBFF"))
                view.findViewById<ImageView>(R.id.home_fragment_banner_icon_image).setImageResource(R.drawable.icn_mcb_home_todo)
            }
            else if(getInt(ARG_OBJECT)==3) {
                view.findViewById<TextView>(R.id.home_fragment_banner_title).text = "방학에 뭐하지?"
                view.findViewById<TextView>(R.id.home_fragment_banner_content).text = "유익한 공모전, 대외활동 찾아보기"
                view.findViewById<TextView>(R.id.home_fragment_banner_content).setTextColor(Color.parseColor("#FFD982"))
                view.findViewById<ImageView>(R.id.home_fragment_banner_icon_image).setImageResource(R.drawable.icn_mcb_home_bulb)
            }
        }
    }
}