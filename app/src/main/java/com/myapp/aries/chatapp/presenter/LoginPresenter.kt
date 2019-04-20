package com.myapp.aries.chatapp.presenter

import com.myapp.aries.chatapp.model.LoginModel
import com.myapp.aries.chatapp.model.MainModel
import com.myapp.aries.chatapp.view.LoginView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.disposables.CompositeDisposable

class LoginPresenter(private val loginView: LoginView, private val loginModel: LoginModel){
    private var observableList = CompositeDisposable()
    fun login(userName:String){
        observableList.add(loginModel.getUserID(userName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { loginView.startAsyncProgress() }
            .doFinally { loginView.endAsyncProgress() }
            .subscribe({
                val responseString = it.body()!!.string()
                //println(responseString)
                val userID = responseString.toInt()
                loginView.navigateToChat(userID, userName)
            },{loginView.showConnectingFail()})
        )
    }

}