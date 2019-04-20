package com.myapp.aries.chatapp.view

interface ChatView{
    fun sendMessage()
    fun receiveNewMessage()
    fun showConnectingFail()

    fun startAsyncProgress()
    fun endAsyncProgress()

}