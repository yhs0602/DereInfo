package com.kyhsgeekcode.dereinfo.model


enum class FlickMode(value: Int) {
    None(0),
    Left(1),
    Right(2),
    Up(3),
    Down(4)
;
    companion object {
        fun fromStatus(status: Int): FlickMode = when (status) {
            0->None
            1->Left
            2->Right
            else->throw IllegalArgumentException("status=${status}")
        }
    }
}