package com.mobisist.springbootseed.appstarter.common

import org.junit.Assert
import org.junit.Test

class DESPlusTest {

    @Test
    fun testDefaultKey() {
        val plainText = "123456"
        val desPlus = DESPlus()
        Assert.assertEquals(plainText, desPlus.decrypt(desPlus.encrypt(plainText)))
    }

    @Test
    fun testCustomKey() {
        val plainText = "123456"
        val desPlus = DESPlus("sprintbootseed")
        Assert.assertEquals(plainText, desPlus.decrypt(desPlus.encrypt(plainText)))
    }

}