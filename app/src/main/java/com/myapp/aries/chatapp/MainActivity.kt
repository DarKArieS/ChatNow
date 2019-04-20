package com.myapp.aries.chatapp

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.myapp.aries.chatapp.model.MainModel
import com.myapp.aries.chatapp.utilities.EventCollector
import com.myapp.aries.chatapp.utilities.NavigationManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var navigationManager : NavigationManager
    var refreshUIEvent = EventCollector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("Activity onCreate")
        startShowFragment()
    }

    override fun onDestroy() {
        println("MainActivity onDestroy")

        super.onDestroy()
    }

    private fun startShowFragment(){
        navigationManager = object: NavigationManager(
            this.supportFragmentManager,
            R.id.mainFragmentContainer,
            MainModel.getCurrentFragmentTag(this,"LoginFragment")
        ){
            override fun createFragment(tag: String): Fragment? {
                return when(tag){
                    "LoginFragment"-> LoginFragment.newInstance()
                    "ChatFragment"-> ChatFragment.newInstance() //ToDo
                    else->null
                }
            }
        }
    }

    fun navigate(tag:String){
        refreshUIEvent.post {
            navigationManager.navigateTo(tag)
        }.run()
    }

    fun navigate(tag:String, fragment: Fragment){
        refreshUIEvent.post {
            println("refreshUIEvent: I'm running!")
            navigationManager.navigateTo(tag,fragment)
        }.run()
    }

    fun startProgress(){
        progressBar.visibility = View.VISIBLE
    }

    fun endProgress(){
        progressBar.visibility = View.INVISIBLE
    }

    private var mHomeButtonCallback : (()->Unit)? = null
    fun setUpActionBarHomeButton(callback:()->Unit){
        mHomeButtonCallback = callback
        this.supportActionBar?.setHomeButtonEnabled(true)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun removeActionBarHomeButton(){
        mHomeButtonCallback=null
        this.supportActionBar?.setHomeButtonEnabled(false)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home->{
                mHomeButtonCallback?.invoke()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        refreshUIEvent.executable = false
        MainModel.setCurrentFragmentTag(this,navigationManager.currentFragmentTag)
    }

    override fun onPostResume() {
        super.onPostResume()
        refreshUIEvent.executable = true
        if(refreshUIEvent.isSuspended()){
            refreshUIEvent.run()
        }
    }

    //===================================================================================
    //點一下EditTextView外面收起鍵盤
    //ver2.0: 拖曳事件就不收
    //ver3.0: 新增點了不收的View白名單，記得在onCreateView時加入以及onDestroyView時移除

    private var isLongPressListening = false
    private var hasPressed = false
    var noHideSoftInputViewList = mutableListOf<View>()

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val v : View? = this.currentFocus
        if (ev?.action == MotionEvent.ACTION_DOWN){
            if ( !isTouchNoHideSoftInputView(ev.x,ev.y) && this.isShouldHideInput(v, ev))
                isLongPressListening = true
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
        if (v!= null && (v is EditText) )
            return !(isViewTouched(v,event.x,event.y))
        return false
    }

    private fun hideSoftInput(token: IBinder?) {
        if (token != null) {
            val im: InputMethodManager =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun isTouchNoHideSoftInputView(x:Float, y:Float): Boolean{
        for (view in noHideSoftInputViewList){
            if(view.isAttachedToWindow)
                return isViewTouched(view,x,y)
        }
        return false
    }

    private fun isViewTouched(v: View, x:Float, y:Float):Boolean{
        val l : IntArray  = intArrayOf( 0, 0 )
        v.getLocationInWindow(l)
        val left = l[0]
        val top = l[1]
        val bottom = top + v.height
        val right = left + v.width
        return (x > left && x < right && y > top && y < bottom)
    }
    //===================================================================================
}
