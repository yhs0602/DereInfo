package com.kyhsgeekcode.dereinfo.model

import java.io.Serializable

class OneMusic(val difficulties: Map<TW5Difficulty, OneDifficultyData>, val musicInfo: MusicInfo) :
    Serializable
