package com.myapp.aries.chatapp.view

import android.content.Context

interface LoginView {

    fun navigateToChat()
    fun showConnectingFail()
    fun showEmptyNameFail()

    fun startAsyncProgress()
    fun endAsyncProgress()
}