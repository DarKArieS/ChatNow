package com.myapp.aries.chatapp

import android.content.Context
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.myapp.aries.chatapp.custom_view.CustomEditTextView
import kotlinx.android.synthetic.main.activity_main.*

private const val ARG_FRAG_LOGIN = "ARG_FRAG_LOGIN"
private const val ARG_FRAG_CHAT = "ARG_FRAG_CHAT"
private enum class FragmentStatus{SAME,EXIST,ABSENCE}

class MainActivity : AppCompatActivity() {
    private var currentFrag = ARG_FRAG_LOGIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState!=null)
            currentFrag = savedInstanceState.getString("CURRENT_FRAG",ARG_FRAG_LOGIN)

        startShowFragment()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        println("Activity onSaveInstanceState")
        savedInstanceState.putString("CURRENT_FRAG", currentFrag)
        println("----------------show Bundle----------------")
        for ( key in savedInstanceState!!.keySet()){
            println(key)
        }
        println("--------------------------------------------")
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        println("Activity onRestoreInstanceState")
        if (savedInstanceState!=null){
            println("----------------show Bundle----------------")
            for ( key in savedInstanceState.keySet()){
                println(key)
            }
            println("--------------------------------------------")
        }
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun startShowFragment(){
        if(checkFragmentStatus(currentFrag) == FragmentStatus.ABSENCE){
            when(currentFrag){
                ARG_FRAG_LOGIN->{
                    val loginFragment = LoginFragment()
                    loginFragment.retainInstance = true
                    this.supportFragmentManager.beginTransaction()
                        .add(R.id.mainFragmentContainer,loginFragment, ARG_FRAG_LOGIN).commit()
                }
                ARG_FRAG_CHAT->{

                }
            }
        }

    }

    private fun checkFragmentStatus(tag:String):FragmentStatus{
        val foundFrag = this.supportFragmentManager.findFragmentByTag(tag)
        return when{
            (foundFrag == null)->FragmentStatus.ABSENCE
            foundFrag.isVisible->FragmentStatus.SAME
            else->FragmentStatus.EXIST
        }
    }

    private fun getCurrentFragment(){

    }

    private fun showFragment(tag:String){

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

        val a = CustomEditTextView(this)
        val b = EditText(this)
    }

    //===================================================================================
    //點一下EditTextView外面收起鍵盤
    //ver2.0: 拖曳事件就不收
    private var isLongPressListening = false
    private var hasPressed = false

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val v : View? = this.currentFocus
        if (ev?.action == MotionEvent.ACTION_DOWN){
            if (this.isShouldHideInput(v, ev)) {
                isLongPressListening = true
            }
        }
        if (ev?.action == MotionEvent.ACTION_MOVE && isLongPressListening)
            hasPressed = true


        if (ev?.action == MotionEvent.ACTION_UP && isLongPressListening){
            if(!hasPressed){
                hideSoftInput(v!!.windowToken)
                v.clearFocus()
            }else hasPressed = false
            isLongPressListening = false
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideInput(v : View?, event: MotionEvent): Boolean{
        if (v!= null && (v is EditText) ){
            val l : IntArray  = intArrayOf( 0, 0 )
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()

            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

    private fun hideSoftInput(token: IBinder?) {
        if (token != null) {
            val im: InputMethodManager =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
    //===================================================================================
}
