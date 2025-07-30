package com.meztli.alufx.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "cortes")
public class Corte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "aplicaAncho")
    private boolean aplicaAncho;

    @Column(name = "aplicaAlto")
    private boolean aplicaAlto;

    public Corte() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isAplicaAncho() {
        return aplicaAncho;
    }

    public void setAplicaAncho(boolean aplicaAncho) {
        this.aplicaAncho = aplicaAncho;
    }

    public boolean isAplicaAlto() {
        return aplicaAlto;
    }

    public void setAplicaAlto(boolean aplicaAlto) {
        this.aplicaAlto = aplicaAlto;
    }
}
