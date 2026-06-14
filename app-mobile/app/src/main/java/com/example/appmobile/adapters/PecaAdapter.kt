package com.example.appmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.models.Peca
import com.google.android.material.card.MaterialCardView

class PecaAdapter(
    private var pecas: List<Peca>,
    private val context: Context,
    private val isReadOnly: Boolean = false,
    private val listener: (Peca, Boolean) -> Unit
) : RecyclerView.Adapter<PecaAdapter.PecaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PecaViewHolder {
        val layout = if (isReadOnly) R.layout.item_peca_compact else R.layout.item_peca
        val view = LayoutInflater.from(context).inflate(layout, parent, false)
        return PecaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PecaViewHolder, position: Int) {
        val peca = pecas[position]
        holder.bind(peca, listener, context, isReadOnly)
    }

    override fun getItemCount(): Int {
        return pecas.size
    }

    class PecaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardPeca: MaterialCardView? = itemView.findViewById(R.id.cardPeca)
        private val nomePecaTextView: TextView = itemView.findViewById(R.id.nomePeca)
        private val descricaoPecaTextView: TextView? = itemView.findViewById(R.id.descricaoPeca)
        private val precoPecaTextView: TextView = itemView.findViewById(R.id.precoPeca)
        private val tipoPecaTextView: TextView? = itemView.findViewById(R.id.tipoPeca)
        private val badgeHp: TextView? = itemView.findViewById(R.id.badgeHp)
        private val iconeCheck: ImageView? = itemView.findViewById(R.id.iconeCheck)

        fun bind(peca: Peca, listener: (Peca, Boolean) -> Unit, context: Context, isReadOnly: Boolean) {
            nomePecaTextView.text = peca.nome
            precoPecaTextView.text = "R$ ${String.format("%.2f", peca.preco)}"

            // Badge HP
            if (peca.ganho_hp > 0) {
                badgeHp?.text = "+${peca.ganho_hp} hp"
                badgeHp?.visibility = View.VISIBLE
            } else {
                badgeHp?.visibility = View.GONE
            }

            if (!isReadOnly) {
                // Descrição (só no card completo)
                if (peca.descricao.isNotBlank()) {
                    descricaoPecaTextView?.text = peca.descricao
                    descricaoPecaTextView?.visibility = View.VISIBLE
                } else {
                    descricaoPecaTextView?.visibility = View.GONE
                }

                // Tipo e Cor (Mecânica = azul, Estética = roxo)
                tipoPecaTextView?.text = peca.tipo
                if (peca.tipo == "Mecânica") {
                    tipoPecaTextView?.setTextColor(ContextCompat.getColor(context, R.color.tag_mecanica_text))
                    tipoPecaTextView?.setBackgroundColor(ContextCompat.getColor(context, R.color.tag_mecanica))
                } else {
                    tipoPecaTextView?.setTextColor(ContextCompat.getColor(context, R.color.tag_estetica_text))
                    tipoPecaTextView?.setBackgroundColor(ContextCompat.getColor(context, R.color.tag_estetica))
                }
                
                // Controle de Seleção Visual
                val atualizarVisualSelecao = {
                    if (peca.selecionada) {
                        cardPeca?.strokeColor = ContextCompat.getColor(context, R.color.accent_primary)
                        cardPeca?.strokeWidth = 4 // 2dp
                        iconeCheck?.visibility = View.VISIBLE
                    } else {
                        cardPeca?.strokeColor = ContextCompat.getColor(context, R.color.bg_input)
                        cardPeca?.strokeWidth = 2 // 1dp
                        iconeCheck?.visibility = View.INVISIBLE
                    }
                }

                atualizarVisualSelecao()
                
                cardPeca?.setOnClickListener {
                    peca.selecionada = !peca.selecionada
                    atualizarVisualSelecao()
                    listener(peca, peca.selecionada)
                }
            }
        }
    }

    fun updatePecas(novasPecas: List<Peca>) {
        this.pecas = novasPecas
        notifyDataSetChanged()
    }
}
