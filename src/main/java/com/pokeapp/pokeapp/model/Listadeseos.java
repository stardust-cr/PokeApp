package com.pokeapp.pokeapp.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "lista_de_deseos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Listadeseos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "profileImage", "email"})
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carta_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Carta carta;

    @Column(name = "precio_objetivo", precision = 10, scale = 2)
    private BigDecimal precioObjetivo;

    @Column(name = "alerta_activa")
    private boolean alertaActiva = true;

    public Listadeseos() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }

    public Carta getCarta() { return carta; }
    public void setCarta(Carta carta) { this.carta = carta; }

    public BigDecimal getPrecioObjetivo() { return precioObjetivo; }
    public void setPrecioObjetivo(BigDecimal precioObjetivo) { this.precioObjetivo = precioObjetivo; }

    public boolean isAlertaActiva() { return alertaActiva; }
    public void setAlertaActiva(boolean alertaActiva) { this.alertaActiva = alertaActiva; }
}
