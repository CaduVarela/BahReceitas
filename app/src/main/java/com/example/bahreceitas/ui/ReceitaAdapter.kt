package com.example.bahreceitas.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bahreceitas.data.model.Receita
import com.example.bahreceitas.databinding.ItemReceitaBinding

class ReceitaAdapter(
    private val onClick: (Receita) -> Unit
) : ListAdapter<Receita, ReceitaAdapter.ReceitaViewHolder>(ReceitaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceitaViewHolder {
        val binding = ItemReceitaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReceitaViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ReceitaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReceitaViewHolder(
        private val binding: ItemReceitaBinding,
        private val onClick: (Receita) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(receita: Receita) {
            binding.nomeReceita.text = receita.nome
            binding.chipCategoria.text = receita.categoria
            binding.regiaoReceita.text = receita.regiao
            binding.imagemReceita.load(receita.imagemUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
            }

            binding.root.setOnClickListener {
                onClick(receita)
            }
        }
    }

    class ReceitaDiffCallback : DiffUtil.ItemCallback<Receita>() {
        override fun areItemsTheSame(oldItem: Receita, newItem: Receita): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Receita, newItem: Receita): Boolean {
            return oldItem == newItem
        }
    }
}
