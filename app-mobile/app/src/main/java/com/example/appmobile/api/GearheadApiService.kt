package com.example.appmobile.api

import com.example.appmobile.models.Carro
import com.example.appmobile.models.Peca
import com.example.appmobile.models.Projeto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GearheadApiService {
    // === Endpoints de Carros ===
    @GET("/carros")
    fun listarCarros(): Call<List<Carro>>

    // === Endpoints de Peças ===
    @GET("/pecas/{carro_id}")
    fun listarPecasPorCarro(@Path("carro_id") carroId: Int): Call<List<Peca>>

    @GET("/pecas")
    fun listarTodasPecas(): Call<List<Peca>>

    // === Endpoints de Projetos ===
    @POST("/projetos")
    fun criarProjeto(@Body projeto: ProjetoRequest): Call<Projeto>

    @GET("/projetos")
    fun listarProjetos(): Call<List<Projeto>>

    @GET("/projetos/{projeto_id}")
    fun obterProjeto(@Path("projeto_id") projetoId: Int): Call<Projeto>

    @PUT("/projetos/{projeto_id}")
    fun atualizarProjeto(
        @Path("projeto_id") projetoId: Int,
        @Body projeto: ProjetoRequest
    ): Call<Projeto>

    @DELETE("/projetos/{projeto_id}")
    fun deletarProjeto(@Path("projeto_id") projetoId: Int): Call<Void>
}
