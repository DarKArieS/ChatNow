package com.myapp.aries.chatapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myapp.aries.chatapp.R
import com.myapp.aries.chatapp.model.ChatContent

class ChatAdapter (val context: Context, val chatItemList: List<ChatContent>):
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.adapter_chat_own, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = this.chatItemList.count()

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(chatItemList[p1])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(chatItem: ChatContent) {

        }
    }
}