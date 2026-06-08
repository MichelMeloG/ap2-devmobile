package com.example.appmobile.api;

import java.util.List;

public class ProjetoRequest {
    private String nome;
    private double orcamento_maximo;
    private int carro_id;
    private List<Integer> pecas_ids;

    public ProjetoRequest(String nome, double orcamento_maximo, int carro_id, List<Integer> pecas_ids) {
        this.nome = nome;
        this.orcamento_maximo = orcamento_maximo;
        this.carro_id = carro_id;
        this.pecas_ids = pecas_ids;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public double getOrcamento_maximo() {
        return orcamento_maximo;
    }

    public int getCarro_id() {
        return carro_id;
    }

    public List<Integer> getPecas_ids() {
        return pecas_ids;
    }
}
