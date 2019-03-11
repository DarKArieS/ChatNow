package com.myapp.aries.chatapp.model

import io.reactivex.Single

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


class ChatModel{
    interface ChatAPI{
        @GET("messages")
        fun getMessage(): Single<ArrayList<ChatContent>>

        @POST("")
        fun sendMessage(@Body sendChat: SendChat): Single<Response<ResponseBody>>
    }

    private val service = RetrofitManager.retrofit.create(ChatModel.ChatAPI::class.java)

    fun getMessage():Single<ArrayList<ChatContent>>{
        return service.getMessage()
    }

    fun sendMessage(sendChat: SendChat):Single<Response<ResponseBody>>{
        return service.sendMessage(sendChat)
    }
}