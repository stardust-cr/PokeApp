package com.pokeapp.pokeapp.controller;

import com.pokeapp.pokeapp.dto.MazoDTO;
import com.pokeapp.pokeapp.model.Mazos;
import com.pokeapp.pokeapp.model.Mazos_Cartas;
import com.pokeapp.pokeapp.service.ColeccionMazoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mazos")
public class MazoRestController {

    private final ColeccionMazoService service;

    public MazoRestController(ColeccionMazoService service) {
        this.service = service;
    }

    // ── GET /api/mazos/usuario/{userId} ──────────────────────────────────────
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<MazoDTO.Response>> mazosDeUsuario(@PathVariable Long userId) {
        List<Mazos> mazos = service.obtenerMazosUsuario(userId);
        return ResponseEntity.ok(mazos.stream().map(this::toResponse).toList());
    }

    // ── GET /api/mazos/publicos ───────────────────────────────────────────────
    @GetMapping("/publicos")
    public ResponseEntity<List<MazoDTO.Response>> mazosPublicos() {
        List<Mazos> mazos = service.obtenerMazosPublicos();
        return ResponseEntity.ok(mazos.stream().map(this::toResponse).toList());
    }

    // ── POST /api/mazos/{userId}?nombre=...&descripcion=...&esPublico=false ──
    @PostMapping("/{userId}")
    public ResponseEntity<?> crearMazo(
            @PathVariable Long userId,
            @RequestParam String nombre,
            @RequestParam(required = false, defaultValue = "") String descripcion,
            @RequestParam(required = false, defaultValue = "false") boolean esPublico) {
        try {
            Mazos mazo = service.crearMazo(userId, nombre, descripcion, esPublico);
            return ResponseEntity.ok(toResponse(mazo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // ── DELETE /api/mazos/{mazoId} ────────────────────────────────────────────
    @DeleteMapping("/{mazoId}")
    public ResponseEntity<?> eliminarMazo(@PathVariable Long mazoId) {
        try {
            service.eliminarMazo(mazoId);
            return ResponseEntity.ok(Map.of("mensaje", "Mazo eliminado"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // ── GET /api/mazos/{mazoId}/cartas ───────────────────────────────────────
    @GetMapping("/{mazoId}/cartas")
    public ResponseEntity<List<MazoDTO.CartaEnMazoResponse>> cartasDeMazo(@PathVariable Long mazoId) {
        List<Mazos_Cartas> cartas = service.obtenerCartasDeMazo(mazoId);
        List<MazoDTO.CartaEnMazoResponse> resp = cartas.stream().map(mc -> {
            MazoDTO.CartaEnMazoResponse item = new MazoDTO.CartaEnMazoResponse();
            item.setCartaId(mc.getCarta().getId());
            item.setCartaNombre(mc.getCarta().getNombre());
            item.setImagenUrl(mc.getCarta().getImagenUrl());
            item.setCantidad(mc.getCantidad());
            return item;
        }).toList();
        return ResponseEntity.ok(resp);
    }

    // ── POST /api/mazos/{mazoId}/cartas/{cartaId}?cantidad=1 ─────────────────
    @PostMapping("/{mazoId}/cartas/{cartaId}")
    public ResponseEntity<?> agregarCarta(
            @PathVariable Long mazoId,
            @PathVariable Long cartaId,
            @RequestParam(required = false, defaultValue = "1") Integer cantidad) {
        try {
            Mazos_Cartas mc = service.agregarCartaAMazo(mazoId, cartaId, cantidad);
            return ResponseEntity.ok(Map.of(
                "cartaId", mc.getCarta().getId(),
                "cartaNombre", mc.getCarta().getNombre(),
                "cantidad", mc.getCantidad()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // ── DELETE /api/mazos/{mazoId}/cartas/{cartaId} ──────────────────────────
    @DeleteMapping("/{mazoId}/cartas/{cartaId}")
    public ResponseEntity<?> eliminarCarta(
            @PathVariable Long mazoId,
            @PathVariable Long cartaId) {
        try {
            service.eliminarCartaDeMazo(mazoId, cartaId);
            return ResponseEntity.ok(Map.of("mensaje", "Carta eliminada del mazo"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // ── PATCH /api/mazos/{mazoId}/visibilidad?esPublico=true ─────────────────
    @PatchMapping("/{mazoId}/visibilidad")
    public ResponseEntity<?> cambiarVisibilidad(
            @PathVariable Long mazoId,
            @RequestParam boolean esPublico) {
        try {
            service.cambiarVisibilidad(mazoId, esPublico);
            return ResponseEntity.ok(Map.of("mensaje", "Visibilidad actualizada"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    // ── Helper: Mazos → MazoDTO.Response ─────────────────────────────────────
    private MazoDTO.Response toResponse(Mazos m) {
        int numCartas = service.obtenerCartasDeMazo(m.getId()).size();
        MazoDTO.Response response = new MazoDTO.Response();
        response.setId(m.getId());
        response.setUsuarioId(m.getUsuario() != null ? m.getUsuario().getId() : null);
        response.setUsuarioNombre(m.getUsuario() != null ? m.getUsuario().getUsername() : null);
        response.setNombre(m.getNombre());
        response.setDescripcion(m.getDescripcion());
        response.setEsPublico(m.isEsPublico());
        response.setFechaCreacion(m.getFechaCreacion());
        response.setTotalCartas(numCartas);
        return response;
    }
}
