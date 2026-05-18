package com.pokeapp.pokeapp.model;
 
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
 
@Entity
@Table(name = "POKEDEX")
public class Pokedex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    private Integer numero;
    private String nombre;
    private String tipo1;
    private String tipo2;
    private String descripcion;
 
    @Column(name = "imagen_url")
    private String imagenUrl;
 
    @OneToMany(mappedBy = "especie")
    private List<Pokemon> ejemplaresCapturados;
 
    public Pokedex() {}
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
 
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
 
    public String getTipo1() { return tipo1; }
    public void setTipo1(String tipo1) { this.tipo1 = tipo1; }
 
    public String getTipo2() { return tipo2; }
    public void setTipo2(String tipo2) { this.tipo2 = tipo2; }
 
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
 
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
 
    public List<Pokemon> getEjemplaresCapturados() { return ejemplaresCapturados; }
    public void setEjemplaresCapturados(List<Pokemon> ejemplaresCapturados) {
        this.ejemplaresCapturados = ejemplaresCapturados;
    }
}
