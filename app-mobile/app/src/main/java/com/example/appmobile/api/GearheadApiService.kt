package com.example.appmobile.api

import com.example.appmobile.models.Carro
import com.example.appmobile.models.Peca
import com.example.appmobile.models.Projeto
import com.example.appmobile.models.Marca
import com.example.appmobile.models.Modelo
import com.example.appmobile.models.Trim
import com.example.appmobile.models.VinResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GearheadApiService {
    // === Endpoints de Carros ===
    @GET("/carros")
    fun listarCarros(): Call<List<Carro>>

    @POST("/carros")
    fun criarCarro(@Body carro: CarroRequest): Call<Carro>

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

    // === Endpoints Externos (Proxy API Ninjas / NHTSA) ===
    @GET("/api/external/marcas")
    fun listarMarcas(): Call<List<Marca>>

    @GET("/api/external/modelos")
    fun listarModelos(@Query("marca") marca: String): Call<List<Modelo>>

    @GET("/api/external/trims")
    fun listarTrims(
        @Query("marca") marca: String,
        @Query("modelo") modelo: String
    ): Call<List<Trim>>

    @GET("/api/external/vin/{vin}")
    fun consultarVin(@Path("vin") vin: String): Call<VinResult>

    // === Endpoint de IA ===
    @POST("/pecas/resumo-ia")
    fun gerarResumoIA(@Body dados: Map<String, @JvmSuppressWildcards Any>): Call<Map<String, String>>
}
