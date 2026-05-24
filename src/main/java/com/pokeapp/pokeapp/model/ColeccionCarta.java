package com.pokeapp.pokeapp.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "coleccion_cartas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"coleccion_id", "carta_id"}))
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class ColeccionCarta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coleccion_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","usuario"})
    private Coleccion coleccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carta_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Carta carta;

    @Column(nullable = false)
    private Integer cantidad = 1;

    public ColeccionCarta() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Coleccion getColeccion() { return coleccion; }
    public void setColeccion(Coleccion coleccion) { this.coleccion = coleccion; }
    public Carta getCarta() { return carta; }
    public void setCarta(Carta carta) { this.carta = carta; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
