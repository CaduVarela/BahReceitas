package com.example.bahreceitas.ui.favoritos

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bahreceitas.data.model.Receita
import com.example.bahreceitas.data.repository.ReceitaRepository
import kotlinx.coroutines.launch

class FavoritosViewModel(private val repository: ReceitaRepository) : ViewModel() {

    val favoritas: LiveData<List<Receita>> = repository.getFavoritas().asLiveData()

    fun removerFavorita(receita: Receita) {
        viewModelScope.launch {
            repository.removerFavorita(receita)
        }
    }
}
