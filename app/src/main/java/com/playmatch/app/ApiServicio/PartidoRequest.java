package com.playmatch.app.ApiServicio;

public class PartidoRequest {
    private long reservaId;
    private String titulo;
    private int jugadoresMax;
    private boolean esPublica;

    public PartidoRequest(long reservaId, String titulo, int jugadoresMax, boolean esPublica) {
        this.reservaId = reservaId;
        this.titulo = titulo;
        this.jugadoresMax = jugadoresMax;
        this.esPublica = esPublica;
    }
}