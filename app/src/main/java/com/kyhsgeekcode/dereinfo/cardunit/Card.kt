package com.kyhsgeekcode.dereinfo.cardunit

import com.kyhsgeekcode.dereinfo.model.CircleType
import com.kyhsgeekcode.dereinfo.model.DereDatabaseHelper
import kotlin.math.ceil

class Card(
    val cardData: CardModel,
    var level: Int,
    var love: Int,
    var skillLevel: Int,
    var potential: Potential,
    var owned: Boolean = false
) {
    val theRarity = Rarity.fromInt(cardData.rarity)
    val maxLevel = theRarity.maxLevel
    val isSpecialTrained = Rarity.fromInt(cardData.rarity).isSpecialTrained

    val isMaxLove: Boolean
        get() = love == theRarity.maxLove

    val vocal: Int
        get() = ceil(cardData.vocal_min + (cardData.vocal_max - cardData.vocal_min) * level / (maxLevel - 1.0)).toInt() +
                potential.vo + (if (isMaxLove) cardData.bonus_vocal else 0)

    val dance: Int
        get() = ceil(cardData.dance_min + (cardData.dance_max - cardData.dance_min) * level / (maxLevel - 1.0)).toInt() +
                potential.dan + (if (isMaxLove) cardData.bonus_dance else 0)

    val visual: Int
        get() = ceil(cardData.visual_min + (cardData.visual_max - cardData.visual_min) * level / (maxLevel - 1.0)).toInt() +
                potential.vi + (if (isMaxLove) cardData.bonus_visual else 0)

    val life: Int
        get() = cardData.hp_min + if (isMaxLove) cardData.bonus_hp else 0

    val skillModel: SkillModel? =
        DereDatabaseHelper.theInstance.skillModels.find { it.id == cardData.skill_id }

    val leaderSkillModel: LeaderSkillModel? =
        DereDatabaseHelper.theInstance.leaderSkillModels.find { it.id == cardData.leader_skill_id }

    val skillDuration: Float
        get() = if (skillModel == null) 0.0f else (skillModel.available_time_type + 1) * (1.0f + 0.05f * skillLevel)

    val circleType = CircleType.fromInt(cardData.attribute)

    fun canWork(unit: CardUnit, guest: Card, life: Int): Boolean {
        skillModel?.let {
            when (it.skill_trigger_type) {
                1 -> if (life <= it.skill_trigger_value) // overload
                    return false
                2 -> if (!unit.hasOnlyAttr(it.skill_trigger_value))
                    return false
                3 -> if (!(unit.hasAttr(1) && !unit.hasAttr(2) && unit.hasAttr(3)))
                    return false
            }
        }
        return true
    }

    // INCORRECT: It won't work on very first and very last
    fun isWorkingAt(time: Float): Boolean {
        if (skillModel == null)
            return false
        val offset = time.rem(skillModel.condition)
        return offset in 0.0f..skillDuration
    }
}
