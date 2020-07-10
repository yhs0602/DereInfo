package com.kyhsgeekcode.dereinfo.cardunit

import com.kyhsgeekcode.dereinfo.model.CircleType
import com.kyhsgeekcode.dereinfo.model.DereDatabaseHelper
import java.util.jar.Attributes

class CardUnit(
    val cards: List<Card>
) {
    fun calculateAppeal(guest: Card, type: CircleType, roomBonus: Array<Int>): Array<Int> {
        val leader = cards[0]
        val leaderSkillModel = DereDatabaseHelper.theInstance.leaderSkillModels.find {
            it.id == leader.cardData.leader_skill_id
        }
        val guestSkillModel = DereDatabaseHelper.theInstance.leaderSkillModels.find{
            it.id== guest.cardData.leader_skill_id
        }
        // vo, vi, da, total, lif, skill; 1, 2, 3, 4, 5, 6
        val totalBoni = initBoniFromRoom()
        if (leaderSkillModel?.canApply(this, guest) == true) {
            val boni = leaderSkillModel.getBonusRatio(this)
        }
        if(guestSkillModel?.canApply(this, guest) == true) {

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
        if(LeaderSkillModel.RESONANCE_IDS.contains(leaderSkillModel?.id)) {
            if(leaderSkillModel?.canApply(this, guest) == true ) {
                return true
            }
        }
        if(LeaderSkillModel.RESONANCE_IDS.contains(guestSkillModel?.id)) {
            if(guestSkillModel?.canApply(this, guest) == true ) {
                return true
            }
        }
        return false
    }


    var calculatedVo: Int = 0
    var calculatedDan: Int = 0
    var calculatedVi: Int = 0
    var calculatedLife: Int = 0

}
