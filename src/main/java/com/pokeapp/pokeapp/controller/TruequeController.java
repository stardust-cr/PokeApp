package com.pokeapp.pokeapp.controller;

import com.pokeapp.pokeapp.dto.PropuestaTruequeDTO;
import com.pokeapp.pokeapp.service.TruequeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trueques")
public class TruequeController {

    private final TruequeService truequeService;

    public TruequeController(TruequeService truequeService) {
        this.truequeService = truequeService;
    }

    /** Proponer un trueque */
    @PostMapping("/proponer")
    public ResponseEntity<?> proponer(@RequestBody PropuestaTruequeDTO.CrearRequest req) {
        try {
            return ResponseEntity.ok(truequeService.proponerTrueque(req));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    /** Aceptar una propuesta */
    @PostMapping("/{propuestaId}/aceptar")
    public ResponseEntity<?> aceptar(@PathVariable Long propuestaId, @RequestParam Long vendedorId) {
        try {
            return ResponseEntity.ok(truequeService.responderPropuesta(propuestaId, vendedorId, true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    /** Rechazar una propuesta */
    @PostMapping("/{propuestaId}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable Long propuestaId, @RequestParam Long vendedorId) {
        try {
            return ResponseEntity.ok(truequeService.responderPropuesta(propuestaId, vendedorId, false));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    /** Propuestas sobre una publicación concreta */
    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<PropuestaTruequeDTO.Response>> porVenta(@PathVariable Long ventaId) {
        return ResponseEntity.ok(truequeService.propuestasDeVenta(ventaId));
    }

    /** Propuestas recibidas (pendientes) para el vendedor — para el badge */
    @GetMapping("/recibidas/{vendedorId}")
    public ResponseEntity<List<PropuestaTruequeDTO.Response>> recibidas(@PathVariable Long vendedorId) {
        return ResponseEntity.ok(truequeService.propuestasRecibidas(vendedorId));
    }

    /** Propuestas enviadas por el usuario */
    @GetMapping("/enviadas/{proponenteId}")
    public ResponseEntity<List<PropuestaTruequeDTO.Response>> enviadas(@PathVariable Long proponenteId) {
        return ResponseEntity.ok(truequeService.propuestasEnviadas(proponenteId));
    }

    /** Conteo de propuestas pendientes para el badge en la barra */
    @GetMapping("/pendientes/count/{vendedorId}")
    public ResponseEntity<Map<String, Long>> contarPendientes(@PathVariable Long vendedorId) {
        long total = truequeService.contarPropuestasPendientes(vendedorId);
        return ResponseEntity.ok(Map.of("total", total));
    }
}

