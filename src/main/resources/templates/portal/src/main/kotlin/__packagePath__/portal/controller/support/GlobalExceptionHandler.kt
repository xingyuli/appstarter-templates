package @packageName@.portal.controller.support

import com.mobisist.springbootseed.appstarter.infra.web.ApiResultExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler : ApiResultExceptionHandler()
