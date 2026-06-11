package com.example.appmobile.api

data class ProjetoRequest(
    val nome: String,
    val orcamento_maximo: Double,
    val carro_id: Int,
    val pecas_ids: List<Int>
)
