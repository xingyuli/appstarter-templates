package com.mobisist.springbootseed.appstarter.infra.service.security

import org.springframework.security.core.userdetails.UserDetails

data class UserId(val value: Long)

interface UserDetails : UserDetails {
    val typesafeUserId: UserId
    val token: String?
}
