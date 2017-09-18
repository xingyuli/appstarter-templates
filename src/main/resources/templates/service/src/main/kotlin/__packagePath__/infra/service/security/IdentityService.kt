package @packageName@.infra.service.security

import com.mobisist.springbootseed.appstarter.infra.service.security.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

sealed class IdentityService : UserDetailsService {

    // NOTE for template users: when different strategies are needed, simply implement loadUserByUsername and loadUserById in class body
    class AdminIdentityService : IdentityService()
    class PortalIdentityService : IdentityService()

    override fun loadUserByUsername(username: String): IdmUser {
        val user = TODO("not implemented") ?: throw UsernameNotFoundException(username)
        return user.toIdmUser()
    }

    override fun loadUserById(id: Long): IdmUser? {
        val user = TODO("not implemented")
        return user?.let { it.toIdmUser() }
    }

    // NOTE for template users: replace parameter type with your user pojo's type
    private fun loadAuthorities(user: Any): List<String> {
        // NOTE for template users: load from permissions table if needed
        return emptyList()
    }

    // NOTE for template users: replace the receiver type with your user pojo's type
    private fun Any.toIdmUser(): IdmUser {
        // NOTE for template users: pass in token if needed
        return IdmUser(this, null, loadAuthorities(this))
    }

}