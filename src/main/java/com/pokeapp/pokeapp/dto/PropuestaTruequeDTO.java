package com.pokeapp.pokeapp.dto;

import com.pokeapp.pokeapp.model.PropuestaTrueque;
import java.time.LocalDateTime;

public class PropuestaTruequeDTO {

    public static class CrearRequest {
        private Long ventaId;
        private Long proponenteId;
        private Long cartaOfrecidaId;
        private String mensaje;

        public Long getVentaId() { return ventaId; }
        public void setVentaId(Long ventaId) { this.ventaId = ventaId; }
        public Long getProponenteId() { return proponenteId; }
        public void setProponenteId(Long proponenteId) { this.proponenteId = proponenteId; }
        public Long getCartaOfrecidaId() { return cartaOfrecidaId; }
        public void setCartaOfrecidaId(Long cartaOfrecidaId) { this.cartaOfrecidaId = cartaOfrecidaId; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }

    public static class Response {
        private Long id;
        private Long ventaId;
        private String cartaVentaNombre;
        private String cartaVentaImagenUrl;
        private Long vendedorId;
        private String vendedorNombre;
        private Long proponenteId;
        private String proponenteNombre;
        private Long cartaOfrecidaId;
        private String cartaOfrecidaNombre;
        private String cartaOfrecidaImagenUrl;
        private String mensaje;
        private PropuestaTrueque.EstadoPropuesta estado;
        private LocalDateTime fechaPropuesta;
        private LocalDateTime fechaRespuesta;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getVentaId() { return ventaId; }
        public void setVentaId(Long v) { this.ventaId = v; }
        public String getCartaVentaNombre() { return cartaVentaNombre; }
        public void setCartaVentaNombre(String s) { this.cartaVentaNombre = s; }
        public String getCartaVentaImagenUrl() { return cartaVentaImagenUrl; }
        public void setCartaVentaImagenUrl(String s) { this.cartaVentaImagenUrl = s; }
        public Long getVendedorId() { return vendedorId; }
        public void setVendedorId(Long v) { this.vendedorId = v; }
        public String getVendedorNombre() { return vendedorNombre; }
        public void setVendedorNombre(String s) { this.vendedorNombre = s; }
        public Long getProponenteId() { return proponenteId; }
        public void setProponenteId(Long v) { this.proponenteId = v; }
        public String getProponenteNombre() { return proponenteNombre; }
        public void setProponenteNombre(String s) { this.proponenteNombre = s; }
        public Long getCartaOfrecidaId() { return cartaOfrecidaId; }
        public void setCartaOfrecidaId(Long v) { this.cartaOfrecidaId = v; }
        public String getCartaOfrecidaNombre() { return cartaOfrecidaNombre; }
        public void setCartaOfrecidaNombre(String s) { this.cartaOfrecidaNombre = s; }
        public String getCartaOfrecidaImagenUrl() { return cartaOfrecidaImagenUrl; }
        public void setCartaOfrecidaImagenUrl(String s) { this.cartaOfrecidaImagenUrl = s; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String s) { this.mensaje = s; }
        public PropuestaTrueque.EstadoPropuesta getEstado() { return estado; }
        public void setEstado(PropuestaTrueque.EstadoPropuesta e) { this.estado = e; }
        public LocalDateTime getFechaPropuesta() { return fechaPropuesta; }
        public void setFechaPropuesta(LocalDateTime f) { this.fechaPropuesta = f; }
        public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
        public void setFechaRespuesta(LocalDateTime f) { this.fechaRespuesta = f; }
    }
}
