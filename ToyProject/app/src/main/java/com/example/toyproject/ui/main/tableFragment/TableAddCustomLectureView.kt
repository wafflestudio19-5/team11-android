package com.example.toyproject.ui.main.tableFragment

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.toyproject.R

class TableAddCustomLectureView// TODO
    (context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    init {
        inflate(context, R.layout.item_make_custom_lecture, this)
        if (attrs != null) {
            val attributes =
                context.obtainStyledAttributes(attrs, R.styleable.TableAddCustomLectureView)
            attributes.recycle()
        }
    }
}