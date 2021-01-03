package com.kyhsgeekcode.dereinfo.cardunit

enum class Rarity(val maxLevel: Int, val maxLove: Int) {
    SSR_(90, 600),
    SSR(80, 200),
    SR_(70, 300),
    SR(60, 100),
    R_(50, 150),
    R(40, 50),
    N_(30, 60),
    N(20, 20);

    val isSpecialTrained by lazy {
        setOf(
            SSR_,
            SR_,
            R_,
            N_
        ).contains(this)
    }

    companion object {
        fun fromInt(value: Int) = when (value) {
            1 -> N
            2 -> N_
            3 -> R
            4 -> R_
            5 -> SR
            6 -> SR_
            7 -> SSR
            8 -> SSR_
            else -> throw IllegalArgumentException()
        }
    }
}
