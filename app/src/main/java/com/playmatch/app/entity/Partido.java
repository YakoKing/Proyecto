package com.playmatch.app.entity;

import java.io.Serializable;

public class Partido implements Serializable {
    private int id;
    private Integer reservaId;
    private String titulo;
    private int jugadoresMax;
    private String estado;
    private boolean esPublica;

    private String organizador;
    private String pista;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private Reserva reserva;
    private Usuario usuario;
    private String nivel;

    public Partido() {
    }

    // Getters y Setters
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

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public int getJugadoresActuales() {
        return 1; // Mínimo el organizador
    }
}
