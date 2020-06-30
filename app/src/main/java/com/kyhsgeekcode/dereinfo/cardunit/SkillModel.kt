package com.kyhsgeekcode.dereinfo.cardunit

data class SkillModel(
    val id: Int,
    val name: String,
    val explain: String,
    val skill_type: Int,
    val judge_type: Int,
    val skill_trigger_type: Int, // 3: tricol, 2: focus, 1: overload
    val skill_trigger_value: Int, // overload, focus type
    val cutin_type: Int,
    val condition: Int, // time
    val value: Int,
    val probability_type: Int, //2 3 4
    val available_time_type: Int, // i wa su si ka
    val value_2: Int,
    val value_3: Int
)
