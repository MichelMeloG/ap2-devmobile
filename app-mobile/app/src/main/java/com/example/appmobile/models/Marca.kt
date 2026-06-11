package com.example.appmobile.models

data class Marca(
    val make: String
) {
    override fun toString(): String {
        return make.replaceFirstChar { it.uppercase() }
    }
}
