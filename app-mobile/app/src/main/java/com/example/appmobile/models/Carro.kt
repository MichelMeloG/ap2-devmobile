package com.example.appmobile.models

data class Carro(
    val id: Int,
    val modelo: String,
    val categoria: String
) {
    override fun toString(): String {
        return modelo
    }
}
