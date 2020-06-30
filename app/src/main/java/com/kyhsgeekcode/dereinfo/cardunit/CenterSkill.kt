package com.kyhsgeekcode.dereinfo.cardunit

import com.kyhsgeekcode.dereinfo.model.CircleType

enum class ApplyType {
    Vo,
    Dan,
    Vi,
    Life,
    SkillRate,
    Fan,
    Money,
    Exp,
    Present,
    Resonance
}

enum class CenterSkillType {
    None,
    AllUp,
    Vo,
    Dan,
    Vi,
    Life,

}

enum class CenterSkill(
    val engName: String,
    val rarity: Rarity,
    val type: CenterSkillType,
    val requireCircularType: List<CircleType>?, // tricol, cross
    val requireNotCircularType: List<CircleType>?, // princess
    val targetCircularType: List<CircleType>,
    val applyEffects: List<Pair<ApplyType, Float>>, // 곱하기
    val applyEffects2: List<Pair<ApplyType, Float>>? // 조건만족못했을 때
) {
    BrillianceCuteSSR(
        "Cute Brilliance", Rarity.SSR, CenterSkillType.AllUp, null, null, listOf(CircleType.Cute),
        listOf(Pair(ApplyType.Vo, 1.4f), Pair(ApplyType.Dan, 1.4f), Pair(ApplyType.Vi, 1.4f)), null
    ),
    BrillianceCuteSR(
        "Cute Brilliance", Rarity.SR, CenterSkillType.AllUp, null, null, listOf(CircleType.Cute),
        listOf(Pair(ApplyType.Vo, 1.3f), Pair(ApplyType.Dan, 1.3f), Pair(ApplyType.Vi, 1.3f)), null
    ),
    BrillianceCoolSSR(
        "Cool Brilliance", Rarity.SSR, CenterSkillType.AllUp, null, null, listOf(CircleType.Cool),
        listOf(Pair(ApplyType.Vo, 1.4f), Pair(ApplyType.Dan, 1.4f), Pair(ApplyType.Vi, 1.4f)), null
    ),
    BrillianceCoolSR(
        "Cool Brilliance", Rarity.SR, CenterSkillType.AllUp, null, null, listOf(CircleType.Cool),
        listOf(Pair(ApplyType.Vo, 1.3f), Pair(ApplyType.Dan, 1.3f), Pair(ApplyType.Vi, 1.3f)), null
    ),
    BrilliancePassionSSR(
        "Passion Brilliance", Rarity.SSR, CenterSkillType.AllUp, null, null, listOf(CircleType.Passion),
        listOf(Pair(ApplyType.Vo, 1.4f), Pair(ApplyType.Dan, 1.4f), Pair(ApplyType.Vi, 1.4f)), null
    ),
    BrilliancePassionSR(
        "Passion Brilliance", Rarity.SR, CenterSkillType.AllUp, null, null, listOf(CircleType.Passion),
        listOf(Pair(ApplyType.Vo, 1.3f), Pair(ApplyType.Dan, 1.3f), Pair(ApplyType.Vi, 1.3f)), null
    ),

}
