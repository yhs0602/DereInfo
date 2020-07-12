package com.kyhsgeekcode.dereinfo.calc

import com.kyhsgeekcode.dereinfo.cardunit.Card
import com.kyhsgeekcode.dereinfo.cardunit.CardUnit
import com.kyhsgeekcode.dereinfo.cardunit.SkillModel
import com.kyhsgeekcode.dereinfo.equalsDelta
import com.kyhsgeekcode.dereinfo.model.*
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
        appeals = unit.calculateAppeal(guest, type, roomBonus) // vo, vi , da, life, skill
        val attributes = unit.cards.map {
            it.cardData.attribute
        }.toTypedArray()
        val isResonance = unit.isResonanceApplied(guest)
        val totalAppeal = appeals[0] + appeals[1] + appeals[2] + support
        val notes = difficulty.notes!!
        val totalNotes = notes.size
        val scorePerNote = ((ratio * totalAppeal) / totalNotes).roundToInt()
        life = appeals[4]
        val maxLife = life * 2
        val skillModels = arrayOfNulls<SkillModel>(5)
        for ((index, card) in unit.cards.withIndex()) {
            skillModels[index] =
                DereDatabaseHelper.theInstance.skillModels.find { it.id == card.cardData.skill_id }
        }
        val isWorking = booleanArrayOf(false, false, false, false, false)
        val lastWorkTiming = floatArrayOf(0f, 0f, 0f, 0f, 0f)
        var lastWorkSkill: Int = 0
        var strongestSkill = arrayOfNulls<Int>(4) // normal long flick slide
        var nextNoteTiming: Float = notes[0].time
        var processedNotes: Int = 0
        var time: Float = 0f
        var totalScore = 0
        var noteIndex: Int = 0

        while (noteIndex < totalNotes) {
            // 주기적으로 패널티를 먹이고 시작하는 스킬 처리
            // 스킬 발동!
            val skillsToActivate = skillModels.withIndex().filter { theValue ->
                val index = theValue.index
                val skillModel = theValue.value
                (time - lastWorkTiming[index]).equalsDelta(skillModel?.condition?.toFloat())
            }
            val skillsToDeactivate = skillModels.withIndex().filter { theValue ->
                val index = theValue.index
                val skillModel = theValue.value
                (time - lastWorkTiming[index]).equalsDelta(unit.cards[index].getSkillDuration())
            }
            for (skill in skillsToDeactivate) {
                // make working false
                isWorking[skill.index] = false
            }
            val isDamageGuard = skillsToActivate.asSequence().filter {
                it.value?.skill_type == 12 && !skillsToDeactivate.contains(it)
            }.toList().isNotEmpty()

            for (skill in skillsToActivate) {
                if (skill.value?.skill_type == 16) {
                    skillToMimic = lastSkillModel // solidify
                    val skillToMimicNonNull = skillToMimic ?: continue
                    if (unit.cards[skillToMimicNonNull.index].canWork(unit, guest, life)) {
                        if (skillToMimicNonNull.value.skill_trigger_type == 1) {
                            if (!isDamageGuard)
                                life -= skillToMimicNonNull.value.skill_trigger_value
                        }
                        isWorking[skill.index] = true
                        // avoid alternate from using anchor
//                        if(skill.value != null)
//                            workedSkills.add(skill.value!!)
                    }
                } else {
                    // check availability
                    if (unit.cards[skill.index].canWork(unit, guest, life)) {
                        // apply penalty
                        if (skill.value?.skill_trigger_type == 1) { // overload
                            if (!isDamageGuard)
                                life -= skill.value!!.skill_trigger_value // overload
                        }
                        // make working true
                        isWorking[skill.index] = true
                        lastSkillModel =
                            if (skill.value == null) null else IndexedValue(
                                skill.index,
                                skill.value!!
                            )
                        if (skill.value != null)
                            workedSkills.add(skill.value!!)
                    }

                }
                // update last work
                lastWorkTiming[skill.index] += skill.value?.condition?.toFloat() ?: 0f
            }


            val workingSkills = skillModels.withIndex().filter { isWorking[it.index] }
            val workingBoostSkills = workingSkills.filter { it.value?.isBoost() == true }
            // process notes
            var note = notes[processedNotes]
            while (note.time >= time) {
                val comboBonusBase = when ((processedNotes + 1) * 100 / totalNotes) {
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
                val scoreByCombo = (scorePerNote * comboBonusBase).roundToInt()
                val finalScore: Float
                if (isResonance) {
                    val (scoreBonus, comboBonus, lifeBonus) = calculateBonusResonance(
                        note,
                        workingSkills,
                        workingBoostSkills,
                        attributes
                    )
                    finalScore = scoreByCombo * scoreBonus * comboBonus
                    life += lifeBonus
                } else {
                    val (scoreBonus, comboBonus, lifeBonus) = calculateBonus(
                        note,
                        workingSkills,
                        workingBoostSkills,
                        attributes
                    )
                    finalScore = scoreByCombo * scoreBonus * comboBonus
                    life += lifeBonus
                }
                totalScore += finalScore.roundToInt()
                processedNotes++
                if (processedNotes < totalNotes)
                    note = notes[processedNotes]
            }


            // find the earliest event time
            val nextSkillUpdateIndex = lastWorkTiming.withIndex()
                .filter { it.value + (skillModels[it.index]?.condition ?: 0) > time }
                .minBy {
                    it.value + (skillModels[it.index]?.condition ?: 0)
                }?.index // 현재로부터 가장 먼저 발생하는 패널티의 타이밍
            val nextSkillUpdateTiming: Float = if (nextSkillUpdateIndex != null) {
                lastWorkTiming[nextSkillUpdateIndex] + (skillModels[nextSkillUpdateIndex]?.condition
                    ?: 0)
            } else {
                Float.MAX_VALUE
            }
            val nextSkillFinishIndex = lastWorkTiming.withIndex()
                .filter { it.value + unit.cards[it.index].getSkillDuration() > time }
                .minBy { it.value + unit.cards[it.index].getSkillDuration() }?.index
            val nextSkillFinishTiming: Float = if (nextSkillFinishIndex != null) {
                lastWorkTiming[nextSkillFinishIndex] + unit.cards[nextSkillFinishIndex].getSkillDuration()
            } else {
                Float.MAX_VALUE
            }
            // update time
            if (totalNotes > processedNotes) {
                time = minOf(nextNoteTiming, nextSkillUpdateTiming, nextSkillFinishTiming)
            }
        }
        return totalScore
    }

    private fun calculateBonus(
        note: Note,
        workingSkills: List<IndexedValue<SkillModel?>>,
        workingBoostSkills: List<IndexedValue<SkillModel?>>,
        attributes: Array<Int>
    ): Triple<Float, Float, Int> {
        val totalAvailableBonus = workingSkills.map { it ->
            val skillModel = it.value ?: return@map Triple(100f, 100f, 0f)
            if (skillModel.isBoost())
                return@map Triple(100f, 100f, 0f)
            val boostValues = getBoostValues(workingBoostSkills, skillModel, attributes)
            val boost1 = boostValues.map {
                it.first
            }.max()?.div(100f)
            val boost2 = boostValues.map {
                it.second
            }.max()?.div(100f)
            val boost3 = boostValues.map {
                it.third
            }.max()?.div(100f)
            val bonus = skillModel.getBonus(
                note,
                Judge.PERFECT,
                life,
                appeals,
                skillToMimic?.value,
                workedSkills,
                boost1,
                boost2,
                boost3
            )
            bonus
        }
        val scoreBonus = totalAvailableBonus.map {
            it.first
        }.max()?.div(100f) ?: 1f
        val comboBonus = totalAvailableBonus.map {
            it.second
        }.max()?.div(100f) ?: 1f
        val lifeBonus = totalAvailableBonus.map {
            it.third
        }.max()?.div(100f)?.roundToInt() ?: 0
        return Triple(scoreBonus, comboBonus, lifeBonus)
    }

    private fun getBoostValues(
        workingBoostSkills: List<IndexedValue<SkillModel?>>,
        skillModel: SkillModel,
        attributes: Array<Int>
    ): List<Triple<Int, Int, Int>> {
        return workingBoostSkills.map { boostSkill ->
            DereDatabaseHelper.theInstance.skillBoostModels.asSequence().filter { boostModel ->
                (boostModel.skill_value == boostSkill.value?.value)
                        && (boostModel.target_type == skillModel.skill_type)
                        && (boostModel.target_attribute == 0 || boostModel.target_attribute == attributes[boostSkill.index])
            }.map { boostModel ->
                Triple(
                    boostModel.boost_value_1,
                    boostModel.boost_value_2,
                    boostModel.boost_value_3
                )
            }.firstOrNull() ?: Triple(100, 100, 0)
        }
    }

    private fun calculateBonusResonance(
        note: Note,
        workingSkills: List<IndexedValue<SkillModel?>>,
        workingBoostSkills: List<IndexedValue<SkillModel?>>,
        attributes: Array<Int>
    ): Triple<Float, Float, Int> {
        val totalAvailableBonus = workingSkills.map { it ->
            val skillModel = it.value ?: return@map Triple(100f, 100f, 0f)
            if (skillModel.isBoost())
                return@map Triple(100f, 100f, 0f)
            val boostValues = getBoostValues(workingBoostSkills, skillModel, attributes)
            val boost1 = boostValues.sumBy {
                it.first
            }?.div(100f)
            val boost2 = boostValues.sumBy {
                it.second
            }?.div(100f)
            val boost3 = boostValues.sumBy {
                it.third
            }?.div(100f)
            val bonus = skillModel.getBonus(
                note,
                Judge.PERFECT,
                life,
                appeals,
                skillToMimic?.value,
                workedSkills,
                boost1,
                boost2,
                boost3
            )
            bonus
        }
        val scoreBonus = totalAvailableBonus.sumByDouble {
            it.first.toDouble()
        }?.div(100f) //?: 1f
        val comboBonus = totalAvailableBonus.sumByDouble {
            it.second.toDouble()
        }?.div(100f) // ?: 1f
        val lifeBonus = totalAvailableBonus.sumByDouble {
            it.third.toDouble()
        }?.div(100f)?.roundToInt() // ?: 0
        return Triple(scoreBonus.toFloat(), comboBonus.toFloat(), lifeBonus)
    }

    // TODO: 2020/07/10 Apply BOOST

//    private fun calculateScoreBonusResonance(
//        note: Note,
//        workingSkills: List<IndexedValue<SkillModel?>>
//    ): Float {
//        return 1.0f + workingSkills.sumByDouble {
//            (applyScoreBoostResonance(
//                workingSkills, it.value?.getScoreBonus(
//                    note,
//                    Judge.PERFECT,
//                    life,
//                    appeals,
//                    lastSkillModel,
//                    workedSkills
//                )
//            ).div(100.0)) - 1.0
//        }.toFloat()
//    }

//    private fun applyScoreBoostResonance(
//        workingSkills: List<IndexedValue<SkillModel?>>,
//        scoreBonus: Double?
//    ): Int {
//        if (scoreBonus == null)
//            return 100
//        var totalScoreBonus: Double = scoreBonus
//        var totalScoreBonusBoost: Double = 1.0
//        for (skill in workingSkills) {
//            if (skill.value == null)
//                continue
//            if (skill.value!!.isBoost()) {
//                val boostModel =
//                    DereDatabaseHelper.theInstance.skillValueToBoostModel[skill.value!!.value]
//                totalScoreBonus += ((boostModel?.getScoreBoost() ?: 100) - 100)
//            }
//        }
//        return (totalScoreBonus * totalScoreBonusBoost).roundToInt()
//    }

//    private fun calcualteComboBonusResonance(
//        note: Note,
//        workingSkills: List<IndexedValue<SkillModel?>>
//    ): Float {
//        return 1.0f + workingSkills.sumByDouble {
//            (it.value?.getComboBonus(
//                note,
//                Judge.PERFECT,
//                life,
//                appeals,
//                lastSkillModel,
//                workedSkills
//            )?.div(100.0) ?: 1.0) - 1.0
//        }.toFloat()
//    }


//    private fun calculateScoreBonus(
//        note: Note,
//        workingSkills: List<IndexedValue<SkillModel?>>
//    ): Float {
//        return 1.0f + (workingSkills.asSequence().map {
//            (it.value?.getScoreBonus(
//                note,
//                Judge.PERFECT,
//                life,
//                appeals,
//                lastSkillModel,
//                workedSkills
//            )?.div(100.0) ?: 1.0) - 1.0
//        }.max()?.toFloat() ?: 0.0f)
//    }
//
//    private fun calcualteComboBonus(
//        note: Note,
//        workingSkills: List<IndexedValue<SkillModel?>>
//    ): Float {
//        return 1.0f + (workingSkills.asSequence().map {
//            (it.value?.getComboBonus(
//                note,
//                Judge.PERFECT,
//                life,
//                appeals,
//                lastSkillModel,
//                workedSkills
//            )?.div(100.0) ?: 1.0) - 1.0
//        }.max()?.toFloat() ?: 0.0f)
//    }


    var life: Int = 0
    var appeals: Array<Int> = arrayOf()
    var lastSkillModel: IndexedValue<SkillModel>? = null
    private var skillToMimic: IndexedValue<SkillModel>? = null
    private val workedSkills = mutableSetOf<SkillModel>()

}
