package com.myapp.aries.chatapp.utilities

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.myapp.aries.chatapp.R


abstract class NavigationManager(private val fragmentManager: FragmentManager,
                                 private val fragmentContainer:Int,
                                 var currentFragmentTag:String)
{
    init{
        lateralNavigateTo(currentFragmentTag)
    }

    private enum class FragmentStatus{
        SAME,EXIST,ABSENCE;

        var existFragment: Fragment? = null
    }

    fun lateralNavigateTo(tag:String){
        // ToDoDone: study: use show/hide or attach/detach
        // use attach/detach is memory saved but laggy ...
        // attach/detach will remove the fragment from back stack !?
        val status = checkFragmentStatus(tag)
        if(status == FragmentStatus.SAME) return
        val currentFrag = fragmentManager.findFragmentByTag(currentFragmentTag)
        val transaction = fragmentManager.beginTransaction()
        if(currentFrag!=null) transaction.hide(currentFrag)
        if(status == FragmentStatus.ABSENCE){
            status.existFragment = createFragment(tag)
            transaction.add(fragmentContainer,status.existFragment!!,tag)
        }else transaction.show(status.existFragment!!)
        transaction.commit()

        currentFragmentTag = tag
    }

    /*
     *  Force to show a new fragment which already instantiated
     */
    fun forwardNavigateTo(
        tag:String,
        fragment: Fragment,
        addToBackStack:Boolean = false,
        replaceCurrentFragment:Boolean = false
    ){
        val transaction = fragmentManager.beginTransaction()
        if(replaceCurrentFragment) transaction.replace(fragmentContainer,fragment,tag)
        else{
            val currentFrag = fragmentManager.findFragmentByTag(currentFragmentTag)
            if(currentFrag!=null) transaction.hide(currentFrag)
            when(val status = checkFragmentStatus(tag)){
                FragmentStatus.SAME->return
                FragmentStatus.ABSENCE->transaction.add(fragmentContainer,fragment,tag)
                FragmentStatus.EXIST->{
                    transaction.remove(status.existFragment!!)
                    transaction.add(fragmentContainer,fragment,tag)
                }
            }
        }
        if(addToBackStack) transaction.addToBackStack(null)
        else currentFragmentTag = tag

        transaction.commit()
    }

    fun forwardNavigateTo(
        tag:String,
        addToBackStack:Boolean = false,
        replaceCurrentFragment:Boolean = false
    ){
        val fragment = createFragment(tag)
        forwardNavigateTo(tag,fragment,addToBackStack,replaceCurrentFragment)
    }

    private fun checkFragmentStatus(tag:String):FragmentStatus{
        val foundFrag = fragmentManager.findFragmentByTag(tag)
        val status = when{
            (foundFrag == null)->FragmentStatus.ABSENCE
            foundFrag.isVisible->FragmentStatus.SAME
            else->FragmentStatus.EXIST
        }
        status.existFragment = foundFrag
        return status
    }

    private fun setAnimation(transaction:FragmentTransaction){
        transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }

    abstract fun createFragment(tag:String):Fragment

}