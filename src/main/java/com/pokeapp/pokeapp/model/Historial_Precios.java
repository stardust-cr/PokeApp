package com.pokeapp.pokeapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "historial_precios")
public class Historial_Precios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carta_id", nullable = false)
    private Carta carta;

    @Column(name = "precio_anterior", precision = 10, scale = 2)
    private BigDecimal precioAnterior;

    @Column(name = "precio_nuevo", precision = 10, scale = 2)
    private BigDecimal precioNuevo;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @Column(length = 50)
    private String fuente = "TCGPlayer";

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    public Historial_Precios() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Carta getCarta() { return carta; }
    public void setCarta(Carta carta) { this.carta = carta; }

    public BigDecimal getPrecioAnterior() { return precioAnterior; }
    public void setPrecioAnterior(BigDecimal precioAnterior) { this.precioAnterior = precioAnterior; }

    public BigDecimal getPrecioNuevo() { return precioNuevo; }
    public void setPrecioNuevo(BigDecimal precioNuevo) { this.precioNuevo = precioNuevo; }

    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }

    public String getFuente() { return fuente; }
    public void setFuente(String fuente) { this.fuente = fuente; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
}


