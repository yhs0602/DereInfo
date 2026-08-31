package com.kyhsgeekcode.dereinfo.dao

import androidx.room.Dao
import androidx.room.Query
import com.kyhsgeekcode.dereinfo.cardunit.SkillMotifValueModel

@Dao
interface SkillMotifValueDao {
    @Query("SELECT * FROM skill_motif_value")
    suspend fun getAllSkillMotifValue(): List<SkillMotifValueModel>
}