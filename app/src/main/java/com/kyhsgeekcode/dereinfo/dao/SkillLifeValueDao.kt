package com.kyhsgeekcode.dereinfo.dao

import androidx.room.Dao
import androidx.room.Query
import com.kyhsgeekcode.dereinfo.cardunit.SkillLifeValueModel

@Dao
interface SkillLifeValueDao {
    @Query("SELECT * FROM skill_life_value")
    suspend fun getAllSkillLifeValue(): List<SkillLifeValueModel>
}