package com.example.bahreceitas.ui.discover

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
import com.example.bahreceitas.data.repository.ReceitaRepository
import com.example.bahreceitas.databinding.FragmentDescobrirBinding
import com.example.bahreceitas.network.RetrofitInstance
import kotlinx.coroutines.launch

class DescobrirFragment : Fragment() {

    private var _binding: FragmentDescobrirBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DescobrirViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDescobrirBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = ReceitaRepository(database.receitaDao(), RetrofitInstance.api)
        viewModel = DescobrirViewModel(repository)

        setupObservers()
        setupListeners()

        viewModel.carregarReceitaAleatoria()
    }

    private fun setupObservers() {
        viewModel.receita.observe(viewLifecycleOwner) { receita ->
            receita?.let {
                binding.cardReceita.visibility = View.VISIBLE
                binding.mensagemErro.visibility = View.GONE

                binding.nomeReceita.text = it.nome
                binding.categoriaReceita.text = it.categoria
                binding.regiaoReceita.text = it.regiao
                binding.ingredientesReceita.text = it.ingredientes
                binding.instrucoesReceita.text = it.instrucoes
                binding.imagemReceita.load(it.imagemUrl) {
                    crossfade(true)
                }
            }
        }

        viewModel.carregando.observe(viewLifecycleOwner) { carregando ->
            binding.progressBar.visibility = if (carregando) View.VISIBLE else View.GONE
        }

        viewModel.erro.observe(viewLifecycleOwner) { erro ->
            erro?.let {
                binding.mensagemErro.visibility = View.VISIBLE
                binding.cardReceita.visibility = View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.isFavorita.collect { favorita ->
                val icone = if (favorita) {
                    android.R.drawable.star_big_on
                } else {
                    android.R.drawable.star_big_off
                }
                binding.fabFavorito.setImageResource(icone)
            }
        }
    }

    private fun setupListeners() {
        binding.btnNovaReceita.setOnClickListener {
            viewModel.carregarReceitaAleatoria()
        }

        binding.fabFavorito.setOnClickListener {
            viewModel.alternarFavorito()
            val mensagem = if (viewModel.isFavorita.value) {
                getString(R.string.removido_favoritos)
            } else {
                getString(R.string.adicionado_favoritos)
            }
            Toast.makeText(requireContext(), mensagem, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
