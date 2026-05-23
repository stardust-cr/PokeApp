package com.pokeapp.pokeapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pokeapp.pokeapp.model.Coleccion;
import com.pokeapp.pokeapp.service.ColeccionMazoService;

@RestController
@RequestMapping("/api/colecciones")
public class ColeccionRestController {

    private final ColeccionMazoService service;

    public ColeccionRestController(ColeccionMazoService service) {
        this.service = service;
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Coleccion>> coleccionesDeUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(service.obtenerColeccionesUsuario(userId));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> crearColeccion(@PathVariable Long userId, @RequestParam String nombre) {
        try {
            return ResponseEntity.ok(service.crearColeccion(userId, nombre));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{coleccionId}")
    public ResponseEntity<?> eliminarColeccion(@PathVariable Long coleccionId) {
        try {
            service.eliminarColeccion(coleccionId);
            return ResponseEntity.ok(Map.of("mensaje", "Colección eliminada"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}
