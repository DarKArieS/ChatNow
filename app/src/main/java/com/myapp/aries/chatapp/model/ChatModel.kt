package com.myapp.aries.chatapp.model

import io.reactivex.Single
import io.socket.client.IO

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


class ChatModel{
    init{
        println("ChatModel Created")
    }

    interface ChatAPI{
        @GET("messages")
        fun getMessage(): Single<ArrayList<ChatContent>>

        @POST("messages")
        fun sendMessage(@Body sendChat: SendChat): Single<Response<ResponseBody>>

        @GET("getuserid")
        fun getUserIDRx(@Query("user") userId: String): Single<Response<ResponseBody>>
    }

    private val service = RetrofitManager.retrofit.create(ChatModel.ChatAPI::class.java)

    fun getMessage():Single<ArrayList<ChatContent>>{
        return service.getMessage()
    }

    fun sendMessage(sendChat: SendChat):Single<Response<ResponseBody>>{
        return service.sendMessage(sendChat)
    }

    fun getUserID(userName:String):Single<Response<ResponseBody>>{
        return service.getUserIDRx(userName)
    }

    val socket = IO.socket("http://chatroom.sckao.space:3000/")!!

}