package com.myapp.aries.chatapp.view

interface LoginView {

    fun navigateToChat(userID:Int, userName:String)
    fun showConnectingFail()
    fun startAsyncProgress()
    fun endAsyncProgress()

}