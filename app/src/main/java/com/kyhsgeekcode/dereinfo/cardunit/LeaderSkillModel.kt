package com.kyhsgeekcode.dereinfo.cardunit

import kotlin.math.max

data class LeaderSkillModel(
    val id: Int,
    val name: String,
    val explain: String,
    val type: Int,  // 20: appeal 30: present, 40: fan 50: cross 60: unizon 70: resonance 80: yell
    val need_cute: Int,
    val need_cool: Int,
    val need_passion: Int,
    val target_attribute: Int, // cu co pa
    val target_param: Int, // vo da vi
    val up_type: Int,
    val up_value: Int,
    val special_id: Int, // for live party
    val target_attribute_2: Int,
    val target_param_2: Int,
    val up_type_2: Int,
    val up_value_2: Int,
    val need_skill_variation: Int, // resonance
    val param_limit: Int // resonance
) {
    fun canApply(unit: CardUnit, guest: Card): Boolean {
        if ((need_cute >= 1) and !(unit.hasAttr(ATTR_CUTE) || guest.cardData.attribute == ATTR_CUTE))
            return false
        if ((need_cool >= 1) and !(unit.hasAttr(ATTR_COOL) || guest.cardData.attribute == ATTR_COOL))
            return false
        if ((need_passion >= 1) and !(unit.hasAttr(ATTR_PASSION) || guest.cardData.attribute == ATTR_PASSION))
            return false
        if ((need_cute == 6) and !(unit.hasOnlyAttr(ATTR_CUTE) && guest.cardData.attribute == ATTR_CUTE)) // pricess
            return false
        if ((need_cool == 6) and !(unit.hasOnlyAttr(ATTR_COOL) && guest.cardData.attribute == ATTR_COOL)) // pricess
            return false
        if ((need_passion == 6) and !(unit.hasOnlyAttr(ATTR_PASSION) && guest.cardData.attribute == ATTR_PASSION)) // pricess
            return false
        if ((countSkills(unit, guest) < need_skill_variation))
            return false
        return true
    }

    private fun countSkills(unit: CardUnit, guest: Card): Int {
        val cards = unit.cards.map { it.skillModel?.id ?: 0 }.toMutableList()
        cards.add(guest.cardData.skill_id)
        return cards.groupBy { it }.size
    }

    // other, vo, vi, dan, all, life, skill
    fun getBonusRatio(cardUnit: CardUnit): Array<Appeal> {
        val resultArray = arrayOfNulls<Appeal>(cardUnit.cards.size)
        for ((index, card) in cardUnit.cards.withIndex()) {
            val cardData = card.cardData
            // other, vo, vi, dan, all, life, skill,
            val bonus = arrayOf(100, 100, 100, 100, 100, 100, 100)
            if (cardData.attribute == target_attribute || target_attribute == ATTR_ALL) {
                bonus[target_param] += up_value
            }
            if (cardData.attribute == target_attribute_2 || target_attribute_2 == ATTR_ALL) {
                if (target_param_2 <= 6 && up_type_2 == 1) // ignore cute cross xxx fans(param_2 == 12)
                    bonus[target_param_2] += up_value_2
            }

            if (cardData.attribute + 10 == target_attribute_2) { // unizon: Just apply
                bonus[target_param_2] = 100 + up_value_2
            }

            if (bonus[4] > 0) { // all appeal
                for (i in 1..3) {
                    bonus[i] += bonus[4]
                }
                bonus[4] = 0 // remove all appeal bonus
            }

            if (param_limit > 0) { // resonance
                for (i in 1..3)
                    if (param_limit != i) {
                        bonus[i] -= 100
                        bonus[i] = max(bonus[i], 0)
                    }
            }
            resultArray[index] = Appeal(bonus)
        }
        return resultArray as Array<Appeal>
    }

    companion object {
        val ID_RESONANCE_VOICE = 104
        val ID_RESONANCE_STEP = 105
        val ID_RESONANCE_MAKE = 106
        val ATTR_CUTE = 1
        val ATTR_COOL = 2
        val ATTR_PASSION = 3
        val ATTR_ALL = 4
        val RESONANCE_IDS = arrayOf(ID_RESONANCE_MAKE, ID_RESONANCE_STEP, ID_RESONANCE_VOICE)
    }
}
