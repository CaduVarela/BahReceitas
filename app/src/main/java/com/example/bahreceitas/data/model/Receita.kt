package com.example.bahreceitas.data.model

import com.google.gson.annotations.SerializedName

data class RespostaReceitas(
    @SerializedName("items")
    val receitas: List<ReceitaApi>?,
    @SerializedName("meta")
    val meta: Meta?
)

data class Meta(
    @SerializedName("totalItems")
    val totalItems: Int?,
    @SerializedName("currentPage")
    val currentPage: Int?,
    @SerializedName("totalPages")
    val totalPages: Int?
)

data class ReceitaApi(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("receita")
    val nome: String?,
    @SerializedName("ingredientes")
    val ingredientes: String?,
    @SerializedName("modo_preparo")
    val modoPreparo: String?,
    @SerializedName("link_imagem")
    val linkImagem: String?,
    @SerializedName("tipo")
    val tipo: String?,
    @SerializedName("created_at")
    val createdAt: String?
) {
    fun toReceita(): Receita {
        return Receita(
            id = id?.toString() ?: "",
            nome = nome ?: "",
            categoria = tipo ?: "",
            regiao = "Brasil",
            instrucoes = modoPreparo ?: "",
            imagemUrl = linkImagem ?: "",
            linkYoutube = null,
            ingredientes = ingredientes ?: ""
        )
    }
}

data class Receita(
    val id: String,
    val nome: String,
    val categoria: String,
    val regiao: String,
    val instrucoes: String,
    val imagemUrl: String,
    val linkYoutube: String?,
    val ingredientes: String
)
