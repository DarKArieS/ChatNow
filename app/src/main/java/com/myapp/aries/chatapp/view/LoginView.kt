package com.myapp.aries.chatapp.view

import android.content.Context

interface LoginView {

    fun navigateToChat(userID:Int, userName:String)
    fun showConnectingFail()
    fun showEmptyNameFail()

    fun startAsyncProgress()
    fun endAsyncProgress()
}