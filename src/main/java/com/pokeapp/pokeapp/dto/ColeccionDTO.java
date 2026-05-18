package com.pokeapp.pokeapp.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ColeccionDTO {
     @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
 
        @NotNull(message = "El id de usuario es obligatorio")
        private Long usuarioId;
 
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        private String nombre;
    }
 
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private Long usuarioId;
        private String usuarioNombre;
        private String nombre;
        private LocalDateTime creadaEn;
    }
}
