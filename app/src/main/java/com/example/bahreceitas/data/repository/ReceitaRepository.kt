package com.example.bahreceitas.data.repository

import com.example.bahreceitas.data.local.ReceitaDao
import com.example.bahreceitas.data.local.ReceitaEntity
import com.example.bahreceitas.data.model.Receita
import com.example.bahreceitas.network.MealApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReceitaRepository(
    private val receitaDao: ReceitaDao,
    private val apiService: MealApiService
) {

    fun getFavoritas(): Flow<List<Receita>> {
        return receitaDao.getAllFavoritas().map { lista ->
            lista.map { it.toReceita() }
        }
    }

    suspend fun adicionarFavorita(receita: Receita) {
        receitaDao.insert(ReceitaEntity.fromReceita(receita))
    }

    suspend fun removerFavorita(receita: Receita) {
        receitaDao.deleteById(receita.id)
    }

    fun isFavorita(receitaId: String): Flow<Boolean> {
        return receitaDao.isFavorita(receitaId)
    }

    suspend fun getReceitaAleatoria(): Result<Receita> {
        return try {
            val resposta = apiService.getReceitaAleatoria()
            val receitas = resposta.receitas?.map { it.toReceita() } ?: emptyList()
            if (receitas.isNotEmpty()) {
                Result.success(receitas.first())
            } else {
                Result.failure(Exception("Nenhuma receita encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarReceitas(termo: String): Result<List<Receita>> {
        return try {
            val resposta = apiService.buscarReceitas(termo)
            val receitas = resposta.receitas?.map { it.toReceita() } ?: emptyList()
            Result.success(receitas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReceitaPorId(id: String): Result<Receita> {
        return try {
            val resposta = apiService.getReceitaPorId(id)
            val receitas = resposta.receitas?.map { it.toReceita() } ?: emptyList()
            if (receitas.isNotEmpty()) {
                Result.success(receitas.first())
            } else {
                Result.failure(Exception("Receita não encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
