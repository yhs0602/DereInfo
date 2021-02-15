package com.kyhsgeekcode.dereinfo.cardunit

import com.kyhsgeekcode.dereinfo.calc.CGCalc
import com.kyhsgeekcode.dereinfo.model.CircleType

interface IUnit {
    operator fun set(order: Int, card: Card)
    operator fun get(order: Int): Card
    fun setLeader(card: Card)
    fun getLeader(): Card
    fun getCardCount(): Int
    fun listCards(): List<Card>
    fun calculateAppeal(guest: Card, type: CircleType, roomBonus: CGCalc.RoomBonus): Array<Int>
}
