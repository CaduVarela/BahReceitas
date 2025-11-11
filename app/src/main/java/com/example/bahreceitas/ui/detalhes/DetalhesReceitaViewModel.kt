package com.example.bahreceitas.ui.detalhes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bahreceitas.data.model.Receita
import com.example.bahreceitas.data.repository.ReceitaRepository
import kotlinx.coroutines.launch

class DetalhesReceitaViewModel(
    private val repository: ReceitaRepository
) : ViewModel() {

    private val _receita = MutableLiveData<Receita>()
    val receita: LiveData<Receita> = _receita

    private val _isFavorita = MutableLiveData<Boolean>()
    val isFavorita: LiveData<Boolean> = _isFavorita

    private val _mensagem = MutableLiveData<String?>()
    val mensagem: LiveData<String?> = _mensagem

    fun setReceita(receita: Receita) {
        _receita.value = receita
        observarFavorito(receita.id)
    }

    private fun observarFavorito(receitaId: String) {
        viewModelScope.launch {
            repository.isFavorita(receitaId).collect { favorita ->
                _isFavorita.value = favorita
            }
        }
    }

    fun toggleFavorito() {
        _receita.value?.let { receita ->
            viewModelScope.launch {
                if (_isFavorita.value == true) {
                    repository.removerFavorita(receita)
                    _mensagem.value = "Removido dos favoritos"
                } else {
                    repository.adicionarFavorita(receita)
                    _mensagem.value = "Adicionado aos favoritos"
                }
                _mensagem.value = null
            }
        }
    }

    fun formatarInstrucoes(texto: String): String {
        return texto.replace(". ", ".\n\n")
            .replace("\r\n", "\n")
            .trim()
    }
}
