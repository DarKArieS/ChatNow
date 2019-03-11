package com.myapp.aries.chatapp.model

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

class LoginModel{
    interface LoginAPI{
        @GET("getuserid")
        fun getUserIDRx(@Query("user") userId: String): Single<Response<ResponseBody>>
    }

    private var service = RetrofitManager.retrofit.create(LoginModel.LoginAPI::class.java)

    fun getUserID(userName:String):Single<Response<ResponseBody>>{
        return service.getUserIDRx(userName)
    }

}