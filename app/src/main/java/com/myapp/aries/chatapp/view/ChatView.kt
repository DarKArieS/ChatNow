package com.myapp.aries.chatapp.view

import com.myapp.aries.chatapp.model.ChatContent

interface ChatView{
    fun setupAdapter(chatList: List<ChatContent>)
    fun sendMessage()
    fun receiveNewMessage()

}