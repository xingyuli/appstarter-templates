package @packageName@.portal

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.support.SpringBootServletInitializer

@SpringBootApplication(
        scanBasePackages = arrayOf(
                "@packageName@.portal",
                "@packageName@.infra",
                "com.mobisist.springbootseed.appstarter.infra")
)
open class Application : SpringBootServletInitializer() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }

}