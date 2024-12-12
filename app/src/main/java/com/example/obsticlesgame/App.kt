package com.example.obsticlesgame

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
    }
}