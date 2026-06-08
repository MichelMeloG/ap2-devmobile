package com.example.appmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appmobile.R;
import com.example.appmobile.adapters.PecaAdapter;
import com.example.appmobile.api.GearheadApiService;
import com.example.appmobile.api.RetrofitClient;
import com.example.appmobile.models.Peca;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetupPecasActivity extends AppCompatActivity {
    
    private RecyclerView recyclerViewPecas;
    private Switch switchModoAgressivo;
    private Button buttonVoltar;
    private Button buttonConcluir;
    private GearheadApiService apiService;
    private int carroId;
    private double orcamento;
    private String nomeProjeto;
    private List<Peca> pecasSelecionadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_pecas);
        
        recyclerViewPecas = findViewById(R.id.recyclerViewPecas);
        switchModoAgressivo = findViewById(R.id.switchModoAgressivo);
        buttonVoltar = findViewById(R.id.buttonVoltar);
        buttonConcluir = findViewById(R.id.buttonConcluir);
        
        apiService = RetrofitClient.getApiService();
        
        // Recuperar dados da tela anterior
        Intent intent = getIntent();
        carroId = intent.getIntExtra("carro_id", 0);
        orcamento = intent.getDoubleExtra("orcamento", 0);
        nomeProjeto = intent.getStringExtra("nome_projeto");
        
        recyclerViewPecas.setLayoutManager(new LinearLayoutManager(this));
        
        carregarPecas();
        
        buttonVoltar.setOnClickListener(v -> finish());
        
        buttonConcluir.setOnClickListener(v -> {
            pecasSelecionadas.clear();
            // Será implementado na próxima activity
            Intent setupIntent = new Intent(SetupPecasActivity.this, DetalhesProjetoActivity.class);
            setupIntent.putExtra("carro_id", carroId);
            setupIntent.putExtra("orcamento", orcamento);
            setupIntent.putExtra("nome_projeto", nomeProjeto);
            startActivity(setupIntent);
        });
    }

    private void carregarPecas() {
        apiService.listarPecasPorCarro(carroId).enqueue(new Callback<List<Peca>>() {
            @Override
            public void onResponse(Call<List<Peca>> call, Response<List<Peca>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Peca> pecas = response.body();
                    PecaAdapter pecaAdapter = new PecaAdapter(pecas, SetupPecasActivity.this, 
                            (peca, selecionada) -> {
                                if (selecionada) {
                                    if (!pecasSelecionadas.contains(peca)) {
                                        pecasSelecionadas.add(peca);
                                    }
                                } else {
                                    pecasSelecionadas.remove(peca);
                                }
                            });
                    recyclerViewPecas.setAdapter(pecaAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Peca>> call, Throwable t) {
                Toast.makeText(SetupPecasActivity.this, "Erro ao carregar peças", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
