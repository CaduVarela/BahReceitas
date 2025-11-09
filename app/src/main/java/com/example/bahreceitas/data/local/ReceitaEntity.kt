package com.example.bahreceitas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bahreceitas.data.model.Receita

@Entity(tableName = "receitas_favoritas")
data class ReceitaEntity(
    @PrimaryKey
    val id: String,
    val nome: String,
    val categoria: String,
    val regiao: String,
    val instrucoes: String,
    val imagemUrl: String,
    val linkYoutube: String?,
    val ingredientes: String
) {
    fun toReceita(): Receita {
        return Receita(
            id = id,
            nome = nome,
            categoria = categoria,
            regiao = regiao,
            instrucoes = instrucoes,
            imagemUrl = imagemUrl,
            linkYoutube = linkYoutube,
            ingredientes = ingredientes
        )
    }

    companion object {
        fun fromReceita(receita: Receita): ReceitaEntity {
            return ReceitaEntity(
                id = receita.id,
                nome = receita.nome,
                categoria = receita.categoria,
                regiao = receita.regiao,
                instrucoes = receita.instrucoes,
                imagemUrl = receita.imagemUrl,
                linkYoutube = receita.linkYoutube,
                ingredientes = receita.ingredientes
            )
        }
    }
}
