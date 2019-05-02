package com.myapp.aries.chatapp


import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
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
        preferenceManager.findPreference("AboutPreference").setOnPreferenceClickListener {
            // shared_pre stored at chatapp_preferences.xml
            Timber.tag("SettingFragment").d(
                preferenceManager.sharedPreferencesName
            )
            Timber.tag("SettingFragment").d(
                preferenceManager.sharedPreferencesMode.toString()
            )
            Timber.tag("SettingFragment").d(
                preferenceManager.sharedPreferences.all.toString()
            )

            Timber.tag("SettingFragment").d("click About")

            val dialogBuilder = mainActivity?.let{AlertDialog.Builder(it)}
            dialogBuilder?.apply{
                setTitle("關於")
                setMessage("你好~")
                setPositiveButton("ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User clicked OK button
                    })
            }
            val dialog = dialogBuilder?.create()
            dialog?.show()

            true
        }
    }

    private fun setupAppBar(){
        mainActivity?.setUpActionBarHomeButton {
            mainActivity?.onBackPressed()
        }
        this.setHasOptionsMenu(false)
    }
}
