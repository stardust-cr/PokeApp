package com.pokeapp.pokeapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "VENTA")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "profileImage", "email"})
    private User vendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprador_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "profileImage", "email"})
    private User comprador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carta_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Carta carta;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVenta tipo;

    @Enumerated(EnumType.STRING)
    private EstadoVenta estado = EstadoVenta.PENDIENTE;

    @Column(name = "fecha")
    private LocalDateTime fecha = LocalDateTime.now();

    public Venta() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getVendedor() { return vendedor; }
    public void setVendedor(User vendedor) { this.vendedor = vendedor; }

    public User getComprador() { return comprador; }
    public void setComprador(User comprador) { this.comprador = comprador; }

    public Carta getCarta() { return carta; }
    public void setCarta(Carta carta) { this.carta = carta; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public TipoVenta getTipo() { return tipo; }
    public void setTipo(TipoVenta tipo) { this.tipo = tipo; }

    public EstadoVenta getEstado() { return estado; }
    public void setEstado(EstadoVenta estado) { this.estado = estado; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public enum TipoVenta { VENTA, TRUEQUE, SUBASTA }
    public enum EstadoVenta { PENDIENTE, COMPLETADA, CANCELADA }
}