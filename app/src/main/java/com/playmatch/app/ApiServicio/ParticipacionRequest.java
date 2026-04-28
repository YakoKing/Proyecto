package com.playmatch.app.ApiServicio;

public class ParticipacionRequest {
    private long usuarioId;
    private long partidoId;

    public ParticipacionRequest(long usuarioId, long partidoId) {
        this.usuarioId = usuarioId;
        this.partidoId = partidoId;
    }
}
