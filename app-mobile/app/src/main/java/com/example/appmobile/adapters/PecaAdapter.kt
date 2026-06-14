package com.example.appmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        holder.bind(peca, listener, context)
    }

    override fun getItemCount(): Int {
        return pecas.size
    }

    class PecaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBoxPeca: CheckBox = itemView.findViewById(R.id.checkBoxPeca)
        private val nomePecaTextView: TextView = itemView.findViewById(R.id.nomePeca)
        private val descricaoPecaTextView: TextView = itemView.findViewById(R.id.descricaoPeca)
        private val precoPecaTextView: TextView = itemView.findViewById(R.id.precoPeca)
        private val tipoPecaTextView: TextView = itemView.findViewById(R.id.tipoPeca)
        private val badgeHp: TextView = itemView.findViewById(R.id.badgeHp)

        fun bind(peca: Peca, listener: (Peca, Boolean) -> Unit, context: Context) {
            nomePecaTextView.text = peca.nome
            precoPecaTextView.text = "R$ ${String.format("%.2f", peca.preco)}"
            tipoPecaTextView.text = peca.tipo

            // Descrição
            if (peca.descricao.isNotBlank()) {
                descricaoPecaTextView.text = peca.descricao
                descricaoPecaTextView.visibility = View.VISIBLE
            } else {
                descricaoPecaTextView.visibility = View.GONE
            }

            // Badge HP
            if (peca.ganho_hp > 0) {
                badgeHp.text = "+${peca.ganho_hp} hp"
                badgeHp.visibility = View.VISIBLE
            } else {
                badgeHp.visibility = View.GONE
            }

            // Cor do tipo (Mecânica = azul, Estética = roxo)
            if (peca.tipo == "Mecânica") {
                tipoPecaTextView.setTextColor(ContextCompat.getColor(context, R.color.tag_mecanica_text))
                tipoPecaTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.tag_mecanica))
            } else {
                tipoPecaTextView.setTextColor(ContextCompat.getColor(context, R.color.tag_estetica_text))
                tipoPecaTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.tag_estetica))
            }
            
            checkBoxPeca.setOnCheckedChangeListener(null)
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
