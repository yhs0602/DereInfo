package com.kyhsgeekcode.dereinfo.cardunit

import androidx.room.Entity

@Entity(tableName = "skill_motif_value")
data class SkillMotifValueModel(
    val id: Int,
    val motif_value: Int,
    val type_01_value: Int,
    val type_02_value: Int
)