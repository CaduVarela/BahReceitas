package com.example.bahreceitas.ui.buscar

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
import com.example.bahreceitas.databinding.FragmentBuscarBinding
import com.example.bahreceitas.network.RetrofitInstance
import com.example.bahreceitas.ui.ReceitaAdapter
import com.google.gson.Gson

class BuscarFragment : Fragment() {

    private var _binding: FragmentBuscarBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BuscarViewModel
    private lateinit var adapter: ReceitaAdapter

    companion object {
        private var sharedViewModel: BuscarViewModel? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuscarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sharedViewModel == null) {
            val database = AppDatabase.getDatabase(requireContext())
            val repository = ReceitaRepository(database.receitaDao(), RetrofitInstance.api)
            sharedViewModel = BuscarViewModel(repository)
        }
        viewModel = sharedViewModel!!

        setupRecyclerView()
        setupSearchView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = ReceitaAdapter { receita ->
            val receitaJson = Gson().toJson(receita)
            val bundle = bundleOf("receita" to receitaJson)
            findNavController().navigate(R.id.action_buscar_to_detalhes, bundle)
        }
        binding.recyclerReceitas.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.buscarReceitas(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun setupObservers() {
        viewModel.receitas.observe(viewLifecycleOwner) { receitas ->
            adapter.submitList(receitas)
            if (receitas.isNotEmpty()) {
                binding.recyclerReceitas.visibility = View.VISIBLE
                binding.mensagemVazia.visibility = View.GONE
            } else {
                binding.recyclerReceitas.visibility = View.GONE
            }
        }

        viewModel.carregando.observe(viewLifecycleOwner) { carregando ->
            binding.progressBar.visibility = if (carregando) View.VISIBLE else View.GONE
        }

        viewModel.mensagem.observe(viewLifecycleOwner) { mensagem ->
            mensagem?.let {
                binding.mensagemVazia.text = it
                binding.mensagemVazia.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
