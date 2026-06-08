package com.example.appmobile.api;

import retrofit2.Call;
import retrofit2.http.*;
import com.example.appmobile.models.Carro;
import com.example.appmobile.models.Peca;
import com.example.appmobile.models.Projeto;
import java.util.List;

public interface GearheadApiService {
    
    // === Endpoints de Carros ===
    @GET("/carros")
    Call<List<Carro>> listarCarros();

    // === Endpoints de Peças ===
    @GET("/pecas/{carro_id}")
    Call<List<Peca>> listarPecasPorCarro(@Path("carro_id") int carroId);

    @GET("/pecas")
    Call<List<Peca>> listarTodasPecas();

    // === Endpoints de Projetos ===
    @POST("/projetos")
    Call<Projeto> criarProjeto(@Body ProjetoRequest projeto);

    @GET("/projetos")
    Call<List<Projeto>> listarProjetos();

    @GET("/projetos/{projeto_id}")
    Call<Projeto> obterProjeto(@Path("projeto_id") int projetoId);

    @PUT("/projetos/{projeto_id}")
    Call<Projeto> atualizarProjeto(@Path("projeto_id") int projetoId, @Body ProjetoRequest projeto);

    @DELETE("/projetos/{projeto_id}")
    Call<Void> deletarProjeto(@Path("projeto_id") int projetoId);
}
