package @packageName@.admin.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/public")
open class PublicController {

    // NOTE for template users: you can safely remove this route as it is for demonstrating thymeleaf layout
    @GetMapping("/testLayout")
    open fun testLayout() = "test/testLayout"

}