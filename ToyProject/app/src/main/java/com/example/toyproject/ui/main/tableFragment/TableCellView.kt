package com.example.toyproject.ui.main.tableFragment

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView


class TableCellView : androidx.appcompat.widget.AppCompatTextView {


    lateinit var info : String


    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, id : Int) : super(context!!) {

    }
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)
}