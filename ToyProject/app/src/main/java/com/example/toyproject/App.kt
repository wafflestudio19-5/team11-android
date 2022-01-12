package com.example.toyproject

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import kotlin.system.exitProcess

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this,getString(R.string.kakao_app_key))

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())

        }
    }


}