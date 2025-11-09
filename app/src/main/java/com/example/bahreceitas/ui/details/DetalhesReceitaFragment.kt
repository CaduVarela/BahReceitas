package com.example.bahreceitas.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.bahreceitas.R
import com.example.bahreceitas.data.local.AppDatabase
import com.example.bahreceitas.data.model.Receita
import com.example.bahreceitas.data.repository.ReceitaRepository
import com.example.bahreceitas.databinding.FragmentDetalhesReceitaBinding
import com.example.bahreceitas.network.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.launch

class DetalhesReceitaFragment : Fragment() {

    private var _binding: FragmentDetalhesReceitaBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: ReceitaRepository
    private var receitaAtual: Receita? = null
    private var isFavorita = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalhesReceitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        repository = ReceitaRepository(database.receitaDao(), RetrofitInstance.api)

        val receitaJson = arguments?.getString("receita")
        receitaJson?.let {
            receitaAtual = Gson().fromJson(it, Receita::class.java)
            exibirReceita(receitaAtual!!)
        }

        setupListeners()
    }

    private fun exibirReceita(receita: Receita) {
        binding.nomeReceita.text = receita.nome
        binding.chipCategoria.text = receita.categoria
        binding.chipRegiao.text = receita.regiao
        binding.ingredientesReceita.text = receita.ingredientes
        binding.instrucoesReceita.text = formatarInstrucoes(receita.instrucoes)
        binding.imagemReceita.load(receita.imagemUrl) {
            crossfade(true)
        }

        lifecycleScope.launch {
            repository.isFavorita(receita.id).collect { favorita ->
                isFavorita = favorita
                val icone = if (favorita) {
                    android.R.drawable.star_big_on
                } else {
                    android.R.drawable.star_big_off
                }
                binding.fabFavorito.setImageResource(icone)
            }
        }
    }

    private fun formatarInstrucoes(texto: String): String {
        return texto.replace(". ", ".\n\n")
            .replace("\r\n", "\n")
            .trim()
    }

    private fun setupListeners() {
        binding.fabFavorito.setOnClickListener {
            receitaAtual?.let { receita ->
                lifecycleScope.launch {
                    if (isFavorita) {
                        repository.removerFavorita(receita)
                        Toast.makeText(requireContext(), R.string.removido_favoritos, Toast.LENGTH_SHORT).show()
                    } else {
                        repository.adicionarFavorita(receita)
                        Toast.makeText(requireContext(), R.string.adicionado_favoritos, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
