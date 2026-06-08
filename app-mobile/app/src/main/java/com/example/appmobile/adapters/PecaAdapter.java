package com.example.appmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appmobile.R;
import com.example.appmobile.models.Peca;
import java.util.List;

public class PecaAdapter extends RecyclerView.Adapter<PecaAdapter.PecaViewHolder> {
    
    private List<Peca> pecas;
    private Context context;
    private OnPecaSelecionadaListener listener;

    public interface OnPecaSelecionadaListener {
        void onPecaSelecionada(Peca peca, boolean selecionada);
    }

    public PecaAdapter(List<Peca> pecas, Context context, OnPecaSelecionadaListener listener) {
        this.pecas = pecas;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PecaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_peca, parent, false);
        return new PecaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PecaViewHolder holder, int position) {
        Peca peca = pecas.get(position);
        holder.bind(peca, listener);
    }

    @Override
    public int getItemCount() {
        return pecas.size();
    }

    public static class PecaViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBoxPeca;
        private TextView nomePecaTextView;
        private TextView precoPecaTextView;
        private TextView tipoPecaTextView;

        public PecaViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxPeca = itemView.findViewById(R.id.checkBoxPeca);
            nomePecaTextView = itemView.findViewById(R.id.nomePeca);
            precoPecaTextView = itemView.findViewById(R.id.precoPeca);
            tipoPecaTextView = itemView.findViewById(R.id.tipoPeca);
        }

        public void bind(Peca peca, OnPecaSelecionadaListener listener) {
            nomePecaTextView.setText(peca.getNome());
            precoPecaTextView.setText("R$ " + String.format("%.2f", peca.getPreco()));
            tipoPecaTextView.setText(peca.getTipo());
            
            checkBoxPeca.setChecked(peca.isSelecionada());
            
            checkBoxPeca.setOnCheckedChangeListener((buttonView, isChecked) -> {
                peca.setSelecionada(isChecked);
                listener.onPecaSelecionada(peca, isChecked);
            });
        }
    }

    public void updatePecas(List<Peca> novasPecas) {
        this.pecas = novasPecas;
        notifyDataSetChanged();
    }
}
