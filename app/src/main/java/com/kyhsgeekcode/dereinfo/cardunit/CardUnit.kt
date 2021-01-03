package com.kyhsgeekcode.dereinfo.cardunit

import com.kyhsgeekcode.dereinfo.calc.CGCalc
import com.kyhsgeekcode.dereinfo.model.CircleType
import com.kyhsgeekcode.dereinfo.model.DereDatabaseHelper

class CardUnit(
    val cards: MutableList<Card>
) : IUnit {
    override fun calculateAppeal(
        guest: Card,
        type: CircleType,
        roomBonus: CGCalc.RoomBonus
    ): Array<Int> {
        val leader = cards[0]
        val leaderSkillModel = leader.leaderSkillModel
        val guestSkillModel = guest.leaderSkillModel
        // vo, vi, da, total, lif, skill; 1, 2, 3, 4, 5, 6
        val totalBoni = initBoniFromRoom()
        if (leaderSkillModel?.canApply(this, guest) == true) {
            val boni = leaderSkillModel.getBonusRatio(this)
        }
        if (guestSkillModel?.canApply(this, guest) == true) {

        }

        // TODO: 2020/07/10

        return arrayOf()
    }

    // TODO: 2020/07/10
    private fun initBoniFromRoom(): Array<IntArray> {
        return arrayOf()
    }

    fun hasAttr(attribute: Int): Boolean = cards.any { it.cardData.attribute == attribute }
    fun hasOnlyAttr(attribute: Int): Boolean = cards.all { it.cardData.attribute == attribute }


    fun countSkills(): Int = cards.groupBy { it.cardData.skill_id }.size
    fun isResonanceApplied(guest: Card): Boolean {
        val leader = cards[0]
        val leaderSkillModel = DereDatabaseHelper.theInstance.leaderSkillModels.find {
            it.id == leader.cardData.leader_skill_id
        }
        val guestSkillModel = DereDatabaseHelper.theInstance.leaderSkillModels.find {
            it.id == guest.cardData.leader_skill_id
        }
        if (LeaderSkillModel.RESONANCE_IDS.contains(leaderSkillModel?.id)) {
            if (leaderSkillModel?.canApply(this, guest) == true) {
                return true
            }
        }
        if (LeaderSkillModel.RESONANCE_IDS.contains(guestSkillModel?.id)) {
            if (guestSkillModel?.canApply(this, guest) == true) {
                return true
            }
        }
        return false
    }


    var calculatedVo: Int = 0
    var calculatedDan: Int = 0
    var calculatedVi: Int = 0
    var calculatedLife: Int = 0
    override fun setCard(order: Int, card: Card) {
        cards[order] = card
    }

    override fun getCard(order: Int): Card {
        return cards[order]
    }

    override fun setLeader(card: Card) {
        cards[0] = card
    }

    override fun getLeader(): Card {
        return cards[0]
    }

    override fun getCardCount(): Int {
        return cards.size
    }

    override fun listCards(): List<Card> {
        return cards
    }

    override fun getAppeal(): Array<Int> {
        TODO("Not yet implemented")
    }
}
