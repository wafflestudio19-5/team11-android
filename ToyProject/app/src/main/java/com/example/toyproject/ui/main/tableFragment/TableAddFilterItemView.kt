package com.example.toyproject.ui.main.tableFragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.toyproject.R

class TableAddFilterItemView : LinearLayout {


    lateinit var clickLayout : LinearLayout
    lateinit var title : TextView
    lateinit var favoriteStar : ImageView
    lateinit var checked : ImageView
    lateinit var next : ImageView
    var isFavorite : Boolean = false


    constructor(context: Context?) : super(context)
    constructor(context: Context?, title : String, favorite : Boolean=false,
                checked : Boolean=false, next : Boolean = true) : super(context){
        init(context, title, favorite, checked, next)
    }

    private fun init(context:Context?, title : String, favorite : Boolean, checked : Boolean, next : Boolean) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_table_add_filter, this,false)
        addView(view)

        this.clickLayout = view.findViewById(R.id.add_lecture_filter_layout)
        this.title = view.findViewById(R.id.add_lecture_filter_text)
        this.favoriteStar = view.findViewById(R.id.add_lecture_filter_favorite)
        this.checked = view.findViewById(R.id.add_lecture_filter_check)
        this.next = view.findViewById(R.id.add_lecture_filter_next)
        this.isFavorite = favorite

        this.title.text = title

        //즐겨찾기 한 item 이면 노란 별로 바꾸기
        if(favorite) {
            this.favoriteStar.setImageResource(R.drawable.icn_m_scrap_full_yellow)
        }
        else {
            this.favoriteStar.setImageResource(R.drawable.icn_m_scrap_gray400)
        }

        // 선택된 item 이면 배경 옅은 노란색, 제목은 BOLD
        if(checked) {
            this.setBackgroundColor(resources.getColor(R.color.light_yellow))
            this.title.setTypeface(null, Typeface.BOLD)
            this.checked.visibility = VISIBLE
        }
        else {
            this.setBackgroundColor(Color.TRANSPARENT)
            this.checked.visibility = GONE
        }

        if(next) {
            this.next.visibility = VISIBLE
        }
        else {
            this.next.visibility = GONE
        }

        this.favoriteStar.setOnClickListener {

        }
    }
    fun setView(favorite : Boolean, checked : Boolean) {
        //즐겨찾기 한 item 이면 노란 별로 바꾸기
        if(favorite) {
            isFavorite = true
            this.favoriteStar.setBackgroundResource(R.drawable.icn_m_scrap_full_yellow)
        }
        else {
            isFavorite = false
            this.favoriteStar.setBackgroundResource(R.drawable.icn_m_scrap_gray400)
        }

        // 선택된 item 이면 배경 옅은 노란색, 제목은 BOLD
        if(checked) {
            this.setBackgroundColor(resources.getColor(R.color.light_yellow))
            this.title.setTypeface(null, Typeface.BOLD)
            this.checked.visibility = VISIBLE
        }
        else {
            this.checked.visibility = GONE
            this.setBackgroundColor(resources.getColor(R.color.Surface))
        }
    }
}