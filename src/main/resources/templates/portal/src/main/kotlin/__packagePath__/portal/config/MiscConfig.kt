package @packageName@.portal.config

import org.jooq.conf.Settings
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MiscConfig {

    @Bean
    open fun jooqSettings() = Settings().apply { isRenderFormatted = true }

}