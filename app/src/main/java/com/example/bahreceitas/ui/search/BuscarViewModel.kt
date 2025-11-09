package com.example.bahreceitas.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bahreceitas.data.model.Receita
import com.example.bahreceitas.data.repository.ReceitaRepository
import kotlinx.coroutines.launch

class BuscarViewModel(private val repository: ReceitaRepository) : ViewModel() {

    private val _receitas = MutableLiveData<List<Receita>>()
    val receitas: LiveData<List<Receita>> = _receitas

    private val _carregando = MutableLiveData<Boolean>()
    val carregando: LiveData<Boolean> = _carregando

    private val _mensagem = MutableLiveData<String?>()
    val mensagem: LiveData<String?> = _mensagem

    fun buscarReceitas(termo: String) {
        if (termo.isBlank()) {
            _receitas.value = emptyList()
            _mensagem.value = null
            return
        }

        viewModelScope.launch {
            _carregando.value = true
            _mensagem.value = null

            repository.buscarReceitas(termo)
                .onSuccess { lista ->
                    _receitas.value = lista
                    if (lista.isEmpty()) {
                        _mensagem.value = "Nenhuma receita encontrada"
                    }
                }
                .onFailure { exception ->
                    _mensagem.value = exception.message
                    _receitas.value = emptyList()
                }

            _carregando.value = false
        }
    }
}
