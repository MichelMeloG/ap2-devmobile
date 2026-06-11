package com.example.appmobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.adapters.PecaAdapter
import com.example.appmobile.api.ProjetoRequest
import com.example.appmobile.api.RetrofitClient
import com.example.appmobile.models.Peca
import com.example.appmobile.models.Projeto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalhesProjetoActivity : AppCompatActivity() {

    private lateinit var textViewNomeProjeto: TextView
    private lateinit var progressBarOrcamento: ProgressBar
    private lateinit var textViewProgresso: TextView
    private lateinit var recyclerViewPecasDetalhes: RecyclerView
    private lateinit var buttonSalvar: Button
    
    private val apiService = RetrofitClient.apiService

    private var carroId: Int = 0
    private var orcamento: Double = 0.0
    private var nomeProjeto: String = ""
    private var pecasSelecionadas: List<Peca> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_projeto)

        textViewNomeProjeto = findViewById(R.id.textViewNomeProjeto)
        progressBarOrcamento = findViewById(R.id.progressBarOrcamento)
        textViewProgresso = findViewById(R.id.textViewProgresso)
        recyclerViewPecasDetalhes = findViewById(R.id.recyclerViewPecasDetalhes)
        buttonSalvar = findViewById(R.id.buttonSalvar)

        carroId = intent.getIntExtra("carro_id", 0)
        orcamento = intent.getDoubleExtra("orcamento", 0.0)
        nomeProjeto = intent.getStringExtra("nome_projeto") ?: ""

        textViewNomeProjeto.text = "Projeto: $nomeProjeto"

        recyclerViewPecasDetalhes.layoutManager = LinearLayoutManager(this)

        carregarPecasComissionadas()

        buttonSalvar.setOnClickListener { salvarProjeto() }
    }

    private fun carregarPecasComissionadas() {
        apiService.listarPecasPorCarro(carroId).enqueue(object : Callback<List<Peca>> {
            override fun onResponse(call: Call<List<Peca>>, response: Response<List<Peca>>) {
                if (response.isSuccessful && response.body() != null) {
                    // Filtrar apenas selecionadas (implementar lógica de persistência)
                    pecasSelecionadas = response.body()!!
                    
                    val pecaAdapter = PecaAdapter(pecasSelecionadas, this@DetalhesProjetoActivity) { _, _ -> }
                    recyclerViewPecasDetalhes.adapter = pecaAdapter

                    atualizarProgresso()
                }
            }

            override fun onFailure(call: Call<List<Peca>>, t: Throwable) {
                Toast.makeText(this@DetalhesProjetoActivity, "Erro ao carregar detalhes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun atualizarProgresso() {
        var custoTotal = 0.0
        for (peca in pecasSelecionadas) {
            custoTotal += peca.preco
        }

        val percentual = if (orcamento > 0) ((custoTotal / orcamento) * 100).toInt() else 0
        progressBarOrcamento.progress = percentual

        textViewProgresso.text = "$percentual% de R$ ${String.format("%.2f", orcamento)}"
    }

    private fun salvarProjeto() {
        val pecasIds = pecasSelecionadas.map { it.id }

        val projetoRequest = ProjetoRequest(nomeProjeto, orcamento, carroId, pecasIds)

        apiService.criarProjeto(projetoRequest).enqueue(object : Callback<Projeto> {
            override fun onResponse(call: Call<Projeto>, response: Response<Projeto>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DetalhesProjetoActivity, "Projeto salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@DetalhesProjetoActivity, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@DetalhesProjetoActivity, "Erro ao salvar projeto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Projeto>, t: Throwable) {
                Toast.makeText(this@DetalhesProjetoActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
