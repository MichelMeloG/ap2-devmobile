package com.example.appmobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
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
    private lateinit var progressBarVin: ProgressBar
    private lateinit var cardResultadoVin: CardView
    private lateinit var textViewVinCarro: TextView
    private lateinit var textViewVinDetalhes: TextView
    private lateinit var buttonUsarVin: Button
    
    private val apiService = RetrofitClient.apiService
    
    private var resultadoAtual: VinResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_vin)

        editTextVin = findViewById(R.id.editTextVin)
        buttonConsultarVin = findViewById(R.id.buttonConsultarVin)
        progressBarVin = findViewById(R.id.progressBarVin)
        cardResultadoVin = findViewById(R.id.cardResultadoVin)
        textViewVinCarro = findViewById(R.id.textViewVinCarro)
        textViewVinDetalhes = findViewById(R.id.textViewVinDetalhes)
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
                registrarCarroEAvancar(vinResult)
            }
        }
    }

    private fun consultarVin(vin: String) {
        progressBarVin.visibility = View.VISIBLE
        cardResultadoVin.visibility = View.GONE
        buttonConsultarVin.isEnabled = false

        apiService.consultarVin(vin).enqueue(object : Callback<VinResult> {
            override fun onResponse(call: Call<VinResult>, response: Response<VinResult>) {
                progressBarVin.visibility = View.GONE
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
                progressBarVin.visibility = View.GONE
                buttonConsultarVin.isEnabled = true
                Toast.makeText(this@ConsultaVinActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun exibirResultado(resultado: VinResult) {
        textViewVinCarro.text = "${resultado.marca} ${resultado.modelo} ${resultado.ano}"
        
        val detalhes = """
            Motor: ${resultado.motor} (${resultado.cilindros} cil / ${resultado.potencia_hp} hp)
            Combustível: ${resultado.combustivel}
            Carroceria: ${resultado.carroceria} (${resultado.portas} portas)
            Tração: ${resultado.tracao}
            Transmissão: ${resultado.transmissao}
            Fabricante: ${resultado.fabricante} (${resultado.pais_origem})
        """.trimIndent()
        
        textViewVinDetalhes.text = detalhes
        cardResultadoVin.visibility = View.VISIBLE
    }

    private fun registrarCarroEAvancar(resultado: VinResult) {
        val nomeCarroCompleto = "${resultado.marca} ${resultado.modelo} ${resultado.ano}"
        val request = CarroRequest(modelo = nomeCarroCompleto, categoria = "Carro")
        
        buttonUsarVin.isEnabled = false
        progressBarVin.visibility = View.VISIBLE
        
        apiService.criarCarro(request).enqueue(object : Callback<Carro> {
            override fun onResponse(call: Call<Carro>, response: Response<Carro>) {
                buttonUsarVin.isEnabled = true
                progressBarVin.visibility = View.GONE
                
                if (response.isSuccessful && response.body() != null) {
                    val carroSalvo = response.body()!!
                    
                    val intent = Intent(this@ConsultaVinActivity, SetupPecasActivity::class.java)
                    intent.putExtra("carro_id", carroSalvo.id)
                    intent.putExtra("orcamento", 50000.0) // Orçamento padrão para a demo VIN
                    intent.putExtra("nome_projeto", "Projeto ${resultado.modelo}")
                    intent.putExtra("marca_carro", resultado.marca)
                    intent.putExtra("modelo_carro", resultado.modelo)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ConsultaVinActivity, "Erro ao registrar veículo no banco", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Carro>, t: Throwable) {
                buttonUsarVin.isEnabled = true
                progressBarVin.visibility = View.GONE
                Toast.makeText(this@ConsultaVinActivity, "Erro de rede ao salvar veículo", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
