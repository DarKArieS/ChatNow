package com.myapp.aries.chatapp.model

import com.google.gson.annotations.SerializedName

data class ChatContent(
    @SerializedName("user_id")var userID:Int,
    @SerializedName("body") var chat:String
)