package com.mobisist.springbootseed.appstarter.common

import org.slf4j.LoggerFactory

class Functions {
    companion object {
        internal val LOGGER = LoggerFactory.getLogger(Functions::class.java)
    }
}

fun <R> retryableOnCondition(desc: String, times: Int, condition: (R) -> Boolean, block: () -> R): () -> R? {
    val retryable: () -> R? = {
        var result: R? = null
        for (i in 1..times) {
            Functions.LOGGER.debug("the $i time to $desc")
            result = block()

            // stop trying once the condition not meet
            if (!condition(result)) {
                break
            }
        }
        result
    }
    return retryable
}

fun <R> retryableOnException(desc: String, times: Int, exceptionHandler: (Exception) -> Unit = { it.printStackTrace() }, block: () -> R): () -> R? {
    return {
        var result: R? = null
        for (i in 1..times) {
            Functions.LOGGER.debug("the $i time to $desc")
            try {
                result = block()

                // stop trying once no exception thrown
                break
            } catch(e: Exception) {
                exceptionHandler(e)
            }
        }
        result
    }
}

fun <R> retryOnCondition(desc: String, times: Int, condition: (R) -> Boolean, block: () -> R): R?
        = (retryableOnCondition(desc, times, condition, block))()

fun <R> retryOnException(desc: String, times: Int, exceptionHandler: (Exception) -> Unit = { it.printStackTrace() }, block: () -> R): R?
        = (retryableOnException(desc, times, exceptionHandler, block))()
