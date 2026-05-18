package com.pokeapp.pokeapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "gimnasios")
public class Gimnasios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "juego_id", nullable = false)
    private Juegos juego;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(name = "nombre_lider", length = 100)
    private String nombreLider;

    @Column(name = "tipo_principal")
    private String tipoPrincipal;

    @Column(length = 100)
    private String insignia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaGimnasio categoria = CategoriaGimnasio.GIMNASIO;

    public Gimnasios() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Juegos getJuego() { return juego; }
    public void setJuego(Juegos juego) { this.juego = juego; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getNombreLider() { return nombreLider; }
    public void setNombreLider(String nombreLider) { this.nombreLider = nombreLider; }

    public String getTipoPrincipal() { return tipoPrincipal; }
    public void setTipoPrincipal(String tipoPrincipal) { this.tipoPrincipal = tipoPrincipal; }

    public String getInsignia() { return insignia; }
    public void setInsignia(String insignia) { this.insignia = insignia; }

    public CategoriaGimnasio getCategoria() { return categoria; }
    public void setCategoria(CategoriaGimnasio categoria) { this.categoria = categoria; }

    public enum CategoriaGimnasio {
        GIMNASIO, ALTO_MANDO, CAMPEON, LIDER_ARENA
    }
}