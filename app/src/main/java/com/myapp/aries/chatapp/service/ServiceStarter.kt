package com.myapp.aries.chatapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class ServiceStarter : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.tag("myNoti").d("ServiceStarter: Receive call!")

    }
}
