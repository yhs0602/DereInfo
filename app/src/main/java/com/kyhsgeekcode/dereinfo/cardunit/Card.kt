package com.kyhsgeekcode.dereinfo.cardunit

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
    fun canWork(unit: CardUnit, guest: Card, life: Int): Boolean {
        when (skillModel.skill_trigger_type) {
            1 -> if (life <= skillModel.skill_trigger_value) // overload
                return false
            2 -> if (!unit.hasOnlyAttr(skillModel.skill_trigger_value))
                return false
            3 -> if (!(unit.hasAttr(1) && !unit.hasAttr(2) && unit.hasAttr(3)))
                return false
        }
        return true
    }

    // INCORRECT: It won't work on very first and very last
    fun isWorkingAt(time: Float): Boolean {
        val offset = time.rem(skillModel.condition)
        return 0 <= offset && offset <= getSkillDuration()
    }

    val skillModel: SkillModel =
        DereDatabaseHelper.theInstance.skillModels.find { it.id == cardData.skill_id }
            ?: throw Exception("Invalid skill")

    fun getSkillDuration() = (skillModel.available_time_type + 1) * (1.0f + 0.05f * skillLevel)

    fun getAppeals(): Array<Int> {
        return level2appeal(level)
    }

    // other, vo, vi, dan, all, life, skill
    fun level2appeal(level: Int): Array<Int> {
        val theRarity = Rarity.fromInt(cardData.rarity)
        val maxLevel = theRarity.maxLevel
        val result = Array(7) { 0 }
        with(cardData) {
            val vo =
                ceil(vocal_min + (vocal_max - vocal_min) * level / (maxLevel - 1.0)).toInt() + potential.vo
            val da =
                ceil(dance_min + (dance_max - dance_min) * level / (maxLevel - 1.0)).toInt() + potential.dan
            val vi =
                ceil(visual_min + (visual_max - visual_min) * level / (maxLevel - 1.0)).toInt() + potential.vi
            val life = hp_min + if (love == theRarity.maxLove) bonus_hp else 0
            result[1] = vo
            result[2] = vi
            result[3] = da
            result[4] = vo + da + vi
            result[5] = life
            result[6] = 0 // invalid
        }
        return result
    }

    val isSpecialTrained by lazy {
        Rarity.fromInt(cardData.rarity).isSpecialTrained
    }
}
