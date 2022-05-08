package com.kyhsgeekcode.dereinfo.cardunit

import androidx.room.Entity

@Entity(tableName = "skill_life_value_grand")
data class SkillLifeValueGrandModel(
    val id: Int,
    val life_value: Int,
    val type_01_value: Int,
    val type_02_value: Int,
    val type_03_value: Int
)