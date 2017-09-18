package @packageName@.infra.service.core

import @packageName@.infra.service.security.IdmUser
import com.mobisist.springbootseed.appstarter.infra.service.security.UserId

data class ServiceContext(val currentIdmUser: IdmUser) {

    val typesafeUserId: UserId
        inline get() = currentIdmUser.typesafeUserId

    val currentUserId: Long
        inline get() = typesafeUserId.value

}

fun IdmUser?.toServiceCtx() = if (this != null) ServiceContext(this) else null

fun List<Long>.without(serviceCtx: ServiceContext): List<Long> = toMutableList().apply {
    remove(serviceCtx.currentUserId)
}
