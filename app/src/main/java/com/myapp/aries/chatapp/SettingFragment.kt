package com.myapp.aries.chatapp


import android.content.Context
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import timber.log.Timber

class SettingFragment : PreferenceFragmentCompat() {
    private var mainActivity : MainActivity? = null

    init {
        Timber.tag("lifecycle").d("SettingFragment created!")
    }

    companion object {
        fun newInstance() = SettingFragment()
    }

    override fun onAttach(context: Context?) {
        if (context is MainActivity){
            mainActivity = activity as MainActivity
        }
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setupAppBar()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_chat, rootKey)
    }

    private fun setupAppBar(){
        mainActivity?.setUpActionBarHomeButton {
            mainActivity?.onBackPressed()
        }
        this.setHasOptionsMenu(false)
    }


}
