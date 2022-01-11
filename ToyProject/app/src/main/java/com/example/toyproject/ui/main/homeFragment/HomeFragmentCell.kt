package com.example.toyproject.ui.main.homeFragment

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.R

// 홈프래그먼트의 여러 셀을 위한 Custom View
class HomeFragmentCell : LinearLayout {

    lateinit var topLayout : LinearLayout
    lateinit var title : TextView
    private lateinit var more : TextView
    lateinit var recyclerView : RecyclerView

    // 임시
    lateinit var preparing : TextView

    constructor(context: Context?) : super(context){
        init(context)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init(context)
        getAttrs(attrs)

    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init(context)
        getAttrs(attrs,defStyleAttr)
    }

    private fun init(context:Context?){
        val view = LayoutInflater.from(context).inflate(R.layout.home_fragment_cell, this,false)
        addView(view)

        topLayout = findViewById(R.id.home_fragment_cell_top_layout)
        title = findViewById(R.id.home_fragment_cell_title)
        more =  findViewById(R.id.home_fragment_cell_more)
        recyclerView = findViewById(R.id.home_fragment_cell_recycler_view)

        // 임시
        preparing =  findViewById(R.id.preparing)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HomeFragmentCell)
        setTypeArray(typedArray)
    }
    private fun getAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HomeFragmentCell, defStyleAttr, 0)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray : TypedArray) {
        title.text = typedArray.getText(R.styleable.HomeFragmentCell_title)
        topLayout.isClickable = typedArray.getBoolean(R.styleable.HomeFragmentCell_titleClickable, true)
        topLayout.isFocusable = typedArray.getBoolean(R.styleable.HomeFragmentCell_titleClickable, true)
        more.visibility = typedArray.getInt(R.styleable.HomeFragmentCell_moreVisible, INVISIBLE)
        recyclerView.visibility = typedArray.getInt(R.styleable.HomeFragmentCell_recyclerViewVisible, INVISIBLE)

        // 임시
        preparing.visibility = typedArray.getInt(R.styleable.HomeFragmentCell_preparingVisible, GONE)


        typedArray.recycle()
    }

}