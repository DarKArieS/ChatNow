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
        fun getMessage(): Single<Response<ResponseBody>>

        @POST("")
        fun sendMessage(@Body chatContent: ChatContent): Single<Response<ResponseBody>>
    }

    private val service = RetrofitManager.retrofit.create(ChatModel.ChatAPI::class.java)

    fun getMessage():Single<Response<ResponseBody>>{
        return service.getMessage()
    }

    fun sendMessage(chatContent: ChatContent):Single<Response<ResponseBody>>{
        return service.sendMessage(chatContent)
    }
}