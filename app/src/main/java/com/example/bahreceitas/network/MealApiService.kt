package com.example.bahreceitas.network

import com.example.bahreceitas.data.model.RespostaReceitas
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MealApiService {

    @GET("random.php")
    suspend fun getReceitaAleatoria(): RespostaReceitas

    @GET("search.php")
    suspend fun buscarReceitas(@Query("s") termo: String): RespostaReceitas

    @GET("lookup.php")
    suspend fun getReceitaPorId(@Query("i") id: String): RespostaReceitas
}
