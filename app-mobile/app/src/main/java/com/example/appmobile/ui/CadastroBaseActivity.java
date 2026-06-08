package com.example.appmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appmobile.R;
import com.example.appmobile.api.GearheadApiService;
import com.example.appmobile.api.RetrofitClient;
import com.example.appmobile.models.Carro;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroBaseActivity extends AppCompatActivity {
    
    private Spinner spinnerCarros;
    private EditText editTextOrcamento;
    private EditText editTextNomeProjeto;
    private Button buttonProximo;
    private GearheadApiService apiService;
    private List<Carro> carrosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_base);
        
        spinnerCarros = findViewById(R.id.spinnerCarros);
        editTextOrcamento = findViewById(R.id.editTextOrcamento);
        editTextNomeProjeto = findViewById(R.id.editTextNomeProjeto);
        buttonProximo = findViewById(R.id.buttonProximo);
        
        apiService = RetrofitClient.getApiService();
        
        carregarCarros();
        
        buttonProximo.setOnClickListener(v -> validarEAvancar());
    }

    private void carregarCarros() {
        apiService.listarCarros().enqueue(new Callback<List<Carro>>() {
            @Override
            public void onResponse(Call<List<Carro>> call, Response<List<Carro>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    carrosList = response.body();
                    ArrayAdapter<Carro> adapter = new ArrayAdapter<>(
                            CadastroBaseActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            carrosList
                    );
                    spinnerCarros.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Carro>> call, Throwable t) {
                Toast.makeText(CadastroBaseActivity.this, "Erro ao carregar carros", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validarEAvancar() {
        String nomeProjeto = editTextNomeProjeto.getText().toString().trim();
        String orcamentoStr = editTextOrcamento.getText().toString().trim();
        
        if (nomeProjeto.isEmpty()) {
            editTextNomeProjeto.setError("Nome do projeto é obrigatório");
            return;
        }
        
        if (orcamentoStr.isEmpty()) {
            editTextOrcamento.setError("Orçamento é obrigatório");
            return;
        }
        
        try {
            double orcamento = Double.parseDouble(orcamentoStr);
            Carro carroSelecionado = (Carro) spinnerCarros.getSelectedItem();
            
            Intent intent = new Intent(CadastroBaseActivity.this, SetupPecasActivity.class);
            intent.putExtra("carro_id", carroSelecionado.getId());
            intent.putExtra("orcamento", orcamento);
            intent.putExtra("nome_projeto", nomeProjeto);
            startActivity(intent);
        } catch (NumberFormatException e) {
            editTextOrcamento.setError("Valor inválido");
        }
    }
}
