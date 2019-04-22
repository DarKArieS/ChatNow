package com.myapp.aries.chatapp.presenter


import com.myapp.aries.chatapp.model.*
import com.myapp.aries.chatapp.view.ChatView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.reactivex.disposables.CompositeDisposable

class ChatPresenter(private val chatView: ChatView, private var chatModel: ChatModel){
    init{
        println("ChatPresenter Created")
    }

    var userInfo = UserInfo()
    var chatList = mutableListOf<ChatContent>()

    private var observableList = CompositeDisposable()

    fun updateUserId(userName:String, callback: () -> Unit){
        if(userInfo.userID!=-1){
            callback.invoke()
        }else{
            observableList.add(chatModel.getUserID(userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { chatView.startAsyncProgress() }
                .doFinally { chatView.endAsyncProgress() }
                .subscribe({
                    val responseString = it.body()!!.string()
                    //println(responseString)
                    userInfo.userID = responseString.toInt()
                    userInfo.userName = userName
                    callback.invoke()
                },{chatView.showConnectingFail()})
            )
        }
    }

    fun getMessage(){
        observableList.add(
            chatModel
            .getMessage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                chatList.clear()
                chatList.addAll( it as Collection<ChatContent>)
                chatView.receiveNewMessage()
                //ToDo: Don't refresh all message, just update new one!
            }.subscribe({},{chatView.showConnectingFail()})
        )
    }

    fun refreshMessage(callback:()->Unit){
        observableList.add(chatModel
            .getMessage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                chatList.clear()
                chatList.addAll( it as Collection<ChatContent>)
                chatView.receiveNewMessage()
            }.doFinally { callback.invoke() }
            .subscribe({},{chatView.showConnectingFail()})
        )
    }

    fun sendMessage(msg:String){
        if (msg == "") return
        observableList.add(chatModel
            .sendMessage(SendChat(userInfo.userID,msg))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .doOnSuccess {
                chatView.sendMessage()
            }
            .subscribe({},{chatView.showConnectingFail()})
        )
    }

    fun startChatSocket(){
//        chatModel.socket.on(Socket.EVENT_CONNECT, object: Emitter.Listener{
//            override fun call(vararg args: Any?) {
//                println("ChatPresenter: socket: connected!")
//                for (msg in args) println(msg)
//            }
//        })
//        chatModel.socket.on(Socket.EVENT_PING, object: Emitter.Listener{
//            override fun call(vararg args: Any?) {
//                println("socket: ping!")
//                for (msg in args) println(msg)
//            }
//        })
//
//        chatModel.socket.on(Socket.EVENT_DISCONNECT, object: Emitter.Listener{
//            override fun call(vararg args: Any?) {
//                //斷線會觸發，會自己連回來
//                println("ChatPresenter: socket: EVENT_DISCONNECT!")
//                for (msg in args) println(msg)
//            }
//        })
//
//        chatModel.socket.on(Socket.EVENT_CONNECT_ERROR, object: Emitter.Listener{
//            override fun call(vararg args: Any?) {
//                //Ping不到會一直觸發
//                println("ChatPresenter: socket: EVENT_CONNECT_ERROR!")
//                for (msg in args) println(msg)
//            }
//        })
//
//        chatModel.socket.on("chatRoom:messages", object: Emitter.Listener{
//            override fun call(vararg args: Any?) {
//                println("ChatPresenter: socket: chatRoom:messages get!")
//                for (msg in args) println(msg)
//                getMessage()
//            }
//        })
//        println("ChatPresenter: socket io connection!")
//
//        if(!chatModel.socket.connected())chatModel.socket.connect()
    }

    fun stopChatSocket(){
//        chatModel.socket.disconnect()
//        chatModel.socket.off()
//        println("ChatPresenter: socket io disconnection!")
    }

    fun cancelRequests(){
        observableList.clear()
    }
}