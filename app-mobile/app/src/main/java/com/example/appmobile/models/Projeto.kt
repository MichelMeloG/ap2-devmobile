package com.example.appmobile.models

import com.google.gson.annotations.SerializedName

data class Projeto(
    val id: Int = 0,
    val nome: String,
    @SerializedName("orcamento_maximo")
    val orcamentoMaximo: Double,
    @SerializedName("carro_id")
    val carroId: Int,
    @SerializedName("pecas_ids")
    val pecasIds: List<Int> = emptyList(),
    @SerializedName("custo_total_calculado")
    val custoTotalCalculado: Double = 0.0,
    @SerializedName("criado_em")
    val criadoEm: String? = null
) {
    override fun toString(): String {
        return nome
    }
}
