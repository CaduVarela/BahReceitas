package com.example.bahreceitas.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import coil.load
import com.example.bahreceitas.R
import com.example.bahreceitas.data.local.AppDatabase
import com.example.bahreceitas.data.repository.ReceitaRepository
import com.example.bahreceitas.databinding.FragmentDescobrirBinding
import com.example.bahreceitas.network.RetrofitInstance

class DescobrirFragment : Fragment() {

    private var _binding: FragmentDescobrirBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DescobrirViewModel

    companion object {
        private var sharedViewModel: DescobrirViewModel? = null
    }

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

        if (sharedViewModel == null) {
            val database = AppDatabase.getDatabase(requireContext())
            val repository = ReceitaRepository(database.receitaDao(), RetrofitInstance.api)
            sharedViewModel = DescobrirViewModel(repository)
        }
        viewModel = sharedViewModel!!

        setupObservers()
        setupListeners()

        if (viewModel.receita.value == null) {
            viewModel.carregarReceitaAleatoria()
        }
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

        viewModel.isFavorita.observe(viewLifecycleOwner) { favorita ->
            val icone = if (favorita) {
                R.drawable.ic_star_filled
            } else {
                R.drawable.ic_star_outline
            }
            binding.fabFavorito.setImageResource(icone)
        }
    }

    private fun setupListeners() {
        binding.btnNovaReceita.setOnClickListener {
            viewModel.carregarReceitaAleatoria()
        }

        binding.fabFavorito.setOnClickListener {
            val isFav = viewModel.isFavorita.value == true
            viewModel.alternarFavorito()
            val mensagem = if (isFav) {
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
