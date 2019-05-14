package com.myapp.aries.chatapp.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.myapp.aries.chatapp.MainActivity
import com.myapp.aries.chatapp.R
import com.myapp.aries.chatapp.model.ChatContent

class MessageNotificationManager(val context: Context) {
    private val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, getChannel())
    private var newMessageList = mutableListOf<ChatContent>()

    init {
        initBuilder()
    }

    fun addNewMessage(message: ChatContent) {
        newMessageList.add(message)
    }

    fun notice() {
        if (newMessageList.size == 0) return
        mBuilder.setNotificationContent(newMessageList[0])
        mNotificationManager.notify(newMessageList[0].id, mBuilder.build())

//        val summaryNotification = mBuilder
//            .setContentTitle("Summary")
//            //set content text to support devices running API level < 24
//            .setContentText("Two new messages")
//            .setSmallIcon(R.drawable.ic_message_black_24dp)
//            //build summary info into InboxStyle template
//            .setStyle(NotificationCompat.InboxStyle()
//                .addLine("Alex Faarborg Check this out")
//                .addLine("Jeff Chang Launch Party")
//                .setBigContentTitle("2 new messages")
//                .setSummaryText("janedoe@example.com"))
//            //specify which group this notification belongs to
//            .setGroup("Notification_Group")
//            //set this notification as the summary for the group
//            .setGroupSummary(true)
//            .build()
//
//        mNotificationManager.notify(100, summaryNotification)

        newMessageList.removeAt(0)
    }

    private fun initBuilder() {
        val resultPendingIntent = TaskStackBuilder
            .create(context)
            .addNextIntent(
                Intent(context, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            )
            .getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        mBuilder
            .setSmallIcon(R.drawable.ic_message_black_24dp)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_message_black_24dp
                )
            )
            .setContentIntent(resultPendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setColor(ContextCompat.getColor(context.applicationContext, R.color.colorPrimary))
            //.setCategory(Notification.CATEGORY_REMINDER) // API > 23
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            //.addAction()
            .setAutoCancel(true)
            //.setGroup("Notification_Group")
    }

    private fun NotificationCompat.Builder.setNotificationContent(chatContent: ChatContent): NotificationCompat.Builder {
        return this.setContentTitle(chatContent.user.userName)
            .setContentText(chatContent.chat)
            .setStyle(getBigTextStyle(chatContent))
    }

    private fun getGroup(){

    }

    private fun getChannel(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel =
                NotificationChannel("New_Message", "New Message", NotificationManager.IMPORTANCE_HIGH)

            val notificationChannelGroup = NotificationChannelGroup("msgChannelGroup", "ChaNow Group")

            notificationChannel.description = "Notice you there is a new message!"
            notificationChannel.enableVibration(true)
            notificationChannel.enableLights(true)
            notificationChannel.group = "msgChannelGroup"
            notificationChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            notificationChannel.setShowBadge(true)

            mNotificationManager.createNotificationChannelGroup(notificationChannelGroup)
            mNotificationManager.createNotificationChannel(notificationChannel)

            "New_Message"
        } else "no_channel"
    }

    private fun getBigTextStyle(chatContent: ChatContent): NotificationCompat.BigTextStyle {
        return NotificationCompat.BigTextStyle()
            .bigText(chatContent.chat)
            .setBigContentTitle(chatContent.user.userName)
            .setSummaryText("New Message")
    }

}