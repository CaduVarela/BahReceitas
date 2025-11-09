package com.example.bahreceitas.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("bah_receitas_prefs", Context.MODE_PRIVATE)

    fun setTema(tema: String) {
        prefs.edit().putString(KEY_TEMA, tema).apply()
    }

    fun getTema(): String {
        return prefs.getString(KEY_TEMA, TEMA_SISTEMA) ?: TEMA_SISTEMA
    }

    fun setReceitaDoDiaHabilitada(habilitada: Boolean) {
        prefs.edit().putBoolean(KEY_RECEITA_DIA, habilitada).apply()
    }

    fun isReceitaDoDiaHabilitada(): Boolean {
        return prefs.getBoolean(KEY_RECEITA_DIA, true)
    }

    fun setUltimaBusca(termo: String) {
        prefs.edit().putString(KEY_ULTIMA_BUSCA, termo).apply()
    }

    fun getUltimaBusca(): String {
        return prefs.getString(KEY_ULTIMA_BUSCA, "") ?: ""
    }

    companion object {
        private const val KEY_TEMA = "tema"
        private const val KEY_RECEITA_DIA = "receita_do_dia"
        private const val KEY_ULTIMA_BUSCA = "ultima_busca"

        const val TEMA_CLARO = "claro"
        const val TEMA_ESCURO = "escuro"
        const val TEMA_SISTEMA = "sistema"
    }
}
