package com.pokeapp.pokeapp.model;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "cartas")
public class Carta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "set_codigo", length = 100)
    private String setCodigo;

    @Column(name = "numero_cartas", length = 20)
    private String numeroCartas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rareza rareza;

    @Column(length = 50)
    private String tipo;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Column(name = "precio_base", precision = 10, scale = 2)
    private BigDecimal precioBase;

    public Carta() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSetCodigo() { return setCodigo; }
    public void setSetCodigo(String setCodigo) { this.setCodigo = setCodigo; }

    public String getNumeroCartas() { return numeroCartas; }
    public void setNumeroCartas(String numeroCartas) { this.numeroCartas = numeroCartas; }

    public Rareza getRareza() { return rareza; }
    public void setRareza(Rareza rareza) { this.rareza = rareza; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }

    public enum Rareza {
        COMUN, POCO_COMUN, RARA, ULTRA_RARA, SECRETA, RAINBOW
    }
}
