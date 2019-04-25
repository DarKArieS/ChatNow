package com.myapp.aries.chatapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Binder
import android.widget.Toast
import com.google.gson.Gson
import com.myapp.aries.chatapp.model.ChatContent
import com.myapp.aries.chatapp.model.MainModel
import io.socket.client.IO
import io.socket.client.Socket
import timber.log.Timber

class NotificationService : Service() {
    interface NotificationListener{
        fun onReceiveNewMessage()
    }

    inner class ServiceBinder: Binder(){
        fun getService() = this@NotificationService
    }

    private val binder = ServiceBinder()
    private val mListeners = mutableListOf<NotificationListener>()

    private val socketOption = IO.Options() // used for send extra query
    private val socket = IO.socket("http://chatroom.sckao.space:3000/",socketOption)!!
    private lateinit var messageNotification : MessageNotificationManager

    init {
        Timber.tag("myNoti").d("NotificationService init")
    }

    override fun onCreate() {
        Timber.tag("myNoti").d("NotificationService onCreate")
        messageNotification = MessageNotificationManager(this)

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

            Timber.tag("myNoti").d("NotificationService: invoke callback!")
            var numberOfActive = 0
            for (l in mListeners){
                l.onReceiveNewMessage()
                numberOfActive++
            }

            if(numberOfActive==0){
                Timber.tag("myNoti").d("NotificationService: Send Notification!")
                val newMessage = Gson().fromJson(it[0].toString(), ChatContent::class.java)

                messageNotification.addNewMessage(newMessage)
                messageNotification.notice()
            }
        }

        Toast.makeText(this, "啟動服務", Toast.LENGTH_LONG).show()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.tag("myNoti").d("NotificationService onStart")
        checkLogin()
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
        // Called when "all" clients have disconnected
        Timber.tag("myNoti").d("NotificationService onUnbind")
        mListeners.clear()

        return true // if you will use onRebind, return true
    }

    override fun onDestroy() {
        Timber.tag("myNoti").d("NotificationService onDestroy")
        socket.disconnect()
        socket.off()
        Toast.makeText(this, "回收服務", Toast.LENGTH_LONG).show()
        restartService()
        super.onDestroy()
    }

    fun addNotificationListener(listener: NotificationListener){
        Timber.tag("myNoti").d("NotificationService addNotificationListener")
        mListeners.add(listener)
    }

    fun removeNotificationListener(listener: NotificationListener){
        Timber.tag("myNoti").d("NotificationService removeNotificationListener")
        mListeners.remove(listener)
    }

    fun checkLogin(){
        if (MainModel.getIsLogIn(this)){
            if(!socket.connected()) socket.connect()
        }else{
            socket.disconnect()
            //ToDo: clear notification
        }
    }

    fun restartService(){
        // restart directly-> crash QAQ
        //val intent = Intent(this, NotificationService::class.java)
        //this.startService(intent)


    }
}
