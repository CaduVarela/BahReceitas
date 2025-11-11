package com.example.bahreceitas.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("bah_receitas_prefs", Context.MODE_PRIVATE)

    fun setTema(tema: String) {
        prefs.edit().putString(KEY_TEMA, tema).apply()
    }

    fun getTema(): String {
        return prefs.getString(KEY_TEMA, TEMA_SISTEMA) ?: TEMA_SISTEMA
    }

    fun aplicarTema() {
        val tema = getTema()
        val modo = when (tema) {
            TEMA_CLARO -> AppCompatDelegate.MODE_NIGHT_NO
            TEMA_ESCURO -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(modo)
    }

    fun setUltimaBusca(termo: String) {
        prefs.edit().putString(KEY_ULTIMA_BUSCA, termo).apply()
    }

    fun getUltimaBusca(): String {
        return prefs.getString(KEY_ULTIMA_BUSCA, "") ?: ""
    }

    companion object {
        private const val KEY_TEMA = "tema"
        private const val KEY_ULTIMA_BUSCA = "ultima_busca"

        const val TEMA_CLARO = "claro"
        const val TEMA_ESCURO = "escuro"
        const val TEMA_SISTEMA = "sistema"
    }
}
