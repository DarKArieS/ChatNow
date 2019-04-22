package com.myapp.aries.chatapp

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.myapp.aries.chatapp.adapter.ChatAdapter
import com.myapp.aries.chatapp.model.ChatContent
import com.myapp.aries.chatapp.model.ChatModel
import com.myapp.aries.chatapp.model.MainModel
import com.myapp.aries.chatapp.presenter.ChatPresenter
import com.myapp.aries.chatapp.service.NotificationService
import com.myapp.aries.chatapp.view.ChatView
import kotlinx.android.synthetic.main.fragment_chat.view.*
import android.content.Intent




class ChatFragment : Fragment(), ChatView {
    private lateinit var rootView : View
    private var chatModel = ChatModel()
    private var chatPresenter = ChatPresenter(this, chatModel)
    private var mainActivity : MainActivity? = null

    companion object {
        @JvmStatic
        fun newInstance(userID: Int, userName: String) =
            ChatFragment().apply {
                arguments = Bundle().apply{
                    println("ChatFragment newInstance apply:")
                    putInt("ARG_USERID", userID)
                    putString("ARG_USERNAME", userName)
                }
            }

        fun newInstance() =
            ChatFragment().apply {
                arguments = Bundle().apply{
                    // No UserInfo
                    putInt("ARG_USERID", -1)
                    putString("ARG_USERNAME", "")
                }
            }
    }

    init{
        println("ChatFragment created!")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chatPresenter.userInfo.userID = it.getInt("ARG_USERID",-1)
            chatPresenter.userInfo.userName = it.getString("ARG_USERNAME","")

//            println("ChatFragment: show argument")
//            println("----------------show Bundle----------------")
//            for ( key in it.keySet()){
//                println(key)
//            }
//            println("--------------------------------------------")
        }
    }

    override fun onAttach(context: Context?) {
        if (context is MainActivity){
            mainActivity = activity as MainActivity
        }
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_chat, container, false)

        mainActivity?.setUpActionBarHomeButton {
            // logout
            mainActivity?.navigate("LoginFragment")
        }

        rootView.sendButton.setOnClickListener {
            chatPresenter.sendMessage(rootView.editText.editableText.toString())
            rootView.editText.setText("")
        }
        rootView.swipeRefreshLayout.setOnRefreshListener{
            chatPresenter.refreshMessage{
                rootView.swipeRefreshLayout.isRefreshing=false
                rootView.floatingActionButton.hide()
            }
        }

        //println(chatPresenter.userInfo.userID)
        chatPresenter.updateUserId(MainModel.getCurrentUserName(this.context!!,"")){
            arguments = Bundle().apply{
                putInt("ARG_USERID", chatPresenter.userInfo.userID)
                putString("ARG_USERNAME", chatPresenter.userInfo.userName)
            }
            rootView.showNameTextView.text = chatPresenter.userInfo.userName
            setupAdapter(chatPresenter.chatList)
            MainModel.setCurrentUserName(this.context!!,chatPresenter.userInfo.userName)
            chatPresenter.refreshMessage{}
        }


        chatPresenter.startChatSocket()
        startNotificationService()
        bindNotificationService()
        rootView.floatingActionButton.setOnClickListener {scrollToLast()}

        (activity as MainActivity).noHideSoftInputViewList.add(rootView.sendButton)
        (activity as MainActivity).noHideSoftInputViewList.add(rootView.floatingActionButton)

        return rootView
    }

    private fun getRecycleViewFinalItemPosition():Int{
        val layoutManager = rootView.charRecyclerView.layoutManager as LinearLayoutManager
        return layoutManager.findLastVisibleItemPosition()
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
        (activity as MainActivity).noHideSoftInputViewList.clear()
        chatPresenter.stopChatSocket()
        unbindNotificationService()
        super.onDestroyView()
    }

    override fun onDestroy() {
        println("ChatFragment onDestroy")
        chatPresenter.cancelRequests()
        super.onDestroy()
    }

    private fun setupAdapter(chatList: List<ChatContent>){
        rootView.charRecyclerView.adapter = ChatAdapter(this.context!!, chatList, chatPresenter.userInfo.userID)

        val layoutManager = LinearLayoutManager(this.context!!)
        layoutManager.stackFromEnd = true
        rootView.charRecyclerView.layoutManager = layoutManager

//        rootView.charRecyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->  }
        rootView.charRecyclerView.clearOnScrollListeners()
        rootView.charRecyclerView.addOnScrollListener(
            object: RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    println("onScrollStateChanged: $newState")
                    if(newState==0)
                        if(getRecycleViewFinalItemPosition() > (rootView.charRecyclerView.adapter!!.itemCount - 4)){
                        rootView.floatingActionButton.hide()
                        }else rootView.floatingActionButton.show()
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    println("onScrolled: $state  dx: $dx dy: $dy ")
                    super.onScrolled(recyclerView, dx, dy)
                }
            }
        )
    }

    override fun receiveNewMessage(){
        //setupAdapter(chatPresenter.chatList)
        rootView.charRecyclerView.adapter?.notifyDataSetChanged()
        scrollToLast()
    }

    private fun scrollToLast(){
        rootView.charRecyclerView.smoothScrollToPosition(rootView.charRecyclerView.adapter!!.itemCount - 1)
        rootView.floatingActionButton.hide()
    }

    override fun sendMessage() {
        chatPresenter.getMessage()
    }

    override fun showConnectingFail() {
        Toast.makeText(this.context, "連線失敗", Toast.LENGTH_SHORT).show()
    }

    override fun startAsyncProgress() {
        mainActivity?.startProgress()
    }

    override fun endAsyncProgress() {
        mainActivity?.endProgress()
    }

    private var mService: NotificationService? = null
    private var mBinder: NotificationService.ServiceBinder? = null
    private var hasBounded = false
    private val serviceConnection = object:ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mBinder = service as NotificationService.ServiceBinder
            mService = service.getService()
            hasBounded = true
            mBinder!!.registerCallback {
                chatPresenter.getMessage()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBinder!!.clearRegisterCallback()
            mBinder = null
            mService = null
            hasBounded = false
        }
    }

    fun startNotificationService(){
        val intent = Intent(mainActivity, NotificationService::class.java)
        this.context?.startService(intent)
    }

    fun bindNotificationService(){
        val intent = Intent(this.context, NotificationService::class.java)
        this.context?.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindNotificationService(){
        this.context?.unbindService(serviceConnection)
    }

}
