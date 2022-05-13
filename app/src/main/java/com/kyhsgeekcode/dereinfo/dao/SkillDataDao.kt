package com.kyhsgeekcode.dereinfo.dao

import androidx.room.Dao
import androidx.room.Query
import com.kyhsgeekcode.dereinfo.cardunit.SkillModel

@Dao
interface SkillDataDao {
    @Query("SELECT * FROM skill_data")
    suspend fun getAllSkillData(): List<SkillModel>

    @Query("select * from skill_data where id = :skillId")
    suspend fun getSkillData(skillId: Int): SkillModel
}