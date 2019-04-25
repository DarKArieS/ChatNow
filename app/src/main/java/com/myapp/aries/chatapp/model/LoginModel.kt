package com.myapp.aries.chatapp.model

import android.content.Context
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

class LoginModel(val context: Context){
    // From Server
    interface LoginAPI{
        @GET("getuserid")
        fun getUserIDRx(@Query("user") userId: String): Single<Response<ResponseBody>>
    }

    private var service = RetrofitManager.retrofit.create(LoginModel.LoginAPI::class.java)

    fun getUserID(userName:String):Single<Response<ResponseBody>>{
        return service.getUserIDRx(userName)
    }

//    companion object{
//        fun getInstance(context: Context){
//            return LoginModel(context)
//        }
//    }

    //From Shared Preference

    fun getSPUserName() = MainModel.getCurrentUserName(context, "某某人")
    fun setSPUserName(isLogin:Boolean) = MainModel.setIsLogIn(context, isLogin)

}