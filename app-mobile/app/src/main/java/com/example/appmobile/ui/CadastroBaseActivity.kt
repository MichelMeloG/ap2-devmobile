package com.example.appmobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appmobile.R
import com.example.appmobile.api.RetrofitClient
import com.example.appmobile.models.Carro
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroBaseActivity : AppCompatActivity() {

    private lateinit var spinnerCarros: Spinner
    private lateinit var editTextOrcamento: EditText
    private lateinit var editTextNomeProjeto: EditText
    private lateinit var buttonProximo: Button
    
    private val apiService = RetrofitClient.apiService
    private var carrosList = listOf<Carro>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_base)

        spinnerCarros = findViewById(R.id.spinnerCarros)
        editTextOrcamento = findViewById(R.id.editTextOrcamento)
        editTextNomeProjeto = findViewById(R.id.editTextNomeProjeto)
        buttonProximo = findViewById(R.id.buttonProximo)

        carregarCarros()

        buttonProximo.setOnClickListener { validarEAvancar() }
    }

    private fun carregarCarros() {
        apiService.listarCarros().enqueue(object : Callback<List<Carro>> {
            override fun onResponse(call: Call<List<Carro>>, response: Response<List<Carro>>) {
                if (response.isSuccessful && response.body() != null) {
                    carrosList = response.body()!!
                    val adapter = ArrayAdapter(
                        this@CadastroBaseActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        carrosList
                    )
                    spinnerCarros.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<Carro>>, t: Throwable) {
                Toast.makeText(this@CadastroBaseActivity, "Erro ao carregar carros", Toast.LENGTH_SHORT).show()
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

        try {
            val orcamento = orcamentoStr.toDouble()
            val carroSelecionado = spinnerCarros.selectedItem as Carro

            val intent = Intent(this, SetupPecasActivity::class.java)
            intent.putExtra("carro_id", carroSelecionado.id)
            intent.putExtra("orcamento", orcamento)
            intent.putExtra("nome_projeto", nomeProjeto)
            startActivity(intent)
        } catch (e: NumberFormatException) {
            editTextOrcamento.error = "Valor inválido"
        }
    }
}
