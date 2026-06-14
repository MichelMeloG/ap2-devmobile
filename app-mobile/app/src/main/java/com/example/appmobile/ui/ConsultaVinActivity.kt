package com.example.appmobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.appmobile.R
import com.example.appmobile.api.CarroRequest
import com.example.appmobile.api.RetrofitClient
import com.example.appmobile.models.Carro
import com.example.appmobile.models.VinResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConsultaVinActivity : AppCompatActivity() {

    private lateinit var editTextVin: EditText
    private lateinit var buttonConsultarVin: Button
    private lateinit var layoutLoading: LinearLayout
    private lateinit var cardResultadoVin: CardView
    private lateinit var textViewVinCarro: TextView
    private lateinit var textViewVinDetalhes: TextView
    private lateinit var editTextNomeProjetoVin: EditText
    private lateinit var editTextOrcamentoVin: EditText
    private lateinit var buttonUsarVin: Button
    
    private val apiService = RetrofitClient.apiService
    
    private var resultadoAtual: VinResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_vin)

        editTextVin = findViewById(R.id.editTextVin)
        buttonConsultarVin = findViewById(R.id.buttonConsultarVin)
        layoutLoading = findViewById(R.id.layoutLoading)
        cardResultadoVin = findViewById(R.id.cardResultadoVin)
        textViewVinCarro = findViewById(R.id.textViewVinCarro)
        textViewVinDetalhes = findViewById(R.id.textViewVinDetalhes)
        editTextNomeProjetoVin = findViewById(R.id.editTextNomeProjetoVin)
        editTextOrcamentoVin = findViewById(R.id.editTextOrcamentoVin)
        buttonUsarVin = findViewById(R.id.buttonUsarVin)

        buttonConsultarVin.setOnClickListener {
            val vin = editTextVin.text.toString().trim()
            if (vin.length != 17) {
                editTextVin.error = "O VIN deve ter 17 caracteres"
                return@setOnClickListener
            }
            consultarVin(vin)
        }

        buttonUsarVin.setOnClickListener {
            resultadoAtual?.let { vinResult ->
                val nomeProjeto = editTextNomeProjetoVin.text.toString().trim()
                val orcamentoStr = editTextOrcamentoVin.text.toString().trim()

                if (nomeProjeto.isEmpty()) {
                    editTextNomeProjetoVin.error = "Digite o nome do projeto"
                    return@setOnClickListener
                }
                if (orcamentoStr.isEmpty()) {
                    editTextOrcamentoVin.error = "Digite o orçamento"
                    return@setOnClickListener
                }

                val orcamento = try { orcamentoStr.toDouble() } catch (e: Exception) {
                    editTextOrcamentoVin.error = "Valor inválido"
                    return@setOnClickListener
                }

                registrarCarroEAvancar(vinResult, nomeProjeto, orcamento)
            }
        }
    }

    private fun consultarVin(vin: String) {
        layoutLoading.visibility = View.VISIBLE
        cardResultadoVin.visibility = View.GONE
        buttonConsultarVin.isEnabled = false

        apiService.consultarVin(vin).enqueue(object : Callback<VinResult> {
            override fun onResponse(call: Call<VinResult>, response: Response<VinResult>) {
                layoutLoading.visibility = View.GONE
                buttonConsultarVin.isEnabled = true
                
                if (response.isSuccessful && response.body() != null) {
                    val vinResult = response.body()!!
                    resultadoAtual = vinResult
                    exibirResultado(vinResult)
                } else {
                    Toast.makeText(this@ConsultaVinActivity, "Veículo não encontrado ou erro na API", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<VinResult>, t: Throwable) {
                layoutLoading.visibility = View.GONE
                buttonConsultarVin.isEnabled = true
                Toast.makeText(this@ConsultaVinActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun exibirResultado(resultado: VinResult) {
        val titulo = listOf(resultado.marca, resultado.modelo, resultado.ano)
            .filter { !it.isNullOrBlank() }
            .joinToString(" ")
        textViewVinCarro.text = titulo
        
        val detalhes = mutableListOf<String>()
        
        // Motor e Performance
        val infoMotor = mutableListOf<String>()
        if (!resultado.motor.isNullOrBlank()) infoMotor.add(resultado.motor)
        if (!resultado.cilindros.isNullOrBlank()) infoMotor.add("${resultado.cilindros} cil")
        if (!resultado.potencia_hp.isNullOrBlank()) infoMotor.add("${resultado.potencia_hp} hp")
        if (infoMotor.isNotEmpty()) detalhes.add("🔧 Motor: ${infoMotor.joinToString(" / ")}")
        
        // Combustível
        if (!resultado.combustivel.isNullOrBlank()) detalhes.add("⛽ Combustível: ${resultado.combustivel}")
        
        // Carroceria
        val infoCarroceria = mutableListOf<String>()
        if (!resultado.carroceria.isNullOrBlank()) infoCarroceria.add(resultado.carroceria)
        if (!resultado.portas.isNullOrBlank()) infoCarroceria.add("${resultado.portas} portas")
        if (infoCarroceria.isNotEmpty()) detalhes.add("🚗 Carroceria: ${infoCarroceria.joinToString(" - ")}")
        
        // Tração e Transmissão
        if (!resultado.tracao.isNullOrBlank()) detalhes.add("⚙️ Tração: ${resultado.tracao}")
        if (!resultado.transmissao.isNullOrBlank()) detalhes.add("🔄 Transmissão: ${resultado.transmissao}")
        
        // Fabricante e Origem
        val infoOrigem = mutableListOf<String>()
        if (!resultado.fabricante.isNullOrBlank()) infoOrigem.add(resultado.fabricante)
        if (!resultado.pais_origem.isNullOrBlank()) infoOrigem.add(resultado.pais_origem)
        if (infoOrigem.isNotEmpty()) detalhes.add("🏭 Fabricante: ${infoOrigem.joinToString(" / ")}")
        
        if (detalhes.isEmpty()) {
            textViewVinDetalhes.text = "Detalhes não disponíveis para este chassi."
        } else {
            textViewVinDetalhes.text = detalhes.joinToString("\n")
        }

        // Preencher nome do projeto sugerido
        val modeloNome = listOf(resultado.marca, resultado.modelo).filter { !it.isNullOrBlank() }.joinToString(" ")
        editTextNomeProjetoVin.setText("Projeto $modeloNome")
        
        cardResultadoVin.visibility = View.VISIBLE
    }

    private fun registrarCarroEAvancar(resultado: VinResult, nomeProjeto: String, orcamento: Double) {
        val nomeCarroCompleto = listOf(resultado.marca, resultado.modelo, resultado.ano)
            .filter { !it.isNullOrBlank() }
            .joinToString(" ")
        val request = CarroRequest(modelo = nomeCarroCompleto, categoria = "Carro")
        
        buttonUsarVin.isEnabled = false
        layoutLoading.visibility = View.VISIBLE
        
        apiService.criarCarro(request).enqueue(object : Callback<Carro> {
            override fun onResponse(call: Call<Carro>, response: Response<Carro>) {
                buttonUsarVin.isEnabled = true
                layoutLoading.visibility = View.GONE
                
                if (response.isSuccessful && response.body() != null) {
                    val carroSalvo = response.body()!!
                    
                    val intent = Intent(this@ConsultaVinActivity, SetupPecasActivity::class.java)
                    intent.putExtra("carro_id", carroSalvo.id)
                    intent.putExtra("orcamento", orcamento)
                    intent.putExtra("nome_projeto", nomeProjeto)
                    intent.putExtra("marca_carro", resultado.marca ?: "")
                    intent.putExtra("modelo_carro", resultado.modelo ?: "")
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ConsultaVinActivity, "Erro ao registrar veículo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Carro>, t: Throwable) {
                buttonUsarVin.isEnabled = true
                layoutLoading.visibility = View.GONE
                Toast.makeText(this@ConsultaVinActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
