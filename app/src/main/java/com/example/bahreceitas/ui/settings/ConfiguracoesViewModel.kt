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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ConfiguracoesViewModel(
    private val repository: ReceitaRepository,
    private val prefsManager: PreferencesManager
) : ViewModel() {

    val favoritas = repository.getFavoritas().asLiveData()

    private val _mensagem = MutableLiveData<String?>()
    val mensagem: LiveData<String?> = _mensagem

    private val _exportJson = MutableLiveData<String>()
    val exportJson: LiveData<String> = _exportJson

    fun getTema(): String {
        return prefsManager.getTema()
    }

    fun setTema(tema: String) {
        prefsManager.setTema(tema)
    }

    fun exportarFavoritos() {
        viewModelScope.launch {
            val lista = repository.getFavoritas().first()
            val json = Gson().toJson(lista)
            _exportJson.value = json
        }
    }

    fun importarFavoritos(json: String) {
        viewModelScope.launch {
            try {
                val lista = Gson().fromJson(json, Array<com.example.bahreceitas.data.model.Receita>::class.java)
                if (lista != null && lista.isNotEmpty()) {
                    lista.forEach { receita ->
                        repository.adicionarFavorita(receita)
                    }
                    _mensagem.value = "Favoritos importados com sucesso"
                } else {
                    _mensagem.value = "Nenhum favorito encontrado"
                }
            } catch (e: Exception) {
                _mensagem.value = "Erro ao importar favoritos"
            }
        }
    }
}
