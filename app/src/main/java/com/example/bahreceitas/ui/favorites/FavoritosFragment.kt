package com.example.bahreceitas.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bahreceitas.R
import com.example.bahreceitas.data.local.AppDatabase
import com.example.bahreceitas.data.repository.ReceitaRepository
import com.example.bahreceitas.databinding.FragmentFavoritosBinding
import com.example.bahreceitas.network.RetrofitInstance
import com.example.bahreceitas.ui.ReceitaAdapter
import com.google.gson.Gson

class FavoritosFragment : Fragment() {

    private var _binding: FragmentFavoritosBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoritosViewModel
    private lateinit var adapter: ReceitaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = ReceitaRepository(database.receitaDao(), RetrofitInstance.api)
        viewModel = FavoritosViewModel(repository)

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = ReceitaAdapter { receita ->
            val receitaJson = Gson().toJson(receita)
            val bundle = bundleOf("receita" to receitaJson)
            findNavController().navigate(R.id.action_favoritos_to_detalhes, bundle)
        }
        binding.recyclerFavoritos.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.favoritas.observe(viewLifecycleOwner) { favoritas ->
            adapter.submitList(favoritas)
            if (favoritas.isEmpty()) {
                binding.mensagemVazia.visibility = View.VISIBLE
                binding.recyclerFavoritos.visibility = View.GONE
            } else {
                binding.mensagemVazia.visibility = View.GONE
                binding.recyclerFavoritos.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
