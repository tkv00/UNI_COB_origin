package com.example.uni_cob.FindFriend

import com.example.uni_cob.utility.UserStatus

data class FriendRequest(
    val fromUserId: String? = null,
    val toUserId: String? = null,
    var status: UserStatus = UserStatus.REQUEST
)