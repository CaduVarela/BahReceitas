package com.example.bahreceitas.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.bahreceitas.BuildConfig
import com.example.bahreceitas.R
import com.example.bahreceitas.data.local.AppDatabase
import com.example.bahreceitas.data.repository.ReceitaRepository
import com.example.bahreceitas.databinding.FragmentConfiguracoesBinding
import com.example.bahreceitas.network.RetrofitInstance
import com.example.bahreceitas.utils.PreferencesManager

class ConfiguracoesFragment : Fragment() {

    private var _binding: FragmentConfiguracoesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ConfiguracoesViewModel
    private lateinit var prefsManager: PreferencesManager

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            try {
                val json = viewModel.exportarFavoritos()
                requireContext().contentResolver.openOutputStream(it)?.use { output ->
                    output.write(json.toByteArray())
                }
                Toast.makeText(requireContext(), R.string.favoritos_exportados, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), R.string.erro_exportar, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                val json = requireContext().contentResolver.openInputStream(it)?.bufferedReader()
                    ?.use { reader -> reader.readText() }
                json?.let { content ->
                    viewModel.importarFavoritos(content)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), R.string.erro_importar, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfiguracoesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = ReceitaRepository(database.receitaDao(), RetrofitInstance.api)
        prefsManager = PreferencesManager(requireContext())
        viewModel = ConfiguracoesViewModel(repository, prefsManager)

        setupViews()
        setupListeners()
        setupObservers()
    }

    private fun setupViews() {
        binding.textVersao.text = "Versão ${BuildConfig.VERSION_NAME}"

        when (viewModel.getTema()) {
            PreferencesManager.TEMA_CLARO -> binding.radioTemaClaro.isChecked = true
            PreferencesManager.TEMA_ESCURO -> binding.radioTemaEscuro.isChecked = true
            PreferencesManager.TEMA_SISTEMA -> binding.radioTemaSistema.isChecked = true
        }

        binding.switchReceitaDia.isChecked = viewModel.isReceitaDoDiaHabilitada()
    }

    private fun setupListeners() {
        binding.radioGroupTema.setOnCheckedChangeListener { _, checkedId ->
            val tema = when (checkedId) {
                R.id.radioTemaClaro -> PreferencesManager.TEMA_CLARO
                R.id.radioTemaEscuro -> PreferencesManager.TEMA_ESCURO
                else -> PreferencesManager.TEMA_SISTEMA
            }
            viewModel.setTema(tema)
            aplicarTema(tema)
        }

        binding.switchReceitaDia.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setReceitaDoDiaHabilitada(isChecked)
        }

        binding.btnExportar.setOnClickListener {
            exportLauncher.launch("favoritos_bahreceitas.json")
        }

        binding.btnImportar.setOnClickListener {
            importLauncher.launch(arrayOf("application/json"))
        }
    }

    private fun setupObservers() {
        viewModel.mensagem.observe(viewLifecycleOwner) { mensagem ->
            mensagem?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun aplicarTema(tema: String) {
        val modo = when (tema) {
            PreferencesManager.TEMA_CLARO -> AppCompatDelegate.MODE_NIGHT_NO
            PreferencesManager.TEMA_ESCURO -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(modo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
