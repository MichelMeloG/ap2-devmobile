package com.example.appmobile.models

data class Projeto(
    val nome: String,
    val orcamentoMaximo: Double,
    val carroId: Int,
    val pecas: List<Peca>
) {
    val id: Int = 0 // ou var se for inicializado depois
    val custoTotalCalculado: Double = pecas.sumOf { it.preco }
    val criado_em: String? = null // Pode vir da API depois

    override fun toString(): String {
        return nome
    }
}
