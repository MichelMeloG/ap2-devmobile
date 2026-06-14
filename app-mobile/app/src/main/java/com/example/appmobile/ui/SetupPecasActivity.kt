package com.example.appmobile.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.adapters.PecaAdapter
import com.example.appmobile.api.RetrofitClient
import com.example.appmobile.models.Peca
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetupPecasActivity : AppCompatActivity() {

    private lateinit var recyclerViewPecas: RecyclerView
    private lateinit var chipGroupFiltros: ChipGroup
    private lateinit var buttonVoltar: MaterialButton
    private lateinit var buttonConcluir: MaterialButton
    private lateinit var layoutLoadingPecas: LinearLayout
    
    // Bottom Panel
    private lateinit var textoCustoTotal: TextView
    private lateinit var textoHpTotal: TextView
    private lateinit var progressBarOrcamentoTotal: ProgressBar
    
    private val apiService = RetrofitClient.apiService
    
    private var carroId: Int = 0
    private var orcamento: Double = 0.0
    private var nomeProjeto: String = ""
    private var marcaCarro: String = ""
    private var modeloCarro: String = ""
    
    private val pecasSelecionadas = mutableSetOf<Peca>()
    private var todasPecas = listOf<Peca>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_pecas)

        recyclerViewPecas = findViewById(R.id.recyclerViewPecas)
        chipGroupFiltros = findViewById(R.id.chipGroupFiltros)
        buttonVoltar = findViewById(R.id.buttonVoltar)
        buttonConcluir = findViewById(R.id.buttonConcluir)
        layoutLoadingPecas = findViewById(R.id.layoutLoadingPecas)
        textoCustoTotal = findViewById(R.id.textoCustoTotal)
        textoHpTotal = findViewById(R.id.textoHpTotal)
        progressBarOrcamentoTotal = findViewById(R.id.progressBarOrcamentoTotal)

        carroId = intent.getIntExtra("carro_id", 0)
        orcamento = intent.getDoubleExtra("orcamento", 0.0)
        nomeProjeto = intent.getStringExtra("nome_projeto") ?: ""
        marcaCarro = intent.getStringExtra("marca_carro") ?: ""
        modeloCarro = intent.getStringExtra("modelo_carro") ?: ""
        
        val pecasIdsAnteriores = intent.getIntegerArrayListExtra("pecas_ids") ?: arrayListOf()

        recyclerViewPecas.layoutManager = LinearLayoutManager(this)

        carregarPecas(pecasIdsAnteriores)

        val projetoId = intent.getIntExtra("projeto_id", 0)
        if (projetoId > 0) {
            buttonConcluir.text = "Salvar Alterações"
        }

        // Lógica dos Chips de Filtro
        chipGroupFiltros.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            
            val checkedId = checkedIds.first()
            val pecasFiltradas = when (checkedId) {
                R.id.chipMecanica -> todasPecas.filter { it.tipo == "Mecânica" }
                R.id.chipEstetica -> todasPecas.filter { it.tipo == "Estética" }
                else -> todasPecas // chipTodas
            }
            atualizarListaPecas(pecasFiltradas)
        }

        buttonVoltar.setOnClickListener { finish() }

        buttonConcluir.setOnClickListener {
            if (pecasSelecionadas.isEmpty()) {
                Toast.makeText(this, "Selecione ao menos uma peça", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pecasIds = pecasSelecionadas.map { it.id }

            if (projetoId > 0) {
                // Atualizar projeto existente
                val request = com.example.appmobile.api.ProjetoRequest(
                    nome = nomeProjeto,
                    orcamento_maximo = orcamento,
                    carro_id = carroId,
                    pecas_ids = pecasIds
                )
                apiService.atualizarProjeto(projetoId, request).enqueue(object : Callback<com.example.appmobile.models.Projeto> {
                    override fun onResponse(call: Call<com.example.appmobile.models.Projeto>, response: Response<com.example.appmobile.models.Projeto>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@SetupPecasActivity, "Peças atualizadas!", Toast.LENGTH_SHORT).show()
                            // Volta para a tela de detalhes que recarregará
                            val setupIntent = Intent(this@SetupPecasActivity, DetalhesProjetoActivity::class.java)
                            setupIntent.putExtra("projeto_id", projetoId)
                            setupIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(setupIntent)
                            finish()
                        } else {
                            Toast.makeText(this@SetupPecasActivity, "Erro ao atualizar. Orçamento insuficiente?", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<com.example.appmobile.models.Projeto>, t: Throwable) {
                        Toast.makeText(this@SetupPecasActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                // Novo projeto
                val setupIntent = Intent(this@SetupPecasActivity, DetalhesProjetoActivity::class.java)
                setupIntent.putExtra("carro_id", carroId)
                setupIntent.putExtra("orcamento", orcamento)
                setupIntent.putExtra("nome_projeto", nomeProjeto)
                setupIntent.putExtra("marca_carro", marcaCarro)
                setupIntent.putExtra("modelo_carro", modeloCarro)
                setupIntent.putIntegerArrayListExtra("pecas_ids", ArrayList(pecasIds))
                startActivity(setupIntent)
            }
        }
    }

    private fun atualizarPainelResumo() {
        val custoTotal = pecasSelecionadas.sumOf { it.preco }
        val hpTotal = pecasSelecionadas.sumOf { it.ganho_hp }

        textoCustoTotal.text = "R$ ${String.format("%.2f", custoTotal)} / R$ ${String.format("%.2f", orcamento)}"
        textoHpTotal.text = "🚀 +$hpTotal HP"

        if (orcamento > 0) {
            val progress = ((custoTotal / orcamento) * 100).toInt()
            progressBarOrcamentoTotal.progress = progress
            
            // Muda a cor pra vermelho se estourar o orçamento
            if (custoTotal > orcamento) {
                textoCustoTotal.setTextColor(ContextCompat.getColor(this, R.color.danger))
                progressBarOrcamentoTotal.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.danger))
            } else {
                textoCustoTotal.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
                progressBarOrcamentoTotal.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.accent_primary))
            }
        }
    }

    private fun carregarPecas(pecasIdsAnteriores: ArrayList<Int>) {
        layoutLoadingPecas.visibility = View.VISIBLE
        recyclerViewPecas.visibility = View.GONE

        apiService.listarPecasPorCarro(carroId).enqueue(object : Callback<List<Peca>> {
            override fun onResponse(call: Call<List<Peca>>, response: Response<List<Peca>>) {
                layoutLoadingPecas.visibility = View.GONE
                recyclerViewPecas.visibility = View.VISIBLE

                if (response.isSuccessful && response.body() != null) {
                    todasPecas = response.body()!!
                    
                    // Pré-seleciona as peças existentes
                    pecasSelecionadas.clear()
                    val pecasAnteriores = todasPecas.filter { it.id in pecasIdsAnteriores }
                    pecasAnteriores.forEach { it.selecionada = true }
                    pecasSelecionadas.addAll(pecasAnteriores)
                    
                    atualizarListaPecas(todasPecas)
                    atualizarPainelResumo()
                }
            }

            override fun onFailure(call: Call<List<Peca>>, t: Throwable) {
                layoutLoadingPecas.visibility = View.GONE
                recyclerViewPecas.visibility = View.VISIBLE
                Toast.makeText(this@SetupPecasActivity, "Erro ao carregar peças: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun atualizarListaPecas(pecas: List<Peca>) {
        val pecaAdapter = PecaAdapter(pecas, this@SetupPecasActivity, false) { peca, selecionada ->
            if (selecionada) {
                // Evitar duplicatas acidentais
                if (pecasSelecionadas.none { it.id == peca.id }) {
                    pecasSelecionadas.add(peca)
                }
            } else {
                pecasSelecionadas.removeAll { it.id == peca.id }
            }
            atualizarPainelResumo()
        }
        recyclerViewPecas.adapter = pecaAdapter
    }
}
