package com.example.bahreceitas.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bahreceitas.data.local.ReceitaEntity
import com.example.bahreceitas.data.repository.ReceitaRepository
import com.example.bahreceitas.utils.PreferencesManager
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ConfiguracoesViewModel(
    private val repository: ReceitaRepository,
    private val prefsManager: PreferencesManager
) : ViewModel() {

    val favoritas = repository.getFavoritas().asLiveData()

    private val _mensagem = MutableLiveData<String?>()
    val mensagem: LiveData<String?> = _mensagem

    fun getTema(): String {
        return prefsManager.getTema()
    }

    fun setTema(tema: String) {
        prefsManager.setTema(tema)
    }

    fun exportarFavoritos(): String {
        val lista = favoritas.value ?: emptyList()
        return Gson().toJson(lista)
    }

    fun importarFavoritos(json: String) {
        viewModelScope.launch {
            try {
                val lista = Gson().fromJson(json, Array<com.example.bahreceitas.data.model.Receita>::class.java)
                lista.forEach { receita ->
                    repository.adicionarFavorita(receita)
                }
                _mensagem.value = "Favoritos importados com sucesso"
            } catch (e: Exception) {
                _mensagem.value = "Erro ao importar favoritos"
            }
        }
    }
}
