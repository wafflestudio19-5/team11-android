package com.example.toyproject.ui.main.tableFragment

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService

@SuppressLint("AppCompatCustomView")
class CustomEditTextForQuery(context: Context?, attrs: AttributeSet?) : EditText(context, attrs) {
    private lateinit var bridge : HideKeyboard

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            bridge.hide()
            this.clearFocus()
            return true
        }
        return false
    }

    interface HideKeyboard {
        fun hide()
    }
    fun set(bridge : HideKeyboard) {
        this.bridge = bridge
    }
}