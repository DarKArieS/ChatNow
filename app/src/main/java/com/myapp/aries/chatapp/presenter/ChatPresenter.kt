package com.myapp.aries.chatapp.presenter


import com.myapp.aries.chatapp.model.ChatModel
import com.myapp.aries.chatapp.model.SendChat
import com.myapp.aries.chatapp.model.UserInfo
import com.myapp.aries.chatapp.view.ChatView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.socket.client.Socket
import io.socket.emitter.Emitter

class ChatPresenter(private val chatView: ChatView, private var chatModel: ChatModel){
    init{
        println("ChatPresenter Created")
    }

    var userInfo = UserInfo()

    fun getNewMessage(){
        chatModel.getMessage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                chatView.setupAdapter(it)
                chatView.receiveNewMessage()
            }.subscribe()
    }

    fun refreshMessage(callback:()->Unit){
        chatModel.getMessage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                chatView.setupAdapter(it)
            }.doFinally { callback.invoke() }.subscribe()
    }

    fun sendMessage(msg:String){
        chatModel.sendMessage(SendChat(userInfo.userID,msg))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {}
            .doOnSuccess {
                chatView.sendMessage()
            }
            .subscribe()
    }

    fun startChatSocket(){
        chatModel.socket.on(Socket.EVENT_CONNECT, object: Emitter.Listener{
            override fun call(vararg args: Any?) {
                println("ChatPresenter: socket: connected!")
                for (msg in args) println(msg)
            }
        })
//        chatModel.socket.on(Socket.EVENT_PING, object: Emitter.Listener{
//            override fun call(vararg args: Any?) {
//                println("socket: ping!")
//                for (msg in args) println(msg)
//            }
//        })

        chatModel.socket.on(Socket.EVENT_DISCONNECT, object: Emitter.Listener{
            override fun call(vararg args: Any?) {
                //斷線會觸發，會自己連回來
                println("ChatPresenter: socket: EVENT_DISCONNECT!")
                for (msg in args) println(msg)
            }
        })

        chatModel.socket.on(Socket.EVENT_CONNECT_ERROR, object: Emitter.Listener{
            override fun call(vararg args: Any?) {
                //Ping不到會一直觸發
                println("ChatPresenter: socket: EVENT_CONNECT_ERROR!")
                for (msg in args) println(msg)
            }
        })

        chatModel.socket.on("chatRoom:messages", object: Emitter.Listener{
            override fun call(vararg args: Any?) {
                println("ChatPresenter: socket: chatRoom:messages get!")
                for (msg in args) println(msg)
                getNewMessage()
            }
        })
        println("ChatPresenter: socket io connection!")
        chatModel.socket.connect()
    }

    fun stopChatSocket(){
        chatModel.socket.disconnect()
        chatModel.socket.off()
        println("ChatPresenter: socket io disconnection!")
    }


}