package com.kyhsgeekcode.dereinfo.dao

import androidx.room.Dao
import androidx.room.Query
import com.kyhsgeekcode.dereinfo.cardunit.SkillMotifValueGrandModel

@Dao
interface SkillMotifValueGrandDao {
    @Query("SELECT * FROM skill_motif_value_grand")
    suspend fun getAllSkillMotifValueGrand(): List<SkillMotifValueGrandModel>
}