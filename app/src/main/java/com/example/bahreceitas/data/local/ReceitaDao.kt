package com.example.bahreceitas.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceitaDao {

    @Query("SELECT * FROM receitas_favoritas ORDER BY nome")
    fun getAllFavoritas(): Flow<List<ReceitaEntity>>

    @Query("SELECT * FROM receitas_favoritas WHERE id = :receitaId")
    suspend fun getById(receitaId: String): ReceitaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(receita: ReceitaEntity)

    @Delete
    suspend fun delete(receita: ReceitaEntity)

    @Query("DELETE FROM receitas_favoritas WHERE id = :receitaId")
    suspend fun deleteById(receitaId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM receitas_favoritas WHERE id = :receitaId)")
    fun isFavorita(receitaId: String): Flow<Boolean>
}
