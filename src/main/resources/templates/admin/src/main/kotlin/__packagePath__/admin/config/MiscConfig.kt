package @packageName@.admin.config

import org.jooq.conf.Settings
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MiscConfig {

    @Bean
    open fun jooqSettings() = Settings().apply { isRenderFormatted = true }

    // NOTE for template users: uncomment this code block if task feature is needed
//    @Bean
//    open fun taskSchedule(): ThreadPoolTaskScheduler {
//        val scheduler = ThreadPoolTaskScheduler().apply {
//            poolSize = 2
//        }
//        scheduler.initialize()
//        return scheduler
//    }

}