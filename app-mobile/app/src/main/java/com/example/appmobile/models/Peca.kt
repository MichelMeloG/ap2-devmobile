package com.example.appmobile.models

data class Peca(
    val id: Int,
    val nome: String,
    val preco: Double,
    val tipo: String,
    val ganho_hp: Int = 0,
    val descricao: String = ""
) {
    override fun toString(): String {
        return "$nome (R$ ${String.format("%.2f", preco)})"
    }
}
