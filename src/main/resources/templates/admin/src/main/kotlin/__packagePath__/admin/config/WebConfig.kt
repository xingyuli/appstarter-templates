package @packageName@.admin.config

import com.mobisist.springbootseed.appstarter.infra.web.config.CustomWebMvcConfigurerAdapter
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry

@Configuration
open class WebConfig : CustomWebMvcConfigurerAdapter() {

    override fun addViewControllers(registry: ViewControllerRegistry) {
        with(ViewControllerRegistryWrapper(registry)) {
            "/login" toView "entrance/login"

            "/" redirectTo "dashboard"
        }
    }

    private class ViewControllerRegistryWrapper(val registry: ViewControllerRegistry) {
        infix fun String.toView(name: String) {
            registry.addViewController(this).setViewName(name)
        }
        infix fun String.redirectTo(url: String) {
            registry.addRedirectViewController(this, url)
        }
    }

}