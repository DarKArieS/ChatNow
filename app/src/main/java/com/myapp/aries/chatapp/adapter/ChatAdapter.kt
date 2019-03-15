package com.myapp.aries.chatapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myapp.aries.chatapp.R
import com.myapp.aries.chatapp.model.ChatContent
import kotlinx.android.synthetic.main.adapter_chat_guest.view.*
import kotlinx.android.synthetic.main.adapter_chat_own.view.*

class ChatAdapter (val context: Context, val chatItemList: List<ChatContent>, val currentUserID:Int):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if(chatItemList[position].userID == currentUserID) 0
        else 1
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return if(p1==0){
            val view = LayoutInflater.from(p0.context).inflate(R.layout.adapter_chat_own, p0, false)
            OwnViewHolder(view)
        }else{
            val view = LayoutInflater.from(p0.context).inflate(R.layout.adapter_chat_guest, p0, false)
            GuestViewHolder(view)
        }
    }

    override fun getItemCount(): Int = this.chatItemList.count()

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        (p0 as CustomViewHolder).bind(chatItemList[p1])
    }

    interface CustomViewHolder{
        fun bind(chatItem: ChatContent)
    }

    inner class GuestViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), CustomViewHolder{
        private val guestNameTextView = itemView.guestNameTextView
        private val guestChatTextView = itemView.guestChatTextView
        private val guestDateTextView = itemView.guestDateTextView
        override fun bind(chatItem: ChatContent) {
            guestNameTextView.text=chatItem.user.userName
            guestChatTextView.text=chatItem.chat
            guestDateTextView.text=chatItem.date
        }
    }

    inner class OwnViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), CustomViewHolder{
        private val ownChatTextView = itemView.ownChatTextView
        private val ownDateTextView = itemView.ownDateTextView
        override fun bind(chatItem: ChatContent) {
            ownChatTextView.text=chatItem.chat
            ownDateTextView.text=chatItem.date
        }
    }
}