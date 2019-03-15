package com.myapp.aries.chatapp.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ChatContent(
    @SerializedName("id") var id:Int = -1,
    @SerializedName("user_id")var userID:Int= -1,
    @SerializedName("body") var chat:String = "",
    @SerializedName("updated_at") var date :String = "",
    @SerializedName("user") var user :UserInfo = UserInfo()
)

data class SendChat(
    @SerializedName("user_id")var userID:Int,
    @SerializedName("body") var chat:String
)

data class UserInfo(
    @SerializedName("id") var userID:Int = -1,
    @SerializedName("name") var userName:String = ""
)