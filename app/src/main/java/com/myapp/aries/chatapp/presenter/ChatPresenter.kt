package com.myapp.aries.chatapp.presenter

import com.myapp.aries.chatapp.model.ChatModel
import com.myapp.aries.chatapp.view.ChatView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ChatPresenter(private val chatView: ChatView, private val chatModel: ChatModel){

    fun getMessage(){
        chatModel.getMessage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {  }
            .doOnSuccess {
                val responseString = it.body()!!.string()
                println(responseString)
            }
            .doOnError {

            }
            .subscribe()
    }

}