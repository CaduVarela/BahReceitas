package com.example.bahreceitas.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import coil.load
import com.example.bahreceitas.R
import com.example.bahreceitas.data.local.AppDatabase
import com.example.bahreceitas.data.model.Receita
import com.example.bahreceitas.data.repository.ReceitaRepository
import com.example.bahreceitas.databinding.FragmentDetalhesReceitaBinding
import com.example.bahreceitas.network.RetrofitInstance
import com.google.gson.Gson

class DetalhesReceitaFragment : Fragment() {

    private var _binding: FragmentDetalhesReceitaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DetalhesReceitaViewModel

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
        val repository = ReceitaRepository(database.receitaDao(), RetrofitInstance.api)
        viewModel = DetalhesReceitaViewModel(repository)

        val receitaJson = arguments?.getString("receita")
        receitaJson?.let {
            val receita = Gson().fromJson(it, Receita::class.java)
            viewModel.setReceita(receita)
        }

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.receita.observe(viewLifecycleOwner) { receita ->
            binding.nomeReceita.text = receita.nome
            binding.chipCategoria.text = receita.categoria
            binding.chipRegiao.text = receita.regiao
            binding.ingredientesReceita.text = receita.ingredientes
            binding.instrucoesReceita.text = viewModel.formatarInstrucoes(receita.instrucoes)
            binding.imagemReceita.load(receita.imagemUrl) {
                crossfade(true)
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

        viewModel.mensagem.observe(viewLifecycleOwner) { mensagem ->
            mensagem?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.fabVoltar.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.fabFavorito.setOnClickListener {
            viewModel.toggleFavorito()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
