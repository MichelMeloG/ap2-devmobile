package com.example.appmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import com.example.appmobile.R;
import com.example.appmobile.models.Projeto;
import java.util.List;

public class ProjetoAdapter extends RecyclerView.Adapter<ProjetoAdapter.ProjetoViewHolder> {
    
    private List<Projeto> projetos;
    private Context context;
    private OnProjetoClickListener listener;

    public interface OnProjetoClickListener {
        void onProjetoClick(Projeto projeto);
        void onDeletarClick(Projeto projeto);
    }

    public ProjetoAdapter(List<Projeto> projetos, Context context, OnProjetoClickListener listener) {
        this.projetos = projetos;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProjetoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_projeto, parent, false);
        return new ProjetoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjetoViewHolder holder, int position) {
        Projeto projeto = projetos.get(position);
        holder.bind(projeto, listener);
    }

    @Override
    public int getItemCount() {
        return projetos.size();
    }

    public static class ProjetoViewHolder extends RecyclerView.ViewHolder {
        private TextView nomeTextView;
        private TextView carroTextView;
        private TextView orcamentoTextView;
        private TextView custoTextView;
        private CardView cardView;

        public ProjetoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.nomeProjeto);
            carroTextView = itemView.findViewById(R.id.carroProjeto);
            orcamentoTextView = itemView.findViewById(R.id.orcamentoProjeto);
            custoTextView = itemView.findViewById(R.id.custoProjeto);
            cardView = itemView.findViewById(R.id.cardProjeto);
        }

        public void bind(Projeto projeto, OnProjetoClickListener listener) {
            nomeTextView.setText(projeto.getNome());
            orcamentoTextView.setText("Orç: R$ " + String.format("%.2f", projeto.getOrcamentoMaximo()));
            custoTextView.setText("Custo: R$ " + String.format("%.2f", projeto.getCustoTotalCalculado()));
            
            cardView.setOnClickListener(v -> listener.onProjetoClick(projeto));
        }
    }

    public void updateProjetos(List<Projeto> novosProjetos) {
        this.projetos = novosProjetos;
        notifyDataSetChanged();
    }
}
