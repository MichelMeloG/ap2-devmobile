package com.example.appmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.appmobile.R;
import com.example.appmobile.adapters.ProjetoAdapter;
import com.example.appmobile.api.GearheadApiService;
import com.example.appmobile.api.RetrofitClient;
import com.example.appmobile.models.Projeto;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {
    
    private RecyclerView recyclerViewProjetos;
    private ProjetoAdapter projetoAdapter;
    private FloatingActionButton fabNovoProjeto;
    private GearheadApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        recyclerViewProjetos = findViewById(R.id.recyclerViewProjetos);
        fabNovoProjeto = findViewById(R.id.fabNovoProjeto);
        
        apiService = RetrofitClient.getApiService();
        
        // Setup RecyclerView
        recyclerViewProjetos.setLayoutManager(new LinearLayoutManager(this));
        projetoAdapter = new ProjetoAdapter(new ArrayList<>(), this, new ProjetoAdapter.OnProjetoClickListener() {
            @Override
            public void onProjetoClick(Projeto projeto) {
                // Abre detalhes do projeto
                Intent intent = new Intent(DashboardActivity.this, DetalhesProjeto Activity.class);
                intent.putExtra("projeto_id", projeto.getId());
                startActivity(intent);
            }

            @Override
            public void onDeletarClick(Projeto projeto) {
                deletarProjeto(projeto.getId());
            }
        });
        recyclerViewProjetos.setAdapter(projetoAdapter);
        
        // Novo Projeto
        fabNovoProjeto.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CadastroBaseActivity.class);
            startActivity(intent);
        });
        
        carregarProjetos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarProjetos();
    }

    private void carregarProjetos() {
        apiService.listarProjetos().enqueue(new Callback<List<Projeto>>() {
            @Override
            public void onResponse(Call<List<Projeto>> call, Response<List<Projeto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    projetoAdapter.updateProjetos(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Projeto>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Erro ao carregar projetos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletarProjeto(int projetoId) {
        apiService.deletarProjeto(projetoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(DashboardActivity.this, "Projeto deletado", Toast.LENGTH_SHORT).show();
                carregarProjetos();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Erro ao deletar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
