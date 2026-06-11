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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerViewProjetos: RecyclerView
    private lateinit var fabNovoProjeto: FloatingActionButton
    private lateinit var projetoAdapter: ProjetoAdapter
    
    private val apiService = RetrofitClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        recyclerViewProjetos = findViewById(R.id.recyclerViewProjetos)
        fabNovoProjeto = findViewById(R.id.fabNovoProjeto)

        recyclerViewProjetos.layoutManager = LinearLayoutManager(this)
        
        projetoAdapter = ProjetoAdapter(emptyList(), this, object : ProjetoAdapter.OnProjetoClickListener {
            override fun onProjetoClick(projeto: Projeto) {
                val intent = Intent(this@DashboardActivity, DetalhesProjetoActivity::class.java)
                intent.putExtra("projeto_id", projeto.id)
                startActivity(intent)
            }

            override fun onDeletarClick(projeto: Projeto) {
                deletarProjeto(projeto.id)
            }
        })
        recyclerViewProjetos.adapter = projetoAdapter

        fabNovoProjeto.setOnClickListener {
            val intent = Intent(this@DashboardActivity, CadastroBaseActivity::class.java)
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
}
