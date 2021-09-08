package com.example.printimin

import android.app.Application
import com.imin.printerlib.IminPrintUtils

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        IminPrintUtils.getInstance(this)
    }
}