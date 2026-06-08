package com.example.appmobile.models;

import java.util.List;

public class Projeto {
    private int id;
    private String nome;
    private double orcamentoMaximo;
    private double custoTotalCalculado;
    private int carroId;
    private String criado_em;
    private List<Peca> pecas;

    public Projeto(String nome, double orcamentoMaximo, int carroId, List<Peca> pecas) {
        this.nome = nome;
        this.orcamentoMaximo = orcamentoMaximo;
        this.carroId = carroId;
        this.pecas = pecas;
        this.custoTotalCalculado = pecas.stream().mapToDouble(Peca::getPreco).sum();
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getOrcamentoMaximo() {
        return orcamentoMaximo;
    }

    public double getCustoTotalCalculado() {
        return custoTotalCalculado;
    }

    public int getCarroId() {
        return carroId;
    }

    public List<Peca> getPecas() {
        return pecas;
    }

    public String getCriado_em() {
        return criado_em;
    }

    @Override
    public String toString() {
        return nome;
    }
}
