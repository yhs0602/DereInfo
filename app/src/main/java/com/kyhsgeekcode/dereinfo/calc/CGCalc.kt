package com.kyhsgeekcode.dereinfo.calc

import com.kyhsgeekcode.dereinfo.cardunit.Card
import com.kyhsgeekcode.dereinfo.cardunit.CardUnit
import com.kyhsgeekcode.dereinfo.cardunit.SkillModel
import com.kyhsgeekcode.dereinfo.equalsDelta
import com.kyhsgeekcode.dereinfo.model.CircleType
import com.kyhsgeekcode.dereinfo.model.DereDatabaseHelper
import com.kyhsgeekcode.dereinfo.model.OneDifficulty
import kotlin.math.min
import kotlin.math.roundToInt

object CGCalc {

    fun calculateBestUnit(cardPool: List<Card>, difficulty: OneDifficulty, type: CircleType) {
        // 경우의 수: 센터 찾기, 나머지 4개 고르기
        // O(n* n-1C4) = O(n^5)
    }

    fun calcualteScore(
        unit: CardUnit,
        guest: Card,
        difficulty: OneDifficulty,
        type: CircleType,
        roomBonus: Array<Int>,
        support: Int,
        ratio: Float
    ): Int {
        val appeals: Array<Int> =
            unit.calculateAppeal(guest, type, roomBonus) // vo, vi , da, life, skill
        val totalAppeal = appeals[0] + appeals[1] + appeals[2] + support
        val notes = difficulty.notes!!
        val totalNotes = notes.size
        val scorePerNote = ((ratio * totalAppeal) / totalNotes).roundToInt()
        var comboBonus = 1.0f
        var life = appeals[4]
        val maxLife = life * 2
        val skillModels = arrayOfNulls<SkillModel>(5)
        for ((index, card) in unit.cards.withIndex()) {
            skillModels[index] =
                DereDatabaseHelper.theInstance.skillModels.find { it.id == card.cardData.skill_id }
        }
        val isWorking = booleanArrayOf(false, false, false, false, false)
        val lastWorkTiming = floatArrayOf(0f, 0f, 0f, 0f, 0f)
        val lastWorkSkills = ArrayList<Int>()
        var time: Float = 0f
        var nextSkillUpdateTiming: Float = 0f
        var totalScore = 0f
        var noteIndex: Int = 0
        while (noteIndex < totalNotes) {
            // update skill
            for (timing in lastWorkTiming) {

            }

            // handle notes
            val candidateNote = notes[noteIndex]
            if (time < candidateNote.time) {

            }
            comboBonus = when (noteIndex * 100 / totalNotes) {
                in 0 until 5 -> 1.0f
                in 5 until 10 -> 1.1f
                in 10 until 25 -> 1.2f
                in 25 until 50 -> 1.3f
                in 50 until 70 -> 1.4f
                in 70 until 80 -> 1.5f
                in 80 until 90 -> 1.7f
                in 90..100 -> 2.0f
                else -> 2.0f
            }
            val scoreByCombo = (scorePerNote * comboBonus).roundToInt()
            // 주기적으로 패널티를 먹이고 시작하는 스킬 처리
            // 스킬 발동!
            val skillsToActivate = skillModels.withIndex().filter { theValue ->
                val index = theValue.index
                val skillModel = theValue.value
                (time-lastWorkTiming[index]).equalsDelta(skillModel?.condition?.toFloat())
            }
            val skillsToDeactivate = skillModels.withIndex().filter { theValue ->
                val index = theValue.index
                val skillModel = theValue.value
                (time - lastWorkTiming[index]).equalsDelta(unit.cards[index].getSkillDuration())
            }
            for(skill in skillsToActivate) {
                // check availability
                if (unit.cards[skill.index].canWork(unit, guest, life)) {
                    // apply penalty
                    if (skill.value?.skill_trigger_type == 1) { // overload
                        life -= skill.value!!.skill_trigger_value // overload
                    }
                    // make working true
                    isWorking[skill.index] = true
                }
                // update last work
                lastWorkTiming[skill.index] += skill.value?.condition?.toFloat()?: 0f
            }

            for(skill in skillsToDeactivate) {
                // make working false
                isWorking[skill.index] = false
            }



            val finalScore = 0
            totalScore += finalScore
            val nextSkillUpdateIndex = lastWorkTiming.withIndex()
                .minBy { it.value + skillModels[it.index].condition }.index // 현재로부터 가장 먼저 발생하는 패널티의 타이밍
            nextSkillUpdateTiming =
                lastWorkTiming[nextSkillUpdateIndex] + skillModels[nextSkillUpdateIndex].condition
            if (totalNotes > i + 1) {
                time = min(notes[i + 1].time, nextSkillUpdateTiming)
            }
        }

    }
}