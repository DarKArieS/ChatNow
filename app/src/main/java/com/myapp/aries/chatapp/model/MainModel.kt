package com.myapp.aries.chatapp.model

import android.content.Context


class MainModel{
    // not a good class for mvp, due to it needs context
    companion object {
        fun getCurrentFragmentTag(context: Context, default:String):String{
            return context.getSharedPreferences("UI_SP", Context.MODE_PRIVATE)
                .getString("LAST_FRAG",default) ?: default
        }

        fun setCurrentFragmentTag(context: Context, tag:String){
            context.getSharedPreferences("UI_SP", Context.MODE_PRIVATE).edit().putString("LAST_FRAG",tag).apply()
        }

        fun getCurrentUserName(context: Context, default:String):String{
            return context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
                .getString("NAME",default) ?: default
        }

        fun setCurrentUserName(context: Context, name:String){
            context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE).edit().putString("NAME",name).apply()
        }

        fun setIsLogIn(context: Context, isLogin:Boolean){
            context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE).edit().putBoolean("IS_LOGIN",isLogin).apply()
        }

        fun getIsLogIn(context: Context):Boolean{
            return context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE)
                .getBoolean("IS_LOGIN", false)
        }
    }
}

