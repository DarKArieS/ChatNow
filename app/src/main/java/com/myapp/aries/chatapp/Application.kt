package com.myapp.aries.chatapp

import android.app.Application

class CustomApplication : Application() {
    companion object {
        var myApplication : CustomApplication? = null
    }

    override fun onCreate(){
        super.onCreate()
        println("custom application")
        myApplication = this
    }

    fun getInstance():CustomApplication{
        return myApplication!!
    }

}