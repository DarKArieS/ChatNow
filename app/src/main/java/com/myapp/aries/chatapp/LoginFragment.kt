package com.myapp.aries.chatapp


import android.content.Context
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

class LoginFragment() : Fragment(), LoginView {
    private lateinit var rootView:View
    private var mainActivity : MainActivity? = null

    private val loginModel = LoginModel()
    private val loginPresenter = LoginPresenter(this, loginModel)
    private var isProgressing = false

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }

    init{
        println("LoginFragment created!")
    }

    override fun onAttach(context: Context?) {
        if (context is MainActivity){
            mainActivity = activity as MainActivity
        }
        super.onAttach(context)
    }

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

        mainActivity?.removeActionBarHomeButton()
        return rootView
    }

    override fun onDestroy() {
        println("LoginFragment onDestroy")
        super.onDestroy()
    }

    override fun navigateToChat(userID:Int, userName:String) {
        val chatFragment = ChatFragment.newInstance(userID, userName)
        mainActivity?.navigate("ChatFragment",chatFragment)
    }

    override fun startAsyncProgress() {
        isProgressing = true
        mainActivity?.startProgress()
    }

    override fun endAsyncProgress() {
        isProgressing = false
        mainActivity?.endProgress()
    }

    override fun showConnectingFail() {
        Toast.makeText(activity, "連線失敗", Toast.LENGTH_SHORT).show()
    }
}
