package com.myapp.aries.chatapp

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import android.view.*
import com.myapp.aries.chatapp.service.NotificationJobService
import timber.log.Timber

class ChatFragment : Fragment(), ChatView, NotificationService.NotificationListener {
    private lateinit var rootView : View
    private var chatModel = ChatModel()
    private var chatPresenter = ChatPresenter(this, chatModel)
    private var mainActivity : MainActivity? = null

    companion object {
        @JvmStatic
        fun newInstance(userID: Int, userName: String) =
            ChatFragment().apply {
                arguments = Bundle().apply{
                    Timber.tag("lifecycle").d("ChatFragment newInstance apply")
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
        Timber.tag("lifecycle").d("ChatFragment created!")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chatPresenter.userInfo.userID = it.getInt("ARG_USERID",-1)
            chatPresenter.userInfo.userName = it.getString("ARG_USERNAME","")
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

        setupAppBar()

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

        rootView.floatingActionButton.setOnClickListener {scrollToLast()}

        mainActivity?.noHideSoftInputViewList?.add(rootView.sendButton)
        mainActivity?.noHideSoftInputViewList?.add(rootView.floatingActionButton)

        startJobService()
        return rootView
    }

    override fun onHiddenChanged(hidden: Boolean) {
        Timber.tag("lifecycle").d("ChatFrag onHiddenChanged $hidden")
        if(!hidden){
            setupAppBar()
        }
        super.onHiddenChanged(hidden)
    }

    private fun getRecycleViewFinalItemPosition():Int{
        val layoutManager = rootView.charRecyclerView.layoutManager as LinearLayoutManager
        return layoutManager.findLastVisibleItemPosition()
    }

    override fun onPause() {
        unbindNotificationService()
        super.onPause()
    }

    override fun onResume() {
        startNotificationService()
        bindNotificationService()
        super.onResume()
    }

    override fun onDestroyView() {
        mainActivity?.noHideSoftInputViewList?.clear()
        Timber.tag("lifecycle").d("ChatFrag onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Timber.tag("lifecycle").d("ChatFragment onDestroy")
        chatPresenter.cancelRequests()
        super.onDestroy()
    }

    private fun setupAppBar(){
        mainActivity?.setUpActionBarHomeButton {
            // logout
            //stopNotificationService()
            unbindNotificationService()
            MainModel.setIsLogIn(this.context!!, false)
            mService?.checkLogin()
            mainActivity?.forwardNavigate(
                "LoginFragment",
                replaceCurrentFragment = true
            )
        }

        this.setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.chat_appbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.appbar_action_setting->{
                Timber.tag("menu").d("click setting!")
                mainActivity?.forwardNavigate(
                    "SettingFragment",
                    addToBackStack = true
                )
            }
        }
        return super.onOptionsItemSelected(item)
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
                    Timber.tag("onScroll").d("onScrollStateChanged: $newState")
                    if(newState==0)
                        if(getRecycleViewFinalItemPosition() > (rootView.charRecyclerView.adapter!!.itemCount - 4)){
                        rootView.floatingActionButton.hide()
                        }else rootView.floatingActionButton.show()
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    //Timber.tag("onScroll").d("onScrolled: $state  dx: $dx dy: $dy ")
                    super.onScrolled(recyclerView, dx, dy)
                }
            }
        )
    }

    override fun receiveNewMessage(){
        //setupAdapter(chatPresenter.chatList)
        scrollToLast()
        rootView.charRecyclerView.adapter?.notifyDataSetChanged()
        //ToDo make custom insert animation!
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
            mService?.addNotificationListener(this@ChatFragment)
            hasBounded = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            clearServiceMember()
        }
    }

    private fun startNotificationService(){
        val intent = Intent(mainActivity, NotificationService::class.java)
        this.context?.startService(intent)
    }

    private fun stopNotificationService(){
        val intent = Intent(mainActivity, NotificationService::class.java)
        this.context?.stopService(intent)
        // Never Stop ~~~~~
    }

    private fun bindNotificationService(){
        val intent = Intent(this.context, NotificationService::class.java)
        this.context?.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindNotificationService(){
        mService?.removeNotificationListener(this@ChatFragment)
        if(mService!=null) this.context?.unbindService(serviceConnection)
        clearServiceMember()
    }

    private fun clearServiceMember(){
        mBinder = null
        mService = null
        hasBounded = false
    }

    override fun onReceiveNewMessage() {
        chatPresenter.getMessage()
    }

    private fun startJobService(){
        val jobScheduler = mainActivity!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val jobInfo = JobInfo
            .Builder(11,ComponentName(this.context!!,NotificationJobService::class.java))
            .setOverrideDeadline(0)
            .build()

        jobScheduler.schedule(jobInfo)
    }
}
