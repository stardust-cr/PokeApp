package com.pokeapp.pokeapp.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "propuesta_trueque")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PropuestaTrueque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** La publicación de trueque a la que se propone */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Venta venta;

    /** El usuario que hace la propuesta (el "comprador") */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proponente_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "profileImage", "email"})
    private User proponente;

    /** La carta que ofrece el proponente a cambio */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carta_ofrecida_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Carta cartaOfrecida;

    @Column(length = 300)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPropuesta estado = EstadoPropuesta.PENDIENTE;

    @Column(name = "fecha_propuesta")
    private LocalDateTime fechaPropuesta = LocalDateTime.now();

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    // ── Getters / Setters ──────────────────────────────────────────────────

    public Long getId() { return id; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public User getProponente() { return proponente; }
    public void setProponente(User proponente) { this.proponente = proponente; }

    public Carta getCartaOfrecida() { return cartaOfrecida; }
    public void setCartaOfrecida(Carta cartaOfrecida) { this.cartaOfrecida = cartaOfrecida; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public EstadoPropuesta getEstado() { return estado; }
    public void setEstado(EstadoPropuesta estado) { this.estado = estado; }

    public LocalDateTime getFechaPropuesta() { return fechaPropuesta; }
    public void setFechaPropuesta(LocalDateTime f) { this.fechaPropuesta = f; }

    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime f) { this.fechaRespuesta = f; }

    public enum EstadoPropuesta { PENDIENTE, ACEPTADA, RECHAZADA }
}

