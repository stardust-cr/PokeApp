package com.pokeapp.pokeapp.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
@Entity
@Table(name = "juegos")
public class Juegos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false)
    private Integer generacion;

    @Column(length = 50)
    private String region;

    @OneToMany(mappedBy = "juego", fetch = FetchType.LAZY)
    private List<Gimnasios> gimnasios;

    // Getters y Setters
    public Juegos() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getGeneracion() { return generacion; }
    public void setGeneracion(Integer generacion) { this.generacion = generacion; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}
