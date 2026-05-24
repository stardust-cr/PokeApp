package com.pokeapp.pokeapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pokeapp.pokeapp.model.Carta;
import com.pokeapp.pokeapp.model.Coleccion;
import com.pokeapp.pokeapp.model.ColeccionCarta;
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

    // ── CARTAS DE UNA COLECCIÓN ──

    @GetMapping("/{coleccionId}/cartas")
    public ResponseEntity<List<ColeccionCarta>> cartasDeColeccion(@PathVariable Long coleccionId) {
        return ResponseEntity.ok(service.obtenerCartasDeColeccion(coleccionId));
    }

    @PostMapping("/{coleccionId}/cartas")
    public ResponseEntity<?> añadirCarta(@PathVariable Long coleccionId,
                                          @RequestParam Long cartaId,
                                          @RequestParam(defaultValue = "1") Integer cantidad) {
        try {
            return ResponseEntity.ok(service.agregarCartaAColeccion(coleccionId, cartaId, cantidad));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{coleccionId}/cartas/{cartaId}")
    public ResponseEntity<?> eliminarCarta(@PathVariable Long coleccionId,
                                            @PathVariable Long cartaId) {
        try {
            service.eliminarCartaDeColeccion(coleccionId, cartaId);
            return ResponseEntity.ok(Map.of("mensaje", "Carta eliminada de la colección"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // Todas las cartas del usuario en cualquier colección (para el formulario de venta)
    @GetMapping("/usuario/{userId}/cartas")
    public ResponseEntity<List<Carta>> cartasDeUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(service.obtenerCartasDeUsuario(userId));
    }
}