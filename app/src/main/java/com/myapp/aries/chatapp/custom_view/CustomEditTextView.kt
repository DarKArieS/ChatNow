package com.myapp.aries.chatapp.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class CustomEditTextView:EditText{

    constructor(context: Context)  : super(context)
    constructor(context: Context, attrs: AttributeSet)  : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle : Int)  : super(context, attrs, defStyle)

    override fun onTouchEvent(event:MotionEvent):Boolean{
        val superResult = super.onTouchEvent(event)

        val im: InputMethodManager =  context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager




        return superResult
    }
}