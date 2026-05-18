package com.pokeapp.pokeapp.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "MAZO_CARTAS")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Mazos_Cartas {

    @EmbeddedId
    private Mazos_CartasId id = new Mazos_CartasId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("mazoId")
    @JoinColumn(name = "mazo_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "cartas", "usuario"})
    private Mazos mazo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartaId")
    @JoinColumn(name = "carta_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Carta carta;

    @Column
    private Integer cantidad = 1;

    public Mazos_Cartas() {}

    public Mazos_CartasId getId() { return id; }
    public void setId(Mazos_CartasId id) { this.id = id; }

    public Mazos getMazo() { return mazo; }
    public void setMazo(Mazos mazo) { this.mazo = mazo; }

    public Carta getCarta() { return carta; }
    public void setCarta(Carta carta) { this.carta = carta; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}