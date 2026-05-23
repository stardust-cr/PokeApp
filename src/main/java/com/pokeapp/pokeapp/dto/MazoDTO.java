package com.pokeapp.pokeapp.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MazoDTO {

    public static class CrearRequest {

        @NotNull(message = "El id de usuario es obligatorio")
        private Long usuarioId;

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        private String nombre;

        @Size(max = 500)
        private String descripcion;

        private Boolean esPublico = false;

        public CrearRequest() {
        }

        public CrearRequest(Long usuarioId, String nombre, String descripcion, Boolean esPublico) {
            this.usuarioId = usuarioId;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.esPublico = esPublico != null ? esPublico : false;
        }

        public Long getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public Boolean getEsPublico() {
            return esPublico;
        }

        public void setEsPublico(Boolean esPublico) {
            this.esPublico = esPublico != null ? esPublico : false;
        }
    }

    public static class AgregarCartaRequest {

        @NotNull(message = "El id de carta es obligatorio")
        private Long cartaId;

        @Min(1)
        @Max(4)
        private Integer cantidad = 1;

        public AgregarCartaRequest() {
        }

        public AgregarCartaRequest(Long cartaId, Integer cantidad) {
            this.cartaId = cartaId;
            this.cantidad = cantidad != null ? cantidad : 1;
        }

        public Long getCartaId() {
            return cartaId;
        }

        public void setCartaId(Long cartaId) {
            this.cartaId = cartaId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad != null ? cantidad : 1;
        }
    }

    public static class Response {
        private Long id;
        private Long usuarioId;
        private String usuarioNombre;
        private String nombre;
        private String descripcion;
        private Boolean esPublico;
        private LocalDateTime fechaCreacion;
        private int totalCartas;
        private List<CartaEnMazoResponse> cartas = new ArrayList<>();

        public Response() {
        }

        public Response(Long id, Long usuarioId, String usuarioNombre, String nombre, String descripcion,
                        Boolean esPublico, LocalDateTime fechaCreacion, int totalCartas,
                        List<CartaEnMazoResponse> cartas) {
            this.id = id;
            this.usuarioId = usuarioId;
            this.usuarioNombre = usuarioNombre;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.esPublico = esPublico;
            this.fechaCreacion = fechaCreacion;
            this.totalCartas = totalCartas;
            this.cartas = cartas != null ? cartas : new ArrayList<>();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
        }

        public String getUsuarioNombre() {
            return usuarioNombre;
        }

        public void setUsuarioNombre(String usuarioNombre) {
            this.usuarioNombre = usuarioNombre;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public Boolean getEsPublico() {
            return esPublico;
        }

        public void setEsPublico(Boolean esPublico) {
            this.esPublico = esPublico;
        }

        public LocalDateTime getFechaCreacion() {
            return fechaCreacion;
        }

        public void setFechaCreacion(LocalDateTime fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
        }

        public int getTotalCartas() {
            return totalCartas;
        }

        public void setTotalCartas(int totalCartas) {
            this.totalCartas = totalCartas;
        }

        public List<CartaEnMazoResponse> getCartas() {
            return cartas;
        }

        public void setCartas(List<CartaEnMazoResponse> cartas) {
            this.cartas = cartas != null ? cartas : new ArrayList<>();
        }
    }

    public static class CartaEnMazoResponse {
        private Long cartaId;
        private String cartaNombre;
        private String imagenUrl;
        private Integer cantidad;

        public CartaEnMazoResponse() {
        }

        public CartaEnMazoResponse(Long cartaId, String cartaNombre, String imagenUrl, Integer cantidad) {
            this.cartaId = cartaId;
            this.cartaNombre = cartaNombre;
            this.imagenUrl = imagenUrl;
            this.cantidad = cantidad;
        }

        public Long getCartaId() {
            return cartaId;
        }

        public void setCartaId(Long cartaId) {
            this.cartaId = cartaId;
        }

        public String getCartaNombre() {
            return cartaNombre;
        }

        public void setCartaNombre(String cartaNombre) {
            this.cartaNombre = cartaNombre;
        }

        public String getImagenUrl() {
            return imagenUrl;
        }

        public void setImagenUrl(String imagenUrl) {
            this.imagenUrl = imagenUrl;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}
