package com.myapp.aries.chatapp


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myapp.aries.chatapp.adapter.ChatAdapter
import com.myapp.aries.chatapp.model.ChatContent
import com.myapp.aries.chatapp.model.ChatModel
import com.myapp.aries.chatapp.presenter.ChatPresenter
import com.myapp.aries.chatapp.view.ChatView
import kotlinx.android.synthetic.main.fragment_chat.view.*


class ChatFragment : Fragment(), ChatView {
    private var userid: Int? = null
    private lateinit var rootView : View
    private var chatModel = ChatModel()
    private var chatPresenter = ChatPresenter(this, chatModel)

    companion object {
        @JvmStatic
        fun newInstance(userID: Int) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putInt("ARG_USERID", userID)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userid = it.getInt("ARG_USERID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_chat, container, false)

        setupAdapter()
        chatPresenter.getMessage()
        return rootView
    }

    private fun setupAdapter(){
        val chatList = listOf<ChatContent>()
        rootView.charRecyclerView.adapter = ChatAdapter(this.context!!, chatList)
        rootView.charRecyclerView.layoutManager = LinearLayoutManager(this.context!!)
    }


}
