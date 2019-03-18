package com.myapp.aries.chatapp

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
