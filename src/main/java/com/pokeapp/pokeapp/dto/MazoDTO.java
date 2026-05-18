package com.pokeapp.pokeapp.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MazoDTO {
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CrearRequest {
 
        @NotNull(message = "El id de usuario es obligatorio")
        private Long usuarioId;
 
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        private String nombre;
 
        @Size(max = 500)
        private String descripcion;
 
        @Builder.Default
        private Boolean esPublico = false;
    }
 
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AgregarCartaRequest {
 
        @NotNull(message = "El id de carta es obligatorio")
        private Long cartaId;
 
        @Min(1) @Max(4)
        @Builder.Default
        private Integer cantidad = 1;
    }
 
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private Long usuarioId;
        private String usuarioNombre;
        private String nombre;
        private String descripcion;
        private Boolean esPublico;
        private LocalDateTime fechaCreacion;
        private int totalCartas;
        private List<CartaEnMazoResponse> cartas;
    }
 
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CartaEnMazoResponse {
        private Long cartaId;
        private String cartaNombre;
        private String imagenUrl;
        private Integer cantidad;
    }
}
