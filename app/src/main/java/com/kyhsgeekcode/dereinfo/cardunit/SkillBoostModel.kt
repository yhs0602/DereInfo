package com.kyhsgeekcode.dereinfo.cardunit

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skill_boost_type")
data class SkillBoostModel(
    @PrimaryKey val id: Int,
    val skill_value: Int, // this type (skill value)
    val target_type: Int, // target skill type
    val boost_value_1: Int, // multiply
    val boost_value_2: Int,
    val boost_value_3: Int,
    val target_attribute: Int // target
)
