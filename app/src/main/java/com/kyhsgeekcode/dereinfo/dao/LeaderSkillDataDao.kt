package com.kyhsgeekcode.dereinfo.dao

import androidx.room.Dao
import androidx.room.Query
import com.kyhsgeekcode.dereinfo.cardunit.LeaderSkillModel

@Dao
interface LeaderSkillDataDao {
    @Query("SELECT * FROM leader_skill_data")
    suspend fun getAllLeaderSkillData(): List<LeaderSkillModel>
}