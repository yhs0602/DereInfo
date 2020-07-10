package com.kyhsgeekcode.dereinfo.cardunit

import com.kyhsgeekcode.dereinfo.model.DereDatabaseHelper
import com.kyhsgeekcode.dereinfo.model.Judge
import com.kyhsgeekcode.dereinfo.model.Note
import kotlin.math.roundToInt

data class SkillModel(
    val id: Int,
    val skill_name: String,
    val explain: String,
    val skill_type: Int,
    val judge_type: Int,
    val skill_trigger_type: Int, // 3: tricol, 2: focus, 1: overload
    val skill_trigger_value: Int, // overload, focus type
    val cutin_type: Int,
    val condition: Int, // time
    val value: Int,
    val probability_type: Int, //2 3 4
    val available_time_type: Int, // i wa su si ka
    val value_2: Int,
    val value_3: Int
) {
    fun getScoreBonus(note: Note, judge: Judge, life: Int, appeals: Array<Int>, lastSkillModel: SkillModel?, strongestSkillModel: SkillModel?): Double {
        return when(skill_type) {
            1 -> if(judge == Judge.PERFECT) value else 0
            2 -> if(judge == Judge.PERFECT || judge == Judge.GREAT) value else 0
            14 -> if(judge == Judge.PERFECT || judge == Judge.GREAT) value else 0
            15 -> if(judge == Judge.PERFECT) value else 0
            16 -> lastSkillModel?.getScoreBonus(note, judge, life, appeals, lastSkillModel, strongestSkillModel)?.toInt()?: 0
            21, 22, 23 -> if(judge == Judge.PERFECT) value else 0
            26 -> if(judge == Judge.PERFECT) value else 0
            27 -> if(judge == Judge.PERFECT) value else 0
            28 -> if(judge == Judge.PERFECT) if(note.isLong()) value_2 else value else 0
            29 -> if(judge == Judge.PERFECT) if(note.isFlick()) value_2 else value else 0
            30 -> if(judge == Judge.PERFECT) if(note.isSlide()) value_2 else value else 0
            35 -> if(judge == Judge.PERFECT) DereDatabaseHelper.theInstance.motifBonus(appeals[1],value) else 0
            36 -> if(judge == Judge.PERFECT) DereDatabaseHelper.theInstance.motifBonus(appeals[3],value) else 0
            37 -> if(judge == Judge.PERFECT) DereDatabaseHelper.theInstance.motifBonus(appeals[2],value) else 0
            39 -> ((strongestSkillModel?.getScoreBonus(note, judge, life, appeals, lastSkillModel, strongestSkillModel)?.toInt()?:0) * value_2 / 100.0).roundToInt()
            else -> 0
        }.toDouble()
    }

    fun getComboBonus(note: Note, judge: Judge, life: Int, appeals: Array<Int>, lastSkillModel: SkillModel?, strongestSkillModel: SkillModel?) : Double {
        return when(skill_type) {
            4 -> value
            16 -> lastSkillModel?.getComboBonus(note, judge, life, appeals, lastSkillModel, strongestSkillModel) ?.toInt() ?: 0
            21, 22, 23 -> value_2
            24 -> value
            25 -> DereDatabaseHelper.theInstance.lifeSparkleBonus(life, value)
            26 -> value_2
            27 -> value_2
            31 -> value
            39 -> value // ?
            else -> 0
        }.toDouble()
    }
    // skill_type에 대한 가설
    // 1, 2 : 스코어, 스/ 쓰알    스코어
    // 4: 콤보 보너스 4                  콤보
    // 5. 판강 레어                             판정
    // 6: 판강 스알                             판정
    // 7: 판강 쓰알 4 + 2 + 1                   판정
    // //8: 미스까지 커버되는 판강
    // 9: 콤보 유지                                 콤보유지
    // 12: 데미지 가드                                       라이프
    // 14: 오버로드 8 + 4 + 2    스코어               콤보유지  라이프
    // 15: 컨센 8+ 4+ 2 + 1     스코어                               판정
    // 16: 앵콜 16
    // 17: 라이프회복 16 + 1                                 라이프
    // 20: 부스트
    // 21~23: 포커스            스코어   콤보
    // 24: 올라운드                     콤보                  라이프
    // 25: 라이프스파클                  콤보
    // 26: 트리콜로로 시너지        스코어  콤보                  라이프
    // 27: 코디네이트            스코어   콤보
    // 28: 롱 액트             스코어 1, 2
    // 29: 플릭 액트            스코어 1, 2
    // 30: 슬라이드 액트        스코어 1, 2
    // 31: 튜닝                       콤보     판정
    // 32~34: 앙상블
    // 35~37: 모티프           스코어
    // 38: 심포니
    // 39: 얼터네이트          콤보 스코어
    // 그런 거 없었다.
}
