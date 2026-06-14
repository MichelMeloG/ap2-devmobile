package com.example.appmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.models.Projeto

class ProjetoAdapter(
    private var projetos: List<Projeto>,
    private val context: Context,
    private val listener: OnProjetoClickListener
) : RecyclerView.Adapter<ProjetoAdapter.ProjetoViewHolder>() {

    interface OnProjetoClickListener {
        fun onProjetoClick(projeto: Projeto)
        fun onEditarClick(projeto: Projeto)
        fun onDeletarClick(projeto: Projeto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjetoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_projeto, parent, false)
        return ProjetoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjetoViewHolder, position: Int) {
        val projeto = projetos[position]
        holder.bind(projeto, listener)
    }

    override fun getItemCount(): Int {
        return projetos.size
    }

    class ProjetoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nomeTextView: TextView = itemView.findViewById(R.id.nomeProjeto)
        private val carroTextView: TextView = itemView.findViewById(R.id.carroProjeto)
        private val orcamentoTextView: TextView = itemView.findViewById(R.id.orcamentoProjeto)
        private val custoTextView: TextView = itemView.findViewById(R.id.custoProjeto)
        private val cardView: CardView = itemView.findViewById(R.id.cardProjeto)
        private val buttonEditar: ImageView = itemView.findViewById(R.id.buttonEditarProjeto)
        private val buttonDeletar: ImageView = itemView.findViewById(R.id.buttonDeletarProjeto)

        fun bind(projeto: Projeto, listener: OnProjetoClickListener) {
            nomeTextView.text = projeto.nome
            orcamentoTextView.text = "Orç: R$ ${String.format("%.2f", projeto.orcamentoMaximo)}"
            custoTextView.text = "Custo: R$ ${String.format("%.2f", projeto.custoTotalCalculado)}"
            
            cardView.setOnClickListener { listener.onProjetoClick(projeto) }
            buttonEditar.setOnClickListener { listener.onEditarClick(projeto) }
            buttonDeletar.setOnClickListener { listener.onDeletarClick(projeto) }
        }
    }

    fun updateProjetos(novosProjetos: List<Projeto>) {
        this.projetos = novosProjetos
        notifyDataSetChanged()
    }
}
