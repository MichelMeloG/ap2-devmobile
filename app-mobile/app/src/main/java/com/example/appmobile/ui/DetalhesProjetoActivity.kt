package com.example.appmobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.adapters.PecaAdapter
import com.example.appmobile.api.ProjetoRequest
import com.example.appmobile.api.RetrofitClient
import com.example.appmobile.models.Peca
import com.example.appmobile.models.Projeto
import com.example.appmobile.models.Trim
import com.example.appmobile.models.Carro
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalhesProjetoActivity : AppCompatActivity() {

    private lateinit var textViewNomeProjeto: TextView
    private lateinit var cardFichaTecnica: CardView
    private lateinit var textViewFichaNome: TextView
    private lateinit var textViewFichaGeracao: TextView
    private lateinit var textViewFichaAnos: TextView
    private lateinit var progressBarOrcamento: ProgressBar
    private lateinit var textViewProgresso: TextView
    private lateinit var recyclerViewPecasDetalhes: RecyclerView
    private lateinit var buttonSalvar: MaterialButton
    
    private val apiService = RetrofitClient.apiService

    private var carroId: Int = 0
    private var orcamento: Double = 0.0
    private var nomeProjeto: String = ""
    private var marcaCarro: String = ""
    private var modeloCarro: String = ""
    private var pecasIds: List<Int> = listOf()
    private var pecasSelecionadas: List<Peca> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_projeto)

        textViewNomeProjeto = findViewById(R.id.textViewNomeProjeto)
        cardFichaTecnica = findViewById(R.id.cardFichaTecnica)
        textViewFichaNome = findViewById(R.id.textViewFichaNome)
        textViewFichaGeracao = findViewById(R.id.textViewFichaGeracao)
        textViewFichaAnos = findViewById(R.id.textViewFichaAnos)
        progressBarOrcamento = findViewById(R.id.progressBarOrcamento)
        textViewProgresso = findViewById(R.id.textViewProgresso)
        recyclerViewPecasDetalhes = findViewById(R.id.recyclerViewPecasDetalhes)
        buttonSalvar = findViewById(R.id.buttonSalvar)

        val projetoId = intent.getIntExtra("projeto_id", 0)

        recyclerViewPecasDetalhes.layoutManager = LinearLayoutManager(this)

        if (projetoId > 0) {
            carregarProjetoExistente(projetoId)
        } else {
            carroId = intent.getIntExtra("carro_id", 0)
            orcamento = intent.getDoubleExtra("orcamento", 0.0)
            nomeProjeto = intent.getStringExtra("nome_projeto") ?: ""
            marcaCarro = intent.getStringExtra("marca_carro") ?: ""
            modeloCarro = intent.getStringExtra("modelo_carro") ?: ""
            pecasIds = intent.getIntegerArrayListExtra("pecas_ids") ?: arrayListOf()

            textViewNomeProjeto.text = "🏁 $nomeProjeto"

            carregarPecasSelecionadas()
            
            if (marcaCarro.isNotEmpty() && modeloCarro.isNotEmpty()) {
                carregarFichaTecnica()
            }

            buttonSalvar.setOnClickListener { salvarProjeto() }
        }
    }

    private fun carregarProjetoExistente(projetoId: Int) {
        buttonSalvar.visibility = View.VISIBLE
        buttonSalvar.text = "✏️ Editar Peças"
        
        buttonSalvar.setOnClickListener {
            val editIntent = Intent(this@DetalhesProjetoActivity, SetupPecasActivity::class.java)
            editIntent.putExtra("projeto_id", projetoId)
            editIntent.putExtra("carro_id", carroId)
            editIntent.putExtra("orcamento", orcamento)
            editIntent.putExtra("nome_projeto", nomeProjeto)
            editIntent.putExtra("marca_carro", marcaCarro)
            editIntent.putExtra("modelo_carro", modeloCarro)
            editIntent.putIntegerArrayListExtra("pecas_ids", ArrayList(pecasIds))
            startActivity(editIntent)
        }

        apiService.obterProjeto(projetoId).enqueue(object : Callback<Projeto> {
            override fun onResponse(call: Call<Projeto>, response: Response<Projeto>) {
                if (response.isSuccessful && response.body() != null) {
                    val projeto = response.body()!!
                    nomeProjeto = projeto.nome
                    orcamento = projeto.orcamentoMaximo
                    carroId = projeto.carroId
                    pecasIds = projeto.pecasIds
                    
                    textViewNomeProjeto.text = "🏁 $nomeProjeto"
                    
                    apiService.listarCarros().enqueue(object : Callback<List<Carro>> {
                        override fun onResponse(call: Call<List<Carro>>, res: Response<List<Carro>>) {
                            if (res.isSuccessful && res.body() != null) {
                                val carro = res.body()!!.find { it.id == carroId }
                                if (carro != null) {
                                    val parts = carro.modelo.split(" ")
                                    if (parts.isNotEmpty()) {
                                        marcaCarro = parts[0]
                                        modeloCarro = parts.drop(1).joinToString(" ")
                                        carregarFichaTecnica()
                                    }
                                }
                            }
                            carregarPecasSelecionadas()
                        }

                        override fun onFailure(call: Call<List<Carro>>, t: Throwable) {
                            carregarPecasSelecionadas()
                        }
                    })
                } else {
                    Toast.makeText(this@DetalhesProjetoActivity, "Erro ao carregar projeto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Projeto>, t: Throwable) {
                Toast.makeText(this@DetalhesProjetoActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun carregarPecasSelecionadas() {
        apiService.listarPecasPorCarro(carroId).enqueue(object : Callback<List<Peca>> {
            override fun onResponse(call: Call<List<Peca>>, response: Response<List<Peca>>) {
                if (response.isSuccessful && response.body() != null) {
                    pecasSelecionadas = response.body()!!.filter { it.id in pecasIds }
                    
                    val pecaAdapter = PecaAdapter(pecasSelecionadas, this@DetalhesProjetoActivity, true) { _, _ -> }
                    recyclerViewPecasDetalhes.adapter = pecaAdapter

                    atualizarProgresso()
                    injetarAiResumoFragment()
                }
            }

            override fun onFailure(call: Call<List<Peca>>, t: Throwable) {
                Toast.makeText(this@DetalhesProjetoActivity, "Erro ao carregar detalhes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun carregarFichaTecnica() {
        apiService.listarTrims(marcaCarro, modeloCarro).enqueue(object : Callback<List<Trim>> {
            override fun onResponse(call: Call<List<Trim>>, response: Response<List<Trim>>) {
                if (response.isSuccessful && response.body() != null) {
                    val trims = response.body()!!
                    if (trims.isNotEmpty()) {
                        val trim = trims.first()
                        cardFichaTecnica.visibility = View.VISIBLE
                        val trimName = trim.trim ?: "Motor não especificado"
                        textViewFichaNome.text = trimName.replace(" Base", "", ignoreCase = true)
                        textViewFichaGeracao.text = "${trim.generation ?: ""} - ${trim.serie ?: ""}".trim(' ', '-')

                        val startYear = trim.trimStartYear ?: trim.generationYearBegin ?: ""
                        val endYear = trim.trimEndYear ?: trim.generationYearEnd ?: ""
                        
                        if (startYear.isNotEmpty() && endYear.isNotEmpty() && startYear != endYear) {
                            textViewFichaAnos.text = "Ano: $startYear a $endYear"
                        } else if (startYear.isNotEmpty()) {
                            textViewFichaAnos.text = "Ano: $startYear"
                        } else {
                            textViewFichaAnos.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Trim>>, t: Throwable) {
                // Não precisa exibir erro, apenas não mostra o card
            }
        })
    }

    private fun injetarAiResumoFragment() {
        if (pecasSelecionadas.isEmpty()) return
        
        val modeloCompleto = "$marcaCarro $modeloCarro".trim()
        val nomes = ArrayList(pecasSelecionadas.map { it.nome })
        val precos = pecasSelecionadas.map { it.preco }.toDoubleArray()
        val ganhos = ArrayList(pecasSelecionadas.map { it.ganho_hp })

        val fragment = com.example.appmobile.ui.fragments.AiResumoFragment.newInstance(
            modeloCompleto,
            nomes,
            precos,
            ganhos
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerAiResumo, fragment)
            .commit()
    }

    private fun atualizarProgresso() {
        var custoTotal = 0.0
        var ganhoTotalHp = 0
        for (peca in pecasSelecionadas) {
            custoTotal += peca.preco
            ganhoTotalHp += peca.ganho_hp
        }

        val percentual = if (orcamento > 0) ((custoTotal / orcamento) * 100).toInt() else 0
        progressBarOrcamento.progress = percentual

        val hpText = if (ganhoTotalHp > 0) " | +${ganhoTotalHp} hp" else ""
        textViewProgresso.text = "R$ ${String.format("%.2f", custoTotal)} / R$ ${String.format("%.2f", orcamento)} ($percentual%)$hpText"
    }

    private fun salvarProjeto() {
        val projetoRequest = ProjetoRequest(nomeProjeto, orcamento, carroId, pecasIds)

        apiService.criarProjeto(projetoRequest).enqueue(object : Callback<Projeto> {
            override fun onResponse(call: Call<Projeto>, response: Response<Projeto>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DetalhesProjetoActivity, "Projeto salvo com sucesso! 🏁", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@DetalhesProjetoActivity, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@DetalhesProjetoActivity, "Orçamento insuficiente para as peças selecionadas", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Projeto>, t: Throwable) {
                Toast.makeText(this@DetalhesProjetoActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
