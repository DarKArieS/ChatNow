package com.myapp.aries.chatapp


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myapp.aries.chatapp.adapter.ChatAdapter
import com.myapp.aries.chatapp.model.ChatContent
import com.myapp.aries.chatapp.model.ChatModel
import com.myapp.aries.chatapp.presenter.ChatPresenter
import com.myapp.aries.chatapp.view.ChatView
import kotlinx.android.synthetic.main.fragment_chat.view.*
import android.opengl.ETC1.getHeight




class ChatFragment : Fragment(), ChatView {
    private lateinit var rootView : View
    private var chatModel = ChatModel()
    private var chatPresenter = ChatPresenter(this, chatModel)

    companion object {
        @JvmStatic
        fun newInstance(userID: Int, userName: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    println("ChatFragment newInstance apply:")
                    putInt("ARG_USERID", userID)
                    putString("ARG_USERNAME", userName)
                }
            }
    }

    init{
        println("ChatFragment created!")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chatPresenter.userInfo.userID = it.getInt("ARG_USERID")
            chatPresenter.userInfo.userName = it.getString("ARG_USERNAME","")

            println("ChatFragment: show argument")
            println("----------------show Bundle----------------")
            for ( key in it.keySet()){
                println(key)
            }
            println("--------------------------------------------")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_chat, container, false)

        rootView.showNameTextView.text = chatPresenter.userInfo.userName
        //rootView.editText.setOnTouchListener { v, event -> receiveNewMessage();event. false}
        rootView.sendButton.setOnClickListener {
            chatPresenter.sendMessage(rootView.editText.editableText.toString())
            rootView.editText.setText("")

        }
        rootView.swipeRefreshLayout.setOnRefreshListener{
            chatPresenter.refreshMessage{
                rootView.swipeRefreshLayout.isRefreshing=false
            }
        }

        rootView.swipeRefreshLayout.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            println("swipeRefreshLayout layout changed: $v")
            println("$oldLeft -> $left")
            println("$oldRight -> $right")
            println("$oldTop -> $top")
            println("$oldBottom -> $bottom")

            if (rootView.charRecyclerView.layoutManager!=null){
                val a = getRecycleViewDistance()
                println("distance: $a")
            }

            rootView.charRecyclerView.scrollBy(0,oldBottom-bottom)


            if (rootView.charRecyclerView.layoutManager!=null){
                val a = getRecycleViewDistance()
                println("after auto scroll distance: $a")
            }

        }

//        rootView.editText.setOnTouchListener { v, event ->
//            if (event.action == 1){
//                val im: InputMethodManager =  activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//
//                println(im.isActive)
//            }
//            false
//        }

//        rootView.editText.setOnFocusChangeListener{ v,hasFocus->
//            println("edittext focus changed!")
//            println(hasFocus)
//            if (hasFocus){
//                rootView.charRecyclerView.scrollToPosition(rootView.charRecyclerView.adapter!!.itemCount - 1)
//            }
//        }

        chatPresenter.getNewMessage()
        chatPresenter.startChatSocket()
        return rootView
    }

    private fun getRecycleViewDistance(): Int {
        val layoutManager = rootView.charRecyclerView.layoutManager as LinearLayoutManager
        val firstVisibItem = rootView.charRecyclerView.getChildAt(0)
        val firstItemPosition = layoutManager.findFirstVisibleItemPosition()
        val itemCount = layoutManager.itemCount
        val recycleViewHeight = rootView.charRecyclerView.getHeight()
        val itemHeight = firstVisibItem.getHeight()
        val firstItemBottom = layoutManager.getDecoratedBottom(firstVisibItem)
        return (itemCount - firstItemPosition - 1) * itemHeight - recycleViewHeight + firstItemBottom
    }

    override fun onSaveInstanceState(outState: Bundle) {
        println("ChatFragment onSaveInstanceState")
        println("put something here :D")
        outState.putString("ARG_STH","Hello~")
        println("----------------show Bundle----------------")
        for ( key in outState.keySet()){
            println(key)
        }
        println("--------------------------------------------")
        println("put something to argument")
        this.arguments?.putString("ARG_STH","XDD")
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        println("ChatFragment onViewStateRestored")
        savedInstanceState?.let{
            println("----------------show Bundle----------------")
            for ( key in it.keySet()){
                println(key)
            }
            println("--------------------------------------------")
        }
        super.onViewStateRestored(savedInstanceState)

    }

    override fun onDestroyView() {
        chatPresenter.stopChatSocket()
        super.onDestroyView()
    }

    override fun onDestroy() {
        println("ChatFragment onDestroy")
        super.onDestroy()
    }

    override fun setupAdapter(chatList: List<ChatContent>){
        rootView.charRecyclerView.adapter = ChatAdapter(this.context!!, chatList, chatPresenter.userInfo.userID)
        rootView.charRecyclerView.layoutManager = LinearLayoutManager(this.context!!)
        //rootView.charRecyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->  }
//        rootView.charRecyclerView.clearOnScrollListeners()
//        rootView.charRecyclerView.addOnScrollListener(
//            object: RecyclerView.OnScrollListener(){
//                var state = 0
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    //println("onScrollStateChanged: $newState")
//                    state = newState
//                    super.onScrollStateChanged(recyclerView, newState)
//                }
//
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    val a =rootView.charRecyclerView.scrollY
//                    println("onScrolled: $state  dx: $dx dy: $dy currentY: $a")
//                    super.onScrolled(recyclerView, dx, dy)
//                }
//            }
//        )
    }

    override fun receiveNewMessage(){
        rootView.charRecyclerView.scrollToPosition(rootView.charRecyclerView.adapter!!.itemCount - 1)
    }

    override fun sendMessage() {
        chatPresenter.getNewMessage()
    }

}
