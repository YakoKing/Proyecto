package com.playmatch.app.entity;

public class Pista {
    private int id;
    private String nombre;
    private String foto;
    private double precioHora;
    private String ubicacion;
    private int capacidadMax;

    public Pista(){

    }

    //Constructor para pruebas

    public Pista(int id , String nombre , double precioHora , String ubicacion ){
        this.id=id;
        this.nombre=nombre;
        this.precioHora=precioHora;
        this.ubicacion=ubicacion;
    }

    //GETTERS Y SETTERS

    public int getId(){return id;}
    public void setId(int id){this.id=id;}

    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}

    public String getFoto(){return foto;}
    public void setFoto(String foto){this.foto=foto;}

    public double getPrecioHora(){return precioHora;}
    public void setPrecioHora(double precioHora){this.precioHora=precioHora;}

    public String getUbicacion(){return ubicacion;}
    public void setUbicacion(String ubicacion){this.ubicacion=ubicacion;}

    public int getCapacidadMax(){return capacidadMax;}
    public void setCapacidadMax(int capacidadMax){this.capacidadMax=capacidadMax;}


}
