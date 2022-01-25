package com.example.toyproject.ui.main.tableFragment

import android.content.Context
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.LinearLayout
import com.example.toyproject.R

class TableAddFilterCheckboxItemView : LinearLayout {

    lateinit var checkBox : CheckBox
    lateinit var clickLayout : LinearLayout


    constructor(context: Context?) : super(context)
    constructor(context: Context?, title : String) : super(context){
        init(context, title)
    }

    private fun init(context:Context?, title : String) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_table_filter_checkbox, this,false)
        addView(view)

        this.clickLayout = view.findViewById(R.id.filter_checkbox_layout)
        this.checkBox = view.findViewById(R.id.filter_checkbox)
        this.checkBox.text = title
    }
}