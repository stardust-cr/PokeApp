package com.pokeapp.pokeapp.dto;

import com.pokeapp.pokeapp.model.Carta;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CartaDTO {

    public static class Request {
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        private String nombre;
        @Size(max = 20) private String setCodigo;
        @Size(max = 20) private String numeroCartas;
        @NotNull(message = "La rareza es obligatoria") private Carta.Rareza rareza;
        @Size(max = 50) private String tipo;
        @Size(max = 255) private String imagenUrl;
        @DecimalMin(value = "0.00") private BigDecimal precioBase;

        public Request() {}
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getSetCodigo() { return setCodigo; }
        public void setSetCodigo(String setCodigo) { this.setCodigo = setCodigo; }
        public String getNumeroCartas() { return numeroCartas; }
        public void setNumeroCartas(String numeroCartas) { this.numeroCartas = numeroCartas; }
        public Carta.Rareza getRareza() { return rareza; }
        public void setRareza(Carta.Rareza rareza) { this.rareza = rareza; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public String getImagenUrl() { return imagenUrl; }
        public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
        public BigDecimal getPrecioBase() { return precioBase; }
        public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }
    }

    public static class Response {
        private Long id;
        private String nombre;
        private String setCodigo;
        private String numeroCartas;
        private Carta.Rareza rareza;
        private String tipo;
        private String imagenUrl;
        private BigDecimal precioBase;

        public Response() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getSetCodigo() { return setCodigo; }
        public void setSetCodigo(String setCodigo) { this.setCodigo = setCodigo; }
        public String getNumeroCartas() { return numeroCartas; }
        public void setNumeroCartas(String n) { this.numeroCartas = n; }
        public Carta.Rareza getRareza() { return rareza; }
        public void setRareza(Carta.Rareza rareza) { this.rareza = rareza; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public String getImagenUrl() { return imagenUrl; }
        public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
        public BigDecimal getPrecioBase() { return precioBase; }
        public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }
    }
}
