package com.kyhsgeekcode.dereinfo.api

import retrofit2.http.GET
import retrofit2.http.Path

val server_url = "https://agile-spire-57059.herokuapp.com/"

data class Game(val name: String)

data class Difficulty(val name: String)

data class Song(
    val name: String,
    val active: Boolean,
    val metadata: String,
    val createdAt: String,
    val updatedAt: String
)

data class Sheet(
    val data: String,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val difficultyId: Int,
    val songId: Int
)

interface API {
    @GET("game")
    suspend fun getGames(): List<Game>

    @GET("game/{id}")
    suspend fun getGame(@Path("id") id: String)

    // @POST("game")
    @GET("game/{songId}/songs")
    suspend fun getSongs(@Path("songId") songId: Int)



}