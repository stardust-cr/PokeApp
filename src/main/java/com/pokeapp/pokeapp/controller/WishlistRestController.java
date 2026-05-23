package com.pokeapp.pokeapp.controller;



import com.pokeapp.pokeapp.dto.VentaDTO;
import com.pokeapp.pokeapp.model.Listadeseos;
import com.pokeapp.pokeapp.service.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistRestController {

    private final VentaService ventaService;

    public WishlistRestController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<Listadeseos>> getWishlist(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ventaService.obtenerDeseosUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<?> añadir(@RequestBody VentaDTO.DeseoRequest request) {
        try {
            Listadeseos item = ventaService.agregarADeseos(request.getUsuarioId(), request);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> eliminar(@PathVariable Long itemId) {
        try {
            ventaService.eliminarDeDeseos(null, itemId);
            return ResponseEntity.ok(Map.of("mensaje", "Eliminado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}