package com.example.appmobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.adapters.PecaAdapter
import com.example.appmobile.api.RetrofitClient
import com.example.appmobile.models.Peca
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetupPecasActivity : AppCompatActivity() {

    private lateinit var recyclerViewPecas: RecyclerView
    private lateinit var switchModoAgressivo: Switch
    private lateinit var buttonVoltar: Button
    private lateinit var buttonConcluir: Button
    
    private val apiService = RetrofitClient.apiService
    
    private var carroId: Int = 0
    private var orcamento: Double = 0.0
    private var nomeProjeto: String = ""
    private val pecasSelecionadas = mutableListOf<Peca>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_pecas)

        recyclerViewPecas = findViewById(R.id.recyclerViewPecas)
        switchModoAgressivo = findViewById(R.id.switchModoAgressivo)
        buttonVoltar = findViewById(R.id.buttonVoltar)
        buttonConcluir = findViewById(R.id.buttonConcluir)

        carroId = intent.getIntExtra("carro_id", 0)
        orcamento = intent.getDoubleExtra("orcamento", 0.0)
        nomeProjeto = intent.getStringExtra("nome_projeto") ?: ""

        recyclerViewPecas.layoutManager = LinearLayoutManager(this)

        carregarPecas()

        buttonVoltar.setOnClickListener { finish() }

        buttonConcluir.setOnClickListener {
            pecasSelecionadas.clear()
            val setupIntent = Intent(this@SetupPecasActivity, DetalhesProjetoActivity::class.java)
            setupIntent.putExtra("carro_id", carroId)
            setupIntent.putExtra("orcamento", orcamento)
            setupIntent.putExtra("nome_projeto", nomeProjeto)
            startActivity(setupIntent)
        }
    }

    private fun carregarPecas() {
        apiService.listarPecasPorCarro(carroId).enqueue(object : Callback<List<Peca>> {
            override fun onResponse(call: Call<List<Peca>>, response: Response<List<Peca>>) {
                if (response.isSuccessful && response.body() != null) {
                    val pecas = response.body()!!
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

            override fun onFailure(call: Call<List<Peca>>, t: Throwable) {
                Toast.makeText(this@SetupPecasActivity, "Erro ao carregar peças", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
