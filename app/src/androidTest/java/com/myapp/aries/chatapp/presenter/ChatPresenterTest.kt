package com.myapp.aries.chatapp.presenter

import com.myapp.aries.chatapp.model.ChatContent
import com.myapp.aries.chatapp.model.ChatModel
import com.myapp.aries.chatapp.view.ChatView
import org.junit.Test

import org.junit.Assert.*

class ChatPresenterTest {

    @Test
    fun startChatSocket() {
        println("Test Start!~")
        val mockView = object : ChatView {
            override fun sendMessage() {

            }

            override fun setupAdapter(chatList: List<ChatContent>) {

            }

            override fun receiveNewMessage() {

            }
        }
        val testModel = ChatModel()
        val testPresenter = ChatPresenter(mockView, testModel)
        testPresenter.startChatSocket()
        Thread.sleep(60000)
        println("TestTestEnd!~")
        testPresenter.stopChatSocket()
    }
}