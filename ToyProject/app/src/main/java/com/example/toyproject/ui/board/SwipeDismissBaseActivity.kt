package com.example.toyproject.ui.board

import android.view.MotionEvent

import android.view.GestureDetector

import android.os.Bundle
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.R


abstract class SwipeDismissBaseActivity : AppCompatActivity() {
    private var gestureDetector: GestureDetector? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gestureDetector = GestureDetector(SwipeDetector())
    }

    private inner class SwipeDetector : SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {

            // Check movement along the Y-axis. If it exceeds SWIPE_MAX_OFF_PATH,
            // then dismiss the swipe.
            if (Math.abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) return false

            // Swipe from left to right.
            // The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE)
            // and a certain velocity (SWIPE_THRESHOLD_VELOCITY).
            if (e2.x - e1.x > SWIPE_MIN_DISTANCE) {

                finish()
                overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_left)
                return true
            }
            return false
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // TouchEvent dispatcher.
        if (gestureDetector != null) {
            if (gestureDetector!!.onTouchEvent(ev)) // If the gestureDetector handles the event, a swipe has been
            // executed and no more needs to be done.
                return true
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector!!.onTouchEvent(event)
    }

    companion object {
        private const val SWIPE_MIN_DISTANCE = 120
        private const val SWIPE_MAX_OFF_PATH = 250
        private const val SWIPE_THRESHOLD_VELOCITY = 200
    }
}

