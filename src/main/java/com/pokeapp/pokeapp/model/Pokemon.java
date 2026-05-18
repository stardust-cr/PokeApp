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
@Table(name = "pokemon")
public class Pokemon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private Run run;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pokemon_id", nullable = false)
    private Pokedex especie;

    // La BD permite NULL en apodo → nullable=true
    @Column(length = 50)
    private String apodo;

    @Column(name = "nivel_captura")
    private Integer nivel_captura;

    @Column(name = "nivel_muerte")
    private Integer nivel_muerte;

    @Column(name = "ruta_captura", length = 100)
    private String ruta_captura;

    @Column(name = "causa_muerte", length = 255)
    private String causa_muerte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPokemon estado = EstadoPokemon.VIVO;

    public Pokemon() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Run getRun() { return run; }
    public void setRun(Run run) { this.run = run; }

    public Pokedex getEspecie() { return especie; }
    public void setEspecie(Pokedex especie) { this.especie = especie; }

    public String getApodo() { return apodo; }
    public void setApodo(String apodo) { this.apodo = apodo; }

    public Integer getNivel_captura() { return nivel_captura; }
    public void setNivel_captura(Integer nivel_captura) { this.nivel_captura = nivel_captura; }

    public Integer getNivel_muerte() { return nivel_muerte; }
    public void setNivel_muerte(Integer nivel_muerte) { this.nivel_muerte = nivel_muerte; }

    public String getRuta_captura() { return ruta_captura; }
    public void setRuta_captura(String ruta_captura) { this.ruta_captura = ruta_captura; }

    public String getCausa_muerte() { return causa_muerte; }
    public void setCausa_muerte(String causa_muerte) { this.causa_muerte = causa_muerte; }

    public EstadoPokemon getEstado() { return estado; }
    public void setEstado(EstadoPokemon estado) { this.estado = estado; }
}