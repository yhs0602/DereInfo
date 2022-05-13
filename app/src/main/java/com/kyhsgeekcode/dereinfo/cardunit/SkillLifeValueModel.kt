package com.kyhsgeekcode.dereinfo.cardunit

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skill_life_value")
data class SkillLifeValueModel(
    @PrimaryKey val id: Int,
    override val life_value: Int,
    override val type_01_value: Int,
    override val type_02_value: Int,
    override val type_03_value: Int
) : ISkillLifeValueModel