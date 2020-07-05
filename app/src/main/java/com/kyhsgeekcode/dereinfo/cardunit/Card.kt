package com.kyhsgeekcode.dereinfo.cardunit

import com.kyhsgeekcode.dereinfo.model.DereDatabaseHelper
import java.lang.Exception

class Card(
    val cardData: CardModel,
    var level: Int,
    var love: Int,
    var isTrained: Boolean,
    var skillLevel: Int,
    var potential: Potential,
    var owned: Boolean
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

    fun isWorkingAt(time: Float): Boolean {
        val offset = time.rem(skillModel.condition)
        return 0 <= offset && offset <= getSkillDuration()
    }

    val skillModel: SkillModel =
        DereDatabaseHelper.theInstance.skillModels.find { it.id == cardData.skill_id }
            ?: throw Exception("Invalid skill")

    fun getSkillDuration() = (skillModel.available_time_type + 1) * (1.0f + 0.05f * skillLevel)
}
