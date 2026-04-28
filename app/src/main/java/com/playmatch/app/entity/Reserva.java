package com.playmatch.app.entity;

public class Reserva {
    private int id;
    private int usuarioId;
    private int pistaId;
    private String fechaPartido;
    private String horaInicio;
    private String horaFin;
    private String estado; // 'pendiente', 'pagado', 'cancelado'

    public Reserva(){

    }

    //Consstructor para pruebas
    public Reserva(int id , int usuarioId , int pistaId , String fechaPartido ){
        this.id=id;
        this.usuarioId=usuarioId;
        this.pistaId=pistaId;
        this.fechaPartido=fechaPartido;
    }

    //Getters y setters

    public int getId(){return id;}
    public void setId(int id){this.id=id;}

    public int getUsuarioId(){return  usuarioId;}
    public void setUsuarioId(int usuarioId){this.usuarioId=usuarioId;}

    public int getPistaId(){return pistaId;}
    public void setPistaId(int pistaId){this.pistaId=pistaId;}

    public String getFechaPartido(){return fechaPartido;}
    public void setFechaPartido(String fechaPartido){this.fechaPartido=fechaPartido;}

    public String getHoraInicio(){return horaInicio;}
    public void setHoraInicio(String horaInicio){this.horaInicio=horaInicio;}

    public String getHoraFin(){return horaFin;}
    public void setHoraFin(String horaFin){this.horaFin=horaFin;}

    public String getEstado(){return estado;}
    public void setEstado(String estado){this.estado=estado;}

}
