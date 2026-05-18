package com.pokeapp.pokeapp.dto;

import com.pokeapp.pokeapp.model.Venta;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VentaDTO {

    public static class CrearRequest {
        @NotNull(message = "El id de la carta es obligatorio") private Long cartaId;
        @NotNull(message = "El tipo es obligatorio") private Venta.TipoVenta tipo;
        @DecimalMin(value = "0.01") private BigDecimal precio;

        public CrearRequest() {}
        public Long getCartaId() { return cartaId; }
        public void setCartaId(Long cartaId) { this.cartaId = cartaId; }
        public Venta.TipoVenta getTipo() { return tipo; }
        public void setTipo(Venta.TipoVenta tipo) { this.tipo = tipo; }
        public BigDecimal getPrecio() { return precio; }
        public void setPrecio(BigDecimal precio) { this.precio = precio; }
    }

    public static class ComprarRequest {
        @NotNull private Long compradorId;
        public ComprarRequest() {}
        public Long getCompradorId() { return compradorId; }
        public void setCompradorId(Long compradorId) { this.compradorId = compradorId; }
    }

    public static class Response {
        private Long id;
        private Long vendedorId;
        private String vendedorNombre;
        private Long compradorId;
        private String compradorNombre;
        private Long cartaId;
        private String cartaNombre;
        private BigDecimal precio;
        private Venta.TipoVenta tipo;
        private Venta.EstadoVenta estado;
        private LocalDateTime fecha;

        public Response() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getVendedorId() { return vendedorId; }
        public void setVendedorId(Long vendedorId) { this.vendedorId = vendedorId; }
        public String getVendedorNombre() { return vendedorNombre; }
        public void setVendedorNombre(String vendedorNombre) { this.vendedorNombre = vendedorNombre; }
        public Long getCompradorId() { return compradorId; }
        public void setCompradorId(Long compradorId) { this.compradorId = compradorId; }
        public String getCompradorNombre() { return compradorNombre; }
        public void setCompradorNombre(String compradorNombre) { this.compradorNombre = compradorNombre; }
        public Long getCartaId() { return cartaId; }
        public void setCartaId(Long cartaId) { this.cartaId = cartaId; }
        public String getCartaNombre() { return cartaNombre; }
        public void setCartaNombre(String cartaNombre) { this.cartaNombre = cartaNombre; }
        public BigDecimal getPrecio() { return precio; }
        public void setPrecio(BigDecimal precio) { this.precio = precio; }
        public Venta.TipoVenta getTipo() { return tipo; }
        public void setTipo(Venta.TipoVenta tipo) { this.tipo = tipo; }
        public Venta.EstadoVenta getEstado() { return estado; }
        public void setEstado(Venta.EstadoVenta estado) { this.estado = estado; }
        public LocalDateTime getFecha() { return fecha; }
        public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

        
    }

    public static class ValorarRequest {
        @NotNull private Long autorId;
        @NotNull @Min(1) @Max(5) private Integer puntuacion;
        @Size(max = 500) private String comentario;

        public ValorarRequest() {}
        public Long getAutorId() { return autorId; }
        public void setAutorId(Long autorId) { this.autorId = autorId; }
        public Integer getPuntuacion() { return puntuacion; }
        public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }
        public String getComentario() { return comentario; }
        public void setComentario(String comentario) { this.comentario = comentario; }
    }

    public static class DeseoRequest {
        @NotNull private Long usuarioId;
        @NotNull private Long cartaId;
        @DecimalMin("0.01") private BigDecimal precioObjetivo;
        private Boolean alertaActiva = true;

        public DeseoRequest() {}
        public Long getUsuarioId() { return usuarioId; }
        public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
        public Long getCartaId() { return cartaId; }
        public void setCartaId(Long cartaId) { this.cartaId = cartaId; }
        public BigDecimal getPrecioObjetivo() { return precioObjetivo; }
        public void setPrecioObjetivo(BigDecimal p) { this.precioObjetivo = p; }
        public Boolean getAlertaActiva() { return alertaActiva; }
        public void setAlertaActiva(Boolean alertaActiva) { this.alertaActiva = alertaActiva; }
    }
}
