package com.myapp.aries.chatapp.view

interface LoginView {

    fun navigateToChat(userID:Int)
    fun connectingFail()
    fun startProgress()
    fun endProgress()

}