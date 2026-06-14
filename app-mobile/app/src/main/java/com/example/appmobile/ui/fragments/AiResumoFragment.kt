package com.example.appmobile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.appmobile.R
import com.example.appmobile.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AiResumoFragment : Fragment() {

    private lateinit var cardResumoIA: CardView
    private lateinit var textViewResumoIA: TextView
    private lateinit var progressBarResumoIA: ProgressBar

    private var modeloCarro: String = ""
    private var pecasNomes: List<String> = emptyList()
    private var pecasPrecos: List<Double> = emptyList()
    private var pecasGanhos: List<Int> = emptyList()

    private val apiService = RetrofitClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            modeloCarro = it.getString(ARG_MODELO) ?: ""
            pecasNomes = it.getStringArrayList(ARG_NOMES)?.toList() ?: emptyList()
            val precosArray = it.getDoubleArray(ARG_PRECOS)
            pecasPrecos = precosArray?.toList() ?: emptyList()
            pecasGanhos = it.getIntegerArrayList(ARG_GANHOS)?.toList() ?: emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ai_resumo, container, false)
        cardResumoIA = view.findViewById(R.id.cardResumoIA)
        textViewResumoIA = view.findViewById(R.id.textViewResumoIA)
        progressBarResumoIA = view.findViewById(R.id.progressBarResumoIA)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carregarResumoIA()
    }

    private fun carregarResumoIA() {
        if (pecasNomes.isEmpty()) {
            cardResumoIA.visibility = View.GONE
            return
        }

        cardResumoIA.visibility = View.VISIBLE
        progressBarResumoIA.visibility = View.VISIBLE
        textViewResumoIA.visibility = View.GONE

        val pecasData = mutableListOf<Map<String, Any>>()
        for (i in pecasNomes.indices) {
            pecasData.add(mapOf(
                "nome" to pecasNomes[i],
                "preco" to (if (i < pecasPrecos.size) pecasPrecos[i] else 0.0),
                "ganho_hp" to (if (i < pecasGanhos.size) pecasGanhos[i] else 0)
            ))
        }

        val requestBody = mapOf<String, Any>(
            "modelo_carro" to modeloCarro,
            "pecas" to pecasData
        )

        apiService.gerarResumoIA(requestBody).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (!isAdded) return
                progressBarResumoIA.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    textViewResumoIA.text = response.body()!!["resumo"] ?: "Resumo indisponível."
                    textViewResumoIA.visibility = View.VISIBLE
                } else {
                    textViewResumoIA.text = "Não foi possível gerar a análise da IA no momento."
                    textViewResumoIA.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                if (!isAdded) return
                progressBarResumoIA.visibility = View.GONE
                textViewResumoIA.text = "Erro de conexão ao contatar a inteligência artificial."
                textViewResumoIA.visibility = View.VISIBLE
            }
        })
    }

    companion object {
        private const val ARG_MODELO = "modelo"
        private const val ARG_NOMES = "nomes"
        private const val ARG_PRECOS = "precos"
        private const val ARG_GANHOS = "ganhos"

        @JvmStatic
        fun newInstance(modelo: String, nomes: ArrayList<String>, precos: DoubleArray, ganhos: ArrayList<Int>) =
            AiResumoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MODELO, modelo)
                    putStringArrayList(ARG_NOMES, nomes)
                    putDoubleArray(ARG_PRECOS, precos)
                    putIntegerArrayList(ARG_GANHOS, ganhos)
                }
            }
    }
}
