package com.kyhsgeekcode.dereinfo.cardunit

data class LeaderSkillModel(
    val id: Int,
    val name: String,
    val explain: String,
    val type: Int,  // 20: appeal 30: present, 40: fan 50: cross 60: unizon 70: resonance 80: yell
    val need_cute: Int,
    val need_cool: Int,
    val need_passion: Int,
    val target_attribute: Int, // cu co pa
    val target_param: Int, // vo da vi
    val up_type: Int,
    val up_value: Int,
    val special_id: Int, // for live party
    val target_attribute_2: Int,
    val target_param_2: Int,
    val up_type_2: Int,
    val up_value_2: Int,
    val need_skill_variation: Int, // resonance
    val param_limit: Int // resonance
)
