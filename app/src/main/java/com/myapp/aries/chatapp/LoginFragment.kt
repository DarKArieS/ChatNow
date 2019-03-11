package com.myapp.aries.chatapp


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.myapp.aries.chatapp.model.LoginModel
import com.myapp.aries.chatapp.presenter.LoginPresenter
import com.myapp.aries.chatapp.view.LoginView
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : Fragment(), LoginView {
    private lateinit var rootView:View

    private val loginModel = LoginModel()
    private val loginPresenter = LoginPresenter(this, loginModel)
    private var isProgressing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_login, container, false)

        rootView.loginButton.setOnClickListener {
            if(!isProgressing)
                loginPresenter.login( rootView.nameEditText.editableText.toString() )
        }
        return rootView
    }

    override fun navigateToChat(userID:Int) {
        val chatFragment = ChatFragment.newInstance(userID)
        (activity as MainActivity).navigate(chatFragment)
    }

    override fun startProgress() {
        isProgressing = true
        (activity as MainActivity).startProgress()
    }

    override fun endProgress() {
        isProgressing = false
        (activity as MainActivity).endProgress()
    }

    override fun connectingFail() {
        Toast.makeText(activity, "連線失敗", Toast.LENGTH_SHORT).show()
    }
}
