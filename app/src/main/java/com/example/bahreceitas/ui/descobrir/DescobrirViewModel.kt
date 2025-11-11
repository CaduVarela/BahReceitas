package com.example.bahreceitas.ui.descobrir

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bahreceitas.data.model.Receita
import com.example.bahreceitas.data.repository.ReceitaRepository
import kotlinx.coroutines.launch

class DescobrirViewModel(private val repository: ReceitaRepository) : ViewModel() {

    private val _receita = MutableLiveData<Receita?>()
    val receita: LiveData<Receita?> = _receita

    private val _carregando = MutableLiveData<Boolean>()
    val carregando: LiveData<Boolean> = _carregando

    private val _erro = MutableLiveData<String?>()
    val erro: LiveData<String?> = _erro

    private val _isFavorita = MutableLiveData<Boolean>()
    val isFavorita: LiveData<Boolean> = _isFavorita

    fun carregarReceitaAleatoria() {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null

            repository.getReceitaAleatoria()
                .onSuccess { receita ->
                    _receita.value = receita
                    observarFavorita(receita.id)
                }
                .onFailure { exception ->
                    _erro.value = exception.message
                }

            _carregando.value = false
        }
    }

    private fun observarFavorita(receitaId: String) {
        viewModelScope.launch {
            repository.isFavorita(receitaId).collect { favorita ->
                _isFavorita.value = favorita
            }
        }
    }

    fun alternarFavorito() {
        val receitaAtual = _receita.value ?: return

        viewModelScope.launch {
            if (_isFavorita.value == true) {
                repository.removerFavorita(receitaAtual)
            } else {
                repository.adicionarFavorita(receitaAtual)
            }
        }
    }
}
