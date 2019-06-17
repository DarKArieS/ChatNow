package com.myapp.aries.chatapp.playground

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

class RxPlayground{
    var userID:Int = -1

    //======================================= Retrofit Login ======================================
    fun login(callback: ()->Unit, userName: String){
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
        client.addInterceptor(logging)
        client.readTimeout(30, TimeUnit.SECONDS)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://chatroom.sckao.space/")
//            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()

        val service : LoginAPI = retrofit.create(LoginAPI::class.java)

        val call = service.getUserID(userName)

        call.enqueue(object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("fail")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseString = response.body()!!.string()
                println(responseString)
                userID = responseString.toInt()
                callback.invoke()
            }
        })

// this guy can be compiled but is not worked XDD!
//        val wtf = service.wtf("XDD")
//        println(wtf)
    }

    interface LoginAPI{
        // okHttp3 like
        @GET("getuserid")
        fun getUserID(@Query("user") userId: String): Call<ResponseBody>

        // integrated with RxJava
        @GET("getuserid")
        fun getUserIDRx(@Query("user") userId: String): Single<ResponseBody>

        // this guy can be compiled but is not worked XDD!
        @GET("getuserid")
        fun wtf(@Query("user") userId: String):String
    }
//======================================= Rx Login ===========================================

    fun RxLogin(userName: String){

    }

//=================================== Rx + Retrofit Login ======================================

    fun rrLogin(callback: ()->Unit, userName: String){
        // Rx + Retrofit
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
        client.addInterceptor(logging)
        client.readTimeout(30, TimeUnit.SECONDS)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://chatroom.sckao.space/")
            .client(client.build())
            .build()

        val service : LoginAPI = retrofit.create(LoginAPI::class.java)

        val singleRx = service.getUserIDRx(userName)

        singleRx.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                val responseString = it.string()
                println(responseString)
                userID = responseString.toInt()
                callback.invoke()
            }
            .doOnError {
                println("fail")
            }
            .subscribe()
    }

    //======================================= Rx playground ======================================
    class myClass{
        var info =""
    }

    fun rxPlayground(){
        val single = object: SingleObserver<String> {
            override fun onError(e: Throwable) {

            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onSuccess(t: String) {
                println("I'm observer onSuccess" + t)
            }
        }

        // observer callback vs observable operator ???
        val observableSingle = Single.create<String>{ emitter ->
            //subscribeOn
            emitter.onSuccess("XD")}

        observableSingle
            .doOnSubscribe {
                //observerOn
            }
            .doOnSuccess { t->
                //observerOn
                println("I'm observable onSuccess" + t) }.subscribe(single)

        val observer = object: Observer<myClass> {
            override fun onComplete() {
                println("Hi! I'm onComplete!")
                Thread.sleep(1000)
                println("onComplete Done!")
            }

            override fun onError(e: Throwable) {

            }

            override fun onNext(t: myClass) {
                println("Hi! I'm onNext!")
                println(t.info)
                Thread.sleep(1000)
                println("onNext Done!")
            }

            override fun onSubscribe(d: Disposable) {
                println("Hi! I'm onSubscribe!")
                Thread.sleep(1000)
                println("onSubscribe Done!")
            }
        }

        val observer2 = object: Observer<myClass> {
            override fun onComplete() {
                println("observer2:Hi! I'm onComplete!")
                Thread.sleep(1000)
                println("observer2:onComplete Done!")
            }

            override fun onError(e: Throwable) {

            }

            override fun onNext(t: myClass) {
                println("observer2:Hi! I'm onNext!")
                println(t.info)
                Thread.sleep(1000)
                println("observer2:onNext Done!")
            }

            override fun onSubscribe(d: Disposable) {
                println("observer2:Hi! I'm onSubscribe!")
                Thread.sleep(1000)
                println("observer2:onSubscribe Done!")
            }
        }

        val observable = Observable.create(object: ObservableOnSubscribe<myClass> {
            override fun subscribe(emitter: ObservableEmitter<myClass>) {
                val a = myClass().apply{this.info=":D"}
                println("observable start to call observer!")
                emitter.onNext(a)
                println("observable call observer onNext1 done!")
                a.info = ":D !!"
                emitter.onNext(a)
                println("observable call observer onNext2 done!")
                emitter.onComplete()
                println("observable call onComplete done!")
                emitter.onNext(myClass().apply{this.info=":D"})
                println("useless call onNext")
            }
        })

        val disposable = observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ output->
                //onNext
            },{ e->
                //onError
            })

//        observable
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(observer)

//        observable
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(observer2)

    }
}