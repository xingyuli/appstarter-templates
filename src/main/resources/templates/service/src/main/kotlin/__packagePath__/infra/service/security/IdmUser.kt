package @packageName@.infra.service.security

import com.mobisist.springbootseed.appstarter.infra.service.security.UserDetails
import com.mobisist.springbootseed.appstarter.infra.service.security.UserId
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

// NOTE for template users: pass in your user pojo, and implement following methods based on it
class IdmUser(val user: Any, override val token: String? = null, private val __authorities: List<String>) : UserDetails {

    override val typesafeUserId: UserId get() = TODO("not implemented")

    override fun getUsername(): String = TODO("not implemented")

    override fun isCredentialsNonExpired(): Boolean = TODO("not implemented")

    override fun isAccountNonExpired(): Boolean = TODO("not implemented")

    override fun isAccountNonLocked(): Boolean = TODO("not implemented")

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return __authorities.map(::SimpleGrantedAuthority)
    }

    override fun isEnabled(): Boolean = TODO("not implemented")

    override fun getPassword(): String = TODO("not implemented")

}