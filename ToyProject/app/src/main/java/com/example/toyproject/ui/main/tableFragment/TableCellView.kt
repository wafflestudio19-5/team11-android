package com.example.toyproject.ui.main.tableFragment

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.toyproject.R


class TableCellView : LinearLayout {


    var info : Int = 0

    lateinit var topLayout : LinearLayout
    lateinit var title : TextView
    lateinit var location : TextView


    constructor(context: Context?) : super(context!!) {
        val view = LayoutInflater.from(context).inflate(R.layout.table_cell_view, this,false)
        addView(view)

        topLayout = findViewById(R.id.cell_top_layout)
        title = findViewById(R.id.cell_title)
        location = findViewById(R.id.cell_location)
    }
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {

    }
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle) {

    }
    private fun init(context:Context?){

    }
}