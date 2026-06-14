package com.example.appmobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.adapters.ProjetoAdapter
import com.example.appmobile.api.RetrofitClient
import com.example.appmobile.models.Projeto
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerViewProjetos: RecyclerView
    private lateinit var fabNovoProjeto: FloatingActionButton
    private lateinit var buttonOficinas: Button
    private lateinit var buttonVin: Button
    private lateinit var projetoAdapter: ProjetoAdapter
    
    private val apiService = RetrofitClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        recyclerViewProjetos = findViewById(R.id.recyclerViewProjetos)
        fabNovoProjeto = findViewById(R.id.fabNovoProjeto)
        buttonOficinas = findViewById(R.id.buttonOficinas)
        buttonVin = findViewById(R.id.buttonVin)

        recyclerViewProjetos.layoutManager = LinearLayoutManager(this)
        
        projetoAdapter = ProjetoAdapter(emptyList(), this, object : ProjetoAdapter.OnProjetoClickListener {
            override fun onProjetoClick(projeto: Projeto) {
                val intent = Intent(this@DashboardActivity, DetalhesProjetoActivity::class.java)
                intent.putExtra("projeto_id", projeto.id)
                startActivity(intent)
            }

            override fun onEditarClick(projeto: Projeto) {
                mostrarDialogEditar(projeto)
            }

            override fun onDeletarClick(projeto: Projeto) {
                androidx.appcompat.app.AlertDialog.Builder(this@DashboardActivity)
                    .setTitle("Excluir Projeto")
                    .setMessage("Tem certeza que deseja deletar o projeto '${projeto.nome}'?")
                    .setPositiveButton("Sim") { _, _ ->
                        deletarProjeto(projeto.id)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        })
        recyclerViewProjetos.adapter = projetoAdapter

        fabNovoProjeto.setOnClickListener {
            val intent = Intent(this@DashboardActivity, CadastroBaseActivity::class.java)
            startActivity(intent)
        }

        buttonOficinas.setOnClickListener {
            val intent = Intent(this@DashboardActivity, OficinaFinderActivity::class.java)
            startActivity(intent)
        }

        buttonVin.setOnClickListener {
            val intent = Intent(this@DashboardActivity, ConsultaVinActivity::class.java)
            startActivity(intent)
        }

        carregarProjetos()
    }

    override fun onResume() {
        super.onResume()
        carregarProjetos()
    }

    private fun carregarProjetos() {
        apiService.listarProjetos().enqueue(object : Callback<List<Projeto>> {
            override fun onResponse(call: Call<List<Projeto>>, response: Response<List<Projeto>>) {
                if (response.isSuccessful && response.body() != null) {
                    projetoAdapter.updateProjetos(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<Projeto>>, t: Throwable) {
                Toast.makeText(this@DashboardActivity, "Erro ao carregar projetos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deletarProjeto(projetoId: Int) {
        apiService.deletarProjeto(projetoId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Toast.makeText(this@DashboardActivity, "Projeto deletado", Toast.LENGTH_SHORT).show()
                carregarProjetos()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@DashboardActivity, "Erro ao deletar", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDialogEditar(projeto: Projeto) {
        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val editNome = android.widget.EditText(this)
        editNome.hint = "Nome do Projeto"
        editNome.setText(projeto.nome)
        layout.addView(editNome)

        val editOrcamento = android.widget.EditText(this)
        editOrcamento.hint = "Orçamento Máximo"
        editOrcamento.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        editOrcamento.setText(projeto.orcamentoMaximo.toString())
        layout.addView(editOrcamento)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Editar Projeto")
            .setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                val novoNome = editNome.text.toString().trim()
                val novoOrcamentoStr = editOrcamento.text.toString().trim()

                if (novoNome.isNotEmpty() && novoOrcamentoStr.isNotEmpty()) {
                    try {
                        val novoOrcamento = novoOrcamentoStr.toDouble()
                        
                        val request = com.example.appmobile.api.ProjetoRequest(
                            nome = novoNome,
                            orcamento_maximo = novoOrcamento,
                            carro_id = projeto.carroId,
                            pecas_ids = projeto.pecasIds
                        )
                        
                        apiService.atualizarProjeto(projeto.id, request).enqueue(object : Callback<Projeto> {
                            override fun onResponse(call: Call<Projeto>, response: Response<Projeto>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(this@DashboardActivity, "Projeto atualizado", Toast.LENGTH_SHORT).show()
                                    carregarProjetos()
                                } else {
                                    Toast.makeText(this@DashboardActivity, "Erro ao atualizar. Verifique se o orçamento é suficiente.", Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onFailure(call: Call<Projeto>, t: Throwable) {
                                Toast.makeText(this@DashboardActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } catch (e: Exception) {
                        Toast.makeText(this, "Orçamento inválido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
