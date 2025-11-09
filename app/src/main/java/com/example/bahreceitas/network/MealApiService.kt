package com.example.bahreceitas.network

import com.example.bahreceitas.data.model.RespostaReceitas
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MealApiService {

    @GET("receitas/todas")
    suspend fun getReceitas(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): RespostaReceitas

    @GET("receitas/descricao")
    suspend fun buscarReceitas(@Query("descricao") termo: String): RespostaReceitas

    @GET("receitas/{id}")
    suspend fun getReceitaPorId(@Path("id") id: String): RespostaReceitas

    @GET("receitas/tipo/{tipo}")
    suspend fun getReceitasPorTipo(@Path("tipo") tipo: String): RespostaReceitas
}
