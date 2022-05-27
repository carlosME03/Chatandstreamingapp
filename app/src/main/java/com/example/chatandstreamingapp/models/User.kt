package com.example.chatandstreamingapp.models

import com.example.chatandstreamingapp.Constants.ROLE_CLIENT

data class User(
    val username: String? = "",
    val email: String? = "",
    val uid: String? = "",
    val role: Int = ROLE_CLIENT
)
