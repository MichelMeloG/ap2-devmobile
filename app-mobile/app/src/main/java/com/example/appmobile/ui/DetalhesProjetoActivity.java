package com.example.appmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appmobile.R;
import com.example.appmobile.adapters.PecaAdapter;
import com.example.appmobile.api.GearheadApiService;
import com.example.appmobile.api.ProjetoRequest;
import com.example.appmobile.api.RetrofitClient;
import com.example.appmobile.models.Peca;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalhesProjetoActivity extends AppCompatActivity {
    
    private TextView textViewNomeProjeto;
    private ProgressBar progressBarOrcamento;
    private TextView textViewProgresso;
    private RecyclerView recyclerViewPecasDetalhes;
    private Button buttonSalvar;
    private GearheadApiService apiService;
    
    private int carroId;
    private double orcamento;
    private String nomeProjeto;
    private List<Peca> pecasSelecionadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_projeto);
        
        textViewNomeProjeto = findViewById(R.id.textViewNomeProjeto);
        progressBarOrcamento = findViewById(R.id.progressBarOrcamento);
        textViewProgresso = findViewById(R.id.textViewProgresso);
        recyclerViewPecasDetalhes = findViewById(R.id.recyclerViewPecasDetalhes);
        buttonSalvar = findViewById(R.id.buttonSalvar);
        
        apiService = RetrofitClient.getApiService();
        
        Intent intent = getIntent();
        carroId = intent.getIntExtra("carro_id", 0);
        orcamento = intent.getDoubleExtra("orcamento", 0);
        nomeProjeto = intent.getStringExtra("nome_projeto");
        
        textViewNomeProjeto.setText("Projeto: " + nomeProjeto);
        
        recyclerViewPecasDetalhes.setLayoutManager(new LinearLayoutManager(this));
        
        carregarPecasComsiadas();
        
        buttonSalvar.setOnClickListener(v -> salvarProjeto());
    }

    private void carregarPecasComsiadas() {
        apiService.listarPecasPorCarro(carroId).enqueue(new Callback<List<Peca>>() {
            @Override
            public void onResponse(Call<List<Peca>> call, Response<List<Peca>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Peca> pecas = response.body();
                    // Filtrar apenas selecionadas (implementar lógica de persistência)
                    pecasSelecionadas = pecas;
                    
                    PecaAdapter pecaAdapter = new PecaAdapter(pecasSelecionadas, DetalhesProjetoActivity.this,
                            (peca, selecionada) -> {});
                    recyclerViewPecasDetalhes.setAdapter(pecaAdapter);
                    
                    atualizarProgresso();
                }
            }

            @Override
            public void onFailure(Call<List<Peca>> call, Throwable t) {
                Toast.makeText(DetalhesProjetoActivity.this, "Erro ao carregar detalhes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarProgresso() {
        double custoTotal = 0;
        for (Peca peca : pecasSelecionadas) {
            custoTotal += peca.getPreco();
        }
        
        int percentual = (int) ((custoTotal / orcamento) * 100);
        progressBarOrcamento.setProgress(percentual);
        
        textViewProgresso.setText(percentual + "% de R$ " + String.format("%.2f", orcamento));
    }

    private void salvarProjeto() {
        // Coletar IDs das peças selecionadas
        List<Integer> pecasIds = new ArrayList<>();
        for (Peca peca : pecasSelecionadas) {
            pecasIds.add(peca.getId());
        }
        
        ProjetoRequest projetoRequest = new ProjetoRequest(nomeProjeto, orcamento, carroId, pecasIds);
        
        apiService.criarProjeto(projetoRequest).enqueue(new Callback<com.example.appmobile.models.Projeto>() {
            @Override
            public void onResponse(Call<com.example.appmobile.models.Projeto> call, Response<com.example.appmobile.models.Projeto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetalhesProjetoActivity.this, "Projeto salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetalhesProjetoActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(DetalhesProjetoActivity.this, "Erro ao salvar projeto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.appmobile.models.Projeto> call, Throwable t) {
                Toast.makeText(DetalhesProjetoActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
