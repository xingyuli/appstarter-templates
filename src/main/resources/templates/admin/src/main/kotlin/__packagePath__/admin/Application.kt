package @packageName@.admin

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.support.SpringBootServletInitializer
import org.springframework.context.annotation.ComponentScan

@ComponentScan(
        basePackages = arrayOf(
                "@packageName@.admin",
                "@packageName@.infra",
                "com.mobisist.springbootseed.appstarter.infra"))
@SpringBootApplication
open class Application : SpringBootServletInitializer() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }

}