package com.mobisist.springbootseed.appstarter.infra.service.security

import org.springframework.security.core.userdetails.UserDetailsService

interface UserDetailsService : UserDetailsService {
    fun loadUserById(id: Long): UserDetails?
}