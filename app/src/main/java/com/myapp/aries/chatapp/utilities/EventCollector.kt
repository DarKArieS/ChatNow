package com.myapp.aries.chatapp.utilities

import android.os.Handler

/* Avoid some callbacks run when the activity is paused.
 */

class EventCollector{
    private val handler = Handler()
    private var collectList = mutableListOf<()->Unit>()
    private var mIsSuspended = false
    var executable  = false

    fun clear():EventCollector{
        collectList.clear()
        return this
    }

    fun post(r:()->Unit): EventCollector{
        collectList.add(r)
        return this
    }

    fun isSuspended():Boolean = mIsSuspended

    fun run(){
        if (!executable ){
            println("EventCollector: suspended!")
            this.mIsSuspended = true
        }else {
            for (r in collectList) handler.post(r)
            this.mIsSuspended = false
            collectList.clear()
        }
    }
}