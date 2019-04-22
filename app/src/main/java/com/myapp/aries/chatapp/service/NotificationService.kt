package com.myapp.aries.chatapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Binder
import io.socket.client.IO
import io.socket.client.Socket
import timber.log.Timber

class NotificationService : Service() {
    inner class ServiceBinder: Binder(){
        private val mutableCallbackList = mutableListOf<()->Unit>()

        val registerCallbackSize = mutableCallbackList.size

        fun getService() = this@NotificationService

        fun registerCallback(callback:()->Unit){
            mutableCallbackList.add(callback)
        }

        fun clearRegisterCallback(){
            mutableCallbackList.clear()
        }

        fun invokeCallback(){
            for (i in mutableCallbackList){
                i.invoke()
            }
        }
    }

    private val binder = ServiceBinder()
    val option = IO.Options() // used for send extra query

    private val socket = IO.socket("http://chatroom.sckao.space:3000/",option)!!

    init {
        Timber.tag("myNoti").d("NotificationService init")
    }

    override fun onCreate() {
        Timber.tag("myNoti").d("NotificationService onCreate")

        // socket connect
        socket.on(Socket.EVENT_CONNECT){
            Timber.tag("myNoti").d("NotificationService socket: connect!")
        }
        socket.on(Socket.EVENT_PING){
            Timber.tag("myNoti").d("NotificationService socket: ping!")
        }

        socket.on(Socket.EVENT_DISCONNECT){
            Timber.tag("myNoti").d("NotificationService socket: disconnect!")
        }

        socket.on(Socket.EVENT_CONNECT_ERROR){
            Timber.tag("myNoti").d("NotificationService socket: connect ERROR!")
        }

        socket.on("chatRoom:messages"){
            Timber.tag("myNoti").d("NotificationService socket: get messages!")

            // println received msg
            for (msg in it) Timber.tag("myNoti").d(msg.toString())

            if (binder.registerCallbackSize>0){
                binder.invokeCallback()
            }else{
                // ToDo Send Notification
                Timber.tag("myNoti").d("NotificationService: Send Notification!")
            }
        }

        if(!socket.connected()) socket.connect()

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.tag("myNoti").d("NotificationService onStart")
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        // only do once when first binding
        Timber.tag("myNoti").d("NotificationService onBind")

        return binder
    }

    override fun onRebind(intent: Intent?) {
        // only called when onUnbind return true
        // called when new clients have connected to the service, after onUnbind have been called
        Timber.tag("myNoti").d("NotificationService onReBind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        // Called when all clients have disconnected
        Timber.tag("myNoti").d("NotificationService onUnbind")

//        return super.onUnbind(intent)
        return true // if you will use onRebind, return true
    }



    override fun onDestroy() {
        Timber.tag("myNoti").d("NotificationService onDestroy")
        socket.disconnect()
        socket.off()
        super.onDestroy()
    }
}
