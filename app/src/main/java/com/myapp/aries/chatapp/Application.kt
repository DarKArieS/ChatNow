package com.myapp.aries.chatapp

import android.app.Application
import timber.log.Timber

class CustomApplication : Application() {
    companion object {
        var myApplication : CustomApplication? = null
    }

    override fun onCreate(){
        super.onCreate()
        println("custom application")
        myApplication = this
        Timber.plant(Timber.DebugTree())
    }

    fun getInstance():CustomApplication{
        return myApplication!!
    }

}