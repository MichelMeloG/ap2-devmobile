package com.example.appmobile.models

data class Peca(
    val id: Int,
    val nome: String,
    val preco: Double,
    val tipo: String,
    var selecionada: Boolean = false
) {
    override fun toString(): String {
        return "$nome (R$ ${String.format("%.2f", preco)})"
    }
}
