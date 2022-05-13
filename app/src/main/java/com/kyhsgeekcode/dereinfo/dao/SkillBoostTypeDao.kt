package com.kyhsgeekcode.dereinfo.dao

import androidx.room.Dao
import androidx.room.Query
import com.kyhsgeekcode.dereinfo.cardunit.SkillBoostModel

@Dao
interface SkillBoostTypeDao {
    @Query("SELECT * FROM skill_boost_type")
    suspend fun getAllSkillBoostType(): List<SkillBoostModel>
}