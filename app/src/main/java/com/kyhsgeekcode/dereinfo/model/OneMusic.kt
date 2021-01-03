package com.kyhsgeekcode.dereinfo.model

import java.io.Serializable

class OneMusic(val difficulties: Map<TW5Difficulty, OneDifficulty>, val musicInfo: MusicInfo) :
    Serializable
