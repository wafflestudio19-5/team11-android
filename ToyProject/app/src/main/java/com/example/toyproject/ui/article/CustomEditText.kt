package com.example.toyproject.ui.article

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText

@SuppressLint("AppCompatCustomView")
class CustomEditText(context: Context?, attrs: AttributeSet?) : EditText(context, attrs) {
    private lateinit var bridge : CustomEditToActivity
    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            bridge.call()
        }
        return false
    }
    interface CustomEditToActivity {
        fun call()
    }
    fun caller(bridge : CustomEditToActivity) {
        this.bridge = bridge
    }
}