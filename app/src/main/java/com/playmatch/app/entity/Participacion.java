package com.playmatch.app.entity;

public class Participacion {
    private int id;
    private int usuarioId;
    private int partidoId;
    private String estado; // 'confirmado', 'lista_espera'

    //Campo de php
    private String nombreUsuario;

    public Participacion(){

    }

    //Constrictor para pruebas
    public Participacion(int id , int usuarioId , int partidoId , String estado){
        this.id=id;
        this.usuarioId=usuarioId;
        this.partidoId=partidoId;
        this.estado=estado;
    }

    //Getters y setters

    public int getId(){return id;}
    public void setId(int id){this.id=id;}

    public int getUsuarioId(){return usuarioId;}
    public void setUsuarioId(int usuarioId){this.usuarioId=usuarioId;}

    public int getPartidoId(){return partidoId;}
    public void setPartidoId(int partidoId){this.partidoId=partidoId;}

    public String getEstado(){return estado;}
    public void setEstado(String estado){this.estado=estado;}

    public String getNombreUsuario(){return nombreUsuario;}
    public void setNombreUsuario(String nombreUsuario){this.nombreUsuario=nombreUsuario;}



}
