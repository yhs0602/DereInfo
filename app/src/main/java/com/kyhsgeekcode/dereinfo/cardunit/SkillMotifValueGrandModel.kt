package com.kyhsgeekcode.dereinfo.cardunit

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skill_motif_value_grand")
data class SkillMotifValueGrandModel(
    @PrimaryKey override val id: Int,
    override val motif_value: Int,
    override val type_01_value: Int,
    override val type_02_value: Int
) : ISkillMotifValueModel
