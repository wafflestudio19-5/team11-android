package com.example.toyproject.ui.main.tableFragment

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TextView
import com.example.toyproject.R

class TableAddFilterHistoryItemView : LinearLayout {

    lateinit var queryText : TextView
    lateinit var field : TextView
    lateinit var clickLayout : LinearLayout


    constructor(context: Context?) : super(context)
    constructor(context: Context?, title : String, field: String) : super(context){
        init(context, title, field)
    }

    private fun init(context: Context?, title : String, field : String) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_table_filter_query_history, this,false)
        addView(view)

        this.clickLayout = view.findViewById(R.id.filter_query_history_layout)
        this.queryText =  view.findViewById(R.id.filter_query_history_text)
        this.field =  view.findViewById(R.id.filter_query_history_field)

        this.queryText.text = title
        this.field.text = field
    }


    private var listener: OnClickListener? = null

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if (listener != null) listener!!.onClick(this)
        }
        return super.dispatchTouchEvent(event)
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP && (event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER || event.keyCode == KeyEvent.KEYCODE_ENTER)) {
            if (listener != null) listener!!.onClick(this)
        }
        return super.dispatchKeyEvent(event)
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        this.listener = listener
    }
}