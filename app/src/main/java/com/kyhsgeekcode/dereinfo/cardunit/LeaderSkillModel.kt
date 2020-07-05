package com.kyhsgeekcode.dereinfo.cardunit

import kotlin.math.max
import kotlin.reflect.jvm.internal.impl.resolve.constants.IntegerLiteralTypeConstructor

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
    fun canApply(unit: CardUnit) : Boolean {
        if((need_cute >= 1) and !unit.hasAttr(1))
                return false
        if((need_cool >= 1) and !unit.hasAttr(2))
                return false
        if((need_passion >= 1) and !unit.hasAttr(3))
                return false
        if((need_cute == 6) and !unit.hasOnlyAttr(1)) // pricess
            return false
        if((need_cool == 6) and !unit.hasOnlyAttr(2)) // pricess
            return false
        if((need_passion == 6) and !unit.hasOnlyAttr(3)) // pricess
            return false
        if((unit.countSkills() < need_skill_variation))
            return false
        return true
    }

    fun getBonusRatio(cardUnit: CardUnit): Array<IntArray> {
        val resultArray = Array<IntArray>(cardUnit.cards.size) { IntArray(7) }
        for((index, card) in cardUnit.cards.withIndex()) {
            val cardData = card.cardData
            // other, vo, vi, dan, all, life, skill,
            val bonus = intArrayOf(100, 100, 100, 100, 100, 100, 100)
            if(cardData.attribute == target_attribute || target_attribute == 4) {
                bonus[target_param] += up_value
            }
            if(cardData.attribute == target_attribute_2 ||  target_attribute_2 == 4) {
                if(target_attribute_2 <= 6 && up_type_2 == 1) // cross fans
                    bonus[target_attribute_2] += up_value_2
            }

            if(cardData.attribute + 10 == target_attribute_2) { // unizon
                bonus[target_param_2] = up_value_2
            }

            if(bonus[4] > 0) {
                for(i in 1..3) {
                    bonus[i] += bonus[4]
                }
                bonus[4] = 0
            }

            if(param_limit > 0) {
                for(i in 1..3)
                    if(param_limit != i) {
                        bonus[i] -= 100
                        bonus[i] = max(bonus[i], 0)
                    }
            }
            resultArray[index] = bonus
        }
        return resultArray
    }
}
