package com.example.appmobile.models;

public class Carro {
    private int id;
    private String modelo;
    private String categoria;

    public Carro(int id, String modelo, String categoria) {
        this.id = id;
        this.modelo = modelo;
        this.categoria = categoria;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getModelo() {
        return modelo;
    }

    public String getCategoria() {
        return categoria;
    }

    @Override
    public String toString() {
        return modelo;
    }
}
