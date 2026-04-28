package com.playmatch.app.entity;


public class Usuario {
        private int id;
        private String nombre;
        private String email;
        private String password;
        private String telefono;
        private double reputacion;
        private String avatarUrl;
        private String fechaRegistro;
        private String posicion;
        private int edad;


        public Usuario(){

        }

        //Constructor para pruebas
        public Usuario(int id ,String nombre , String email ,String password , String fechaRegistro){
            this.id=id;
            this.nombre=nombre;
            this.email=email;
            this.password=password;
            this.fechaRegistro=fechaRegistro;
        }

        //Getters y setters

        public int getId(){return id;}
        public void setId(int id){this.id=id;}

        public String getNombre(){return nombre;}
        public void setNombre(String nombre){this.nombre=nombre;}

        public String getEmail(){return email;}
        public void setEmail(String email){this.email=email;}

        public String getPassword(){return password;}
        public void setPassword(String password){this.password=password;}

        public String getTelefono(){return telefono;}
        public void setTelefono(String telefono){this.telefono=telefono;}

        public double getReputacion(){return reputacion;}
        public void setReputacion(double reputacion){this.reputacion=reputacion;}

        public String getAvatarUrl(){return avatarUrl;}
        public void setAvatarUrl(String avatarUrl){this.avatarUrl=avatarUrl;}

        public String getFechaRegistro(){return  fechaRegistro;}
        public void setFechaRegistro(String fechaRegistro){this.fechaRegistro=fechaRegistro;}

        public String getPosicion(){return  posicion;}
        public void setPosicion(String posicion){this.posicion=posicion;}

        public int getEdad(){return edad;}
        public void setEdad(int edad){this.edad=edad;}
}

