package com.kyhsgeekcode.dereinfo.cardunit

import androidx.room.Entity
import com.kyhsgeekcode.dereinfo.enums.CircleType

@Entity(tableName = "card_data")
class CardData(
    val name: String,
    val type: CircleType,
    val rarity: Rarity,
    val vo: Int,
    val dan: Int,
    val vi: Int,
    val life: Int,
    val center: CenterSkill,
    val maxLevel1: Int,
    val maxLevel2: Int,
    val maxLove1: Int,
    val maxLove2: Int,
    val isFes: Boolean,
    val isLimited: Boolean,
    val isEvent: Boolean,
    val skill: Skill
)
