package com.myapp.aries.chatapp.presenter

import com.myapp.aries.chatapp.model.LoginModel
import com.myapp.aries.chatapp.view.LoginView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginPresenter(private val loginView: LoginView, private val loginModel: LoginModel){
    fun login(userName:String){
        loginModel.getUserID(userName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { loginView.startProgress() }
            .doOnSuccess {
                val responseString = it.body()!!.string()
                println(responseString)
                val userID = responseString.toInt()
                loginView.navigateToChat(userID)
                loginView.endProgress()
            }
            .doOnError {
                loginView.connectingFail()
                loginView.endProgress()
            }
            .subscribe()
    }
}