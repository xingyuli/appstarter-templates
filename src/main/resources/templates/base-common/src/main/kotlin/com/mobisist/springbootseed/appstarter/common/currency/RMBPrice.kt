package com.mobisist.springbootseed.appstarter.common

import java.math.BigDecimal
import java.math.RoundingMode

// NOTE: not hash compatible!!!
data class RMBPrice(val amount: BigDecimal, val unit: RMBUnit) {

    fun to(unit: RMBUnit): RMBPrice {
        if (this.unit == unit) {
            return this
        }
        return RMBPrice(amount.multiply(BigDecimal(this.unit.getRatioTo(unit)).setScale(2, RoundingMode.HALF_UP)), unit)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is RMBPrice) {
            return false
        }

        if (this === other) {
            return true
        }

        // unify the unit at first
        val that = other.to(unit)

        // then unify the scale, because BigDecimal's equals requires scala equal
        val thisWithUnifiedScale: BigDecimal
        val thatWithUnifiedScale: BigDecimal
        if (this.amount.scale() > that.amount.scale()) {
            thisWithUnifiedScale = this.amount
            thatWithUnifiedScale = that.amount.setScale(this.amount.scale(), RoundingMode.UNNECESSARY)
        } else if (this.amount.scale() < that.amount.scale()) {
            thisWithUnifiedScale = this.amount.setScale(that.amount.scale(), RoundingMode.UNNECESSARY)
            thatWithUnifiedScale = that.amount
        } else {
            thisWithUnifiedScale = this.amount
            thatWithUnifiedScale = that.amount
        }

        return thisWithUnifiedScale == thatWithUnifiedScale
    }

    override fun toString(): String = "$amount ${unit.label}"

}

enum class RMBUnit(val label: String) {

    YUAN("元") {
        override fun getRatioTo(unit: RMBUnit): Double = when(unit) {
            YUAN -> 1.0
            JIAO -> 10.0
            FEN -> 100.0
        }
    },

    JIAO("角") {
        override fun getRatioTo(unit: RMBUnit): Double = when(unit) {
            YUAN -> 0.1
            JIAO -> 1.0
            FEN -> 10.0
        }
    },

    FEN("分") {
        override fun getRatioTo(unit: RMBUnit): Double = when(unit) {
            YUAN -> 0.01
            JIAO -> 0.1
            FEN -> 1.0
        }
    };

    // multiply ratio
    abstract fun getRatioTo(unit: RMBUnit): Double

}

fun BigDecimal.rmbConversion(from: RMBUnit, to: RMBUnit): BigDecimal = RMBPrice(this, from).to(to).amount
