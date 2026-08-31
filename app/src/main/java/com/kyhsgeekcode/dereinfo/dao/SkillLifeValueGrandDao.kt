package com.kyhsgeekcode.dereinfo.dao

import androidx.room.Dao
import androidx.room.Query
import com.kyhsgeekcode.dereinfo.cardunit.SkillLifeValueGrandModel

@Dao
interface SkillLifeValueGrandDao {
    @Query("SELECT * FROM skill_life_value_grand")
    suspend fun getAllSkillLifeValueGrand(): List<SkillLifeValueGrandModel>
}