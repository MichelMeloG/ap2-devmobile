package com.example.appmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.models.Peca

class PecaAdapter(
    private var pecas: List<Peca>,
    private val context: Context,
    private val listener: (Peca, Boolean) -> Unit
) : RecyclerView.Adapter<PecaAdapter.PecaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PecaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_peca, parent, false)
        return PecaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PecaViewHolder, position: Int) {
        val peca = pecas[position]
        holder.bind(peca, listener)
    }

    override fun getItemCount(): Int {
        return pecas.size
    }

    class PecaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBoxPeca: CheckBox = itemView.findViewById(R.id.checkBoxPeca)
        private val nomePecaTextView: TextView = itemView.findViewById(R.id.nomePeca)
        private val precoPecaTextView: TextView = itemView.findViewById(R.id.precoPeca)
        private val tipoPecaTextView: TextView = itemView.findViewById(R.id.tipoPeca)

        fun bind(peca: Peca, listener: (Peca, Boolean) -> Unit) {
            nomePecaTextView.text = peca.nome
            precoPecaTextView.text = "R$ ${String.format("%.2f", peca.preco)}"
            tipoPecaTextView.text = peca.tipo
            
            checkBoxPeca.setOnCheckedChangeListener(null) // Remove previous listener to avoid triggering it when setting state
            checkBoxPeca.isChecked = peca.selecionada
            
            checkBoxPeca.setOnCheckedChangeListener { _, isChecked ->
                peca.selecionada = isChecked
                listener(peca, isChecked)
            }
        }
    }

    fun updatePecas(novasPecas: List<Peca>) {
        this.pecas = novasPecas
        notifyDataSetChanged()
    }
}
