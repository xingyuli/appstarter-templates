package @packageName@.infra.service

enum class Sign {

    PLUS {
        override fun actionOn(number: Int): Int = number
    },

    MINUS {
        override fun actionOn(number: Int): Int = -number
    };

    abstract fun actionOn(number: Int): Int

}
