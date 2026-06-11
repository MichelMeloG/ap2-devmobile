package com.example.appmobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appmobile.R
import com.example.appmobile.api.CarroRequest
import com.example.appmobile.api.RetrofitClient
import com.example.appmobile.models.Carro
import com.example.appmobile.models.Marca
import com.example.appmobile.models.Modelo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroBaseActivity : AppCompatActivity() {

    private lateinit var spinnerMarcas: Spinner
    private lateinit var spinnerModelos: Spinner
    private lateinit var editTextOrcamento: EditText
    private lateinit var editTextNomeProjeto: EditText
    private lateinit var buttonProximo: Button
    
    private val apiService = RetrofitClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_base)

        spinnerMarcas = findViewById(R.id.spinnerMarcas)
        spinnerModelos = findViewById(R.id.spinnerModelos)
        editTextOrcamento = findViewById(R.id.editTextOrcamento)
        editTextNomeProjeto = findViewById(R.id.editTextNomeProjeto)
        buttonProximo = findViewById(R.id.buttonProximo)

        carregarMarcas()

        spinnerMarcas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val marcaSelecionada = parent?.getItemAtPosition(position) as? Marca
                marcaSelecionada?.let { carregarModelos(it.make) }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        buttonProximo.setOnClickListener { validarEAvancar() }
    }

    private fun carregarMarcas() {
        apiService.listarMarcas().enqueue(object : Callback<List<Marca>> {
            override fun onResponse(call: Call<List<Marca>>, response: Response<List<Marca>>) {
                if (response.isSuccessful && response.body() != null) {
                    val marcas = response.body()!!
                    val adapter = ArrayAdapter(
                        this@CadastroBaseActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        marcas
                    )
                    spinnerMarcas.adapter = adapter
                } else {
                    Toast.makeText(this@CadastroBaseActivity, "Erro ao carregar marcas (verifique a API KEY)", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Marca>>, t: Throwable) {
                Toast.makeText(this@CadastroBaseActivity, "Erro de rede ao carregar marcas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun carregarModelos(marca: String) {
        apiService.listarModelos(marca).enqueue(object : Callback<List<Modelo>> {
            override fun onResponse(call: Call<List<Modelo>>, response: Response<List<Modelo>>) {
                if (response.isSuccessful && response.body() != null) {
                    val modelos = response.body()!!
                    val adapter = ArrayAdapter(
                        this@CadastroBaseActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        modelos
                    )
                    spinnerModelos.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<Modelo>>, t: Throwable) {
                Toast.makeText(this@CadastroBaseActivity, "Erro ao carregar modelos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validarEAvancar() {
        val nomeProjeto = editTextNomeProjeto.text.toString().trim()
        val orcamentoStr = editTextOrcamento.text.toString().trim()

        if (nomeProjeto.isEmpty()) {
            editTextNomeProjeto.error = "Nome do projeto é obrigatório"
            return
        }

        if (orcamentoStr.isEmpty()) {
            editTextOrcamento.error = "Orçamento é obrigatório"
            return
        }

        val marcaSelecionada = spinnerMarcas.selectedItem as? Marca
        val modeloSelecionado = spinnerModelos.selectedItem as? Modelo

        if (marcaSelecionada == null || modeloSelecionado == null) {
            Toast.makeText(this, "Selecione a marca e o modelo", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val orcamento = orcamentoStr.toDouble()
            
            val nomeCarroCompleto = "${marcaSelecionada.make.replaceFirstChar { it.uppercase() }} ${modeloSelecionado.model}"
            val request = CarroRequest(modelo = nomeCarroCompleto, categoria = "Carro")
            
            buttonProximo.isEnabled = false
            
            // Cria o carro no banco do backend antes de avançar
            apiService.criarCarro(request).enqueue(object : Callback<Carro> {
                override fun onResponse(call: Call<Carro>, response: Response<Carro>) {
                    buttonProximo.isEnabled = true
                    if (response.isSuccessful && response.body() != null) {
                        val carroSalvo = response.body()!!
                        
                        val intent = Intent(this@CadastroBaseActivity, SetupPecasActivity::class.java)
                        intent.putExtra("carro_id", carroSalvo.id)
                        intent.putExtra("orcamento", orcamento)
                        intent.putExtra("nome_projeto", nomeProjeto)
                        intent.putExtra("marca_carro", marcaSelecionada.make)
                        intent.putExtra("modelo_carro", modeloSelecionado.model)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@CadastroBaseActivity, "Erro ao registrar veículo no banco", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Carro>, t: Throwable) {
                    buttonProximo.isEnabled = true
                    Toast.makeText(this@CadastroBaseActivity, "Erro de rede ao salvar veículo", Toast.LENGTH_SHORT).show()
                }
            })

        } catch (e: NumberFormatException) {
            editTextOrcamento.error = "Valor inválido"
        }
    }
}
