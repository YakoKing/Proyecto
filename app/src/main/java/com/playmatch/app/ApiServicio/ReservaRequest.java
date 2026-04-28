package com.playmatch.app.ApiServicio;

public class ReservaRequest {
    private long usuarioId;
    private long pistaId;
    private String fechaPartido;
    private String horaInicio;
    private String horaFin;

    public ReservaRequest(long usuarioId, long pistaId, String fechaPartido, String horaInicio, String horaFin) {
        this.usuarioId = usuarioId;
        this.pistaId = pistaId;
        this.fechaPartido = fechaPartido;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }
}