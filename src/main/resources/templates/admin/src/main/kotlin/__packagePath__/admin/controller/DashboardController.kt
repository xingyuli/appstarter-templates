package @packageName@.admin.controller

import com.mobisist.springbootseed.appstarter.infra.web.ApiResult
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/dashboard")
open class DashboardController {

    private val logger = LoggerFactory.getLogger(DashboardController::class.java)

    @GetMapping
    open fun get() {
        TODO("not implemented")
    }

}