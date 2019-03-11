package com.myapp.aries.chatapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

private const val ARG_FRAG_LOGIN = "ARG_FRAG_LOGIN"
private const val ARG_FRAG_CHAT = "ARG_FRAG_CHAT"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val myManager = this.supportFragmentManager
        val loginFragment = LoginFragment()
        val transaction = myManager.beginTransaction()
        transaction.add(R.id.mainFragmentContainer,loginFragment, ARG_FRAG_LOGIN).commit()
    }

    fun navigate(fragment: Fragment){
        val myManager = this.supportFragmentManager
        val fragTag = when{
            (fragment is LoginFragment)->{ARG_FRAG_LOGIN}
            (fragment is ChatFragment)->{ARG_FRAG_CHAT}
            else->""
        }
        val transaction = myManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        transaction.replace(R.id.mainFragmentContainer, fragment, fragTag)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun startProgress(){
        progressBar.visibility = View.VISIBLE
    }

    fun endProgress(){
        progressBar.visibility = View.INVISIBLE
    }
}
