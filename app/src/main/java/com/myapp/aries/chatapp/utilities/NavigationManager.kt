package com.myapp.aries.chatapp.utilities

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.myapp.aries.chatapp.R


abstract class NavigationManager(private val fragmentManager: FragmentManager,
                                 private val fragmentContainer:Int,
                                 var currentFragmentTag:String)
{
    init{
        navigateTo(currentFragmentTag)
    }

    private enum class FragmentStatus{
        SAME,EXIST,ABSENCE;

        var existFragment: Fragment? = null
    }

    fun navigateTo(tag:String){
        // ToDoDone: study: use show/hide or attach/detach
        // use attach/detach is memory saved but laggy ...
        // attach/detach will remove the fragment from back stack !?
        val status = checkFragmentStatus(tag)
        if(status == FragmentStatus.SAME) return
        val currentFrag = fragmentManager.findFragmentByTag(currentFragmentTag)
        val transaction = fragmentManager.beginTransaction()
//        transaction.setCustomAnimations(
//            R.anim.slide_in_right,
//            R.anim.slide_out_left,
//            R.anim.slide_in_left,
//            R.anim.slide_out_right
//        )
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
    fun navigateTo(tag:String, fragment: Fragment){
        val status = checkFragmentStatus(tag)
        if(status == FragmentStatus.SAME){
            // throw exception
            throw Exception("NavigationManager: you are already at the fragment you want to navigate!")
        }

        val currentFrag = fragmentManager.findFragmentByTag(currentFragmentTag)
        val transaction = fragmentManager.beginTransaction()
//        transaction.setCustomAnimations(
//            R.anim.slide_in_right,
//            R.anim.slide_out_left,
//            R.anim.slide_in_left,
//            R.anim.slide_out_right
//        )
        if(currentFrag!=null) transaction.hide(currentFrag)
        if(status == FragmentStatus.ABSENCE){
            status.existFragment = fragment
            transaction.add(fragmentContainer,status.existFragment!!,tag)
        }else{
            // throw exception
            //throw Exception("NavigationManager: the fragment you want to navigate is already existed!")
            transaction.remove(status.existFragment!!)
            status.existFragment = fragment
            transaction.add(fragmentContainer,status.existFragment!!,tag)
        }
        transaction.commit()

        currentFragmentTag = tag
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

    abstract fun createFragment(tag:String):Fragment?

}