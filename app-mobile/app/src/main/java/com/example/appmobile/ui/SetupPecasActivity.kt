package com.example.appmobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.adapters.PecaAdapter
import com.example.appmobile.api.RetrofitClient
import com.example.appmobile.models.Peca
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetupPecasActivity : AppCompatActivity() {

    private lateinit var recyclerViewPecas: RecyclerView
    private lateinit var switchModoAgressivo: Switch
    private lateinit var buttonVoltar: MaterialButton
    private lateinit var buttonConcluir: MaterialButton
    private lateinit var layoutLoadingPecas: LinearLayout
    
    private val apiService = RetrofitClient.apiService
    
    private var carroId: Int = 0
    private var orcamento: Double = 0.0
    private var nomeProjeto: String = ""
    private var marcaCarro: String = ""
    private var modeloCarro: String = ""
    private val pecasSelecionadas = mutableListOf<Peca>()
    private var todasPecas = listOf<Peca>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_pecas)

        recyclerViewPecas = findViewById(R.id.recyclerViewPecas)
        switchModoAgressivo = findViewById(R.id.switchModoAgressivo)
        buttonVoltar = findViewById(R.id.buttonVoltar)
        buttonConcluir = findViewById(R.id.buttonConcluir)
        layoutLoadingPecas = findViewById(R.id.layoutLoadingPecas)

        carroId = intent.getIntExtra("carro_id", 0)
        orcamento = intent.getDoubleExtra("orcamento", 0.0)
        nomeProjeto = intent.getStringExtra("nome_projeto") ?: ""
        marcaCarro = intent.getStringExtra("marca_carro") ?: ""
        modeloCarro = intent.getStringExtra("modelo_carro") ?: ""

        recyclerViewPecas.layoutManager = LinearLayoutManager(this)

        carregarPecas()

        // Switch "Modo Track": filtra peças mecânicas (performance) quando ativado
        switchModoAgressivo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val pecasFiltradas = todasPecas.filter { it.tipo == "Mecânica" }
                atualizarListaPecas(pecasFiltradas)
            } else {
                atualizarListaPecas(todasPecas)
            }
        }

        buttonVoltar.setOnClickListener { finish() }

        buttonConcluir.setOnClickListener {
            if (pecasSelecionadas.isEmpty()) {
                Toast.makeText(this, "Selecione ao menos uma peça", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pecasIds = pecasSelecionadas.map { it.id }

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

    private fun carregarPecas() {
        // Mostra loading e esconde lista
        layoutLoadingPecas.visibility = View.VISIBLE
        recyclerViewPecas.visibility = View.GONE

        apiService.listarPecasPorCarro(carroId).enqueue(object : Callback<List<Peca>> {
            override fun onResponse(call: Call<List<Peca>>, response: Response<List<Peca>>) {
                layoutLoadingPecas.visibility = View.GONE
                recyclerViewPecas.visibility = View.VISIBLE

                if (response.isSuccessful && response.body() != null) {
                    todasPecas = response.body()!!
                    atualizarListaPecas(todasPecas)
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
        val pecaAdapter = PecaAdapter(pecas, this@SetupPecasActivity) { peca, selecionada ->
            if (selecionada) {
                if (!pecasSelecionadas.contains(peca)) {
                    pecasSelecionadas.add(peca)
                }
            } else {
                pecasSelecionadas.remove(peca)
            }
        }
        recyclerViewPecas.adapter = pecaAdapter
    }
}
