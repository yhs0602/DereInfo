package com.kyhsgeekcode.dereinfo.cardunit

data class CardModel(
    val id: Int,
    val name: String,
    val chara_id: Int,
    val rarity: Int,
    val attribute: Int, // 속성 Cu Co Pa
    val title_flag: Int,
    val evolution_id: Int, // after revolution(특훈)
    val series_id: Int,
    val pose: Int,
    val place: Int,
    val evolution_type: Int,
    val album_id: Int,
    val skill_id: Int,
    val leader_skill_id: Int,
    val grow_type: Int,
    val hp_min: Int,
    val vocal_min: Int,
    val dance_min: Int,
    val visual_min: Int,
    val hp_max: Int,
    val vocal_max: Int,
    val dance_max: Int,
    val visual_max: Int,
    val bonus_hp: Int,
    val bonus_vocal: Int,
    val bonus_dance: Int,
    val bonus_visual: Int
)
