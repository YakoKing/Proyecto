package com.playmatch.app.ApiServicio;

public class PartidoRequest {
    private long reservaId;
    private String titulo;
    private int jugadoresMax;
    private boolean esPublica;
    private String estado;
    private String nivel;

    public PartidoRequest(long reservaId, String titulo, int jugadoresMax, boolean esPublica, String estado, String nivel) {
        this.reservaId = reservaId;
        this.titulo = titulo;
        this.jugadoresMax = jugadoresMax;
        this.esPublica = esPublica;
        this.estado = estado;
        this.nivel = nivel;
    }

    // Getters y Setters
    public long getReservaId() { return reservaId; }
    public void setReservaId(long reservaId) { this.reservaId = reservaId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getJugadoresMax() { return jugadoresMax; }
    public void setJugadoresMax(int jugadoresMax) { this.jugadoresMax = jugadoresMax; }

    public boolean isEsPublica() { return esPublica; }
    public void setEsPublica(boolean esPublica) { this.esPublica = esPublica; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
}
