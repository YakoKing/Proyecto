package com.playmatch.app.entity;

public class Partido {
    private int id;
    private Integer reservaId;
    private String titulo;
    private int jugadoresMax;
    private String estado;  //confirmado y lista_espera
    private boolean esPublica;

    //Campos del endpoint del php
    private String organizador;
    private String pista;

    public Partido(){

    }

    //Constructor para pruebas
    public Partido(int id, String titulo, int jugadoresMax, String estado) {
    this.id = id;
    this.titulo = titulo;
    this.jugadoresMax = jugadoresMax;
    this.estado = estado;
    }

    //Getters y Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getReservaId() { return reservaId; }
    public void setReservaId(Integer reservaId) { this.reservaId = reservaId; }


    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getJugadoresMax() { return jugadoresMax; }
    public void setJugadoresMax(int jugadoresMax) { this.jugadoresMax = jugadoresMax; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean isEsPublica() { return esPublica; }
    public void setEsPublica(boolean esPublica) { this.esPublica = esPublica; }

    public String getOrganizador() { return organizador; }
    public void setOrganizador(String organizador) { this.organizador = organizador; }

    public String getPista() { return pista; }
    public void setPista(String pista) { this.pista = pista; }


}
