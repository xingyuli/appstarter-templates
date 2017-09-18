package @packageName@.infra.service

import com.mobisist.springbootseed.appstarter.infra.service.GroupedErrorCode

sealed class CustomErrorCode(from: Int, no: Int) : GroupedErrorCode(10000, from, no) {

    sealed class UserErrorCode(no: Int) : CustomErrorCode(0, no) {
        object MOBILE_ALREADY_EXISTED : UserErrorCode(0)
        object MOBILE_NOT_EXISTED : UserErrorCode(1)
    }

}