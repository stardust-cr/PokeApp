package com.pokeapp.pokeapp.controller;

import com.pokeapp.pokeapp.dto.CartaDTO;
import com.pokeapp.pokeapp.service.CartaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cartas")
public class CartaRestController {

    private final CartaService cartaService;

    public CartaRestController(CartaService cartaService) {
        this.cartaService = cartaService;
    }

    /** GET /api/cartas/{id} — obtener carta por ID del servidor */
    @GetMapping("/{id}")
    public ResponseEntity<CartaDTO.Response> obtenerPorId(@PathVariable Long id) {
        try {
            CartaDTO.Response carta = cartaService.obtenerPorId(id);
            return ResponseEntity.ok(carta);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/cartas/buscar?nombre=Charizard — buscar cartas por nombre en el servidor */
    @GetMapping("/buscar")
    public ResponseEntity<List<CartaDTO.Response>> buscarPorNombre(@RequestParam String nombre) {
        List<CartaDTO.Response> cartas = cartaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(cartas);
    }

    /** GET /api/cartas — todas las cartas del servidor */
    @GetMapping
    public ResponseEntity<List<CartaDTO.Response>> todas() {
        return ResponseEntity.ok(cartaService.todas());
    }

    /**
     * POST /api/cartas/importar
     * Recibe datos de una carta de la API de PokémonTCG,
     * la guarda en la BD si no existe (o la reutiliza si ya está),
     * y devuelve la carta con su ID de servidor.
     * Body JSON: { "nombre", "setCodigo", "rareza", "imagenUrl" }
     */
    @PostMapping("/importar")
    public ResponseEntity<CartaDTO.Response> importar(@RequestBody Map<String, String> body) {
        try {
            String nombre    = body.get("nombre");
            String setCodigo = body.getOrDefault("setCodigo", "");
            String rareza    = body.getOrDefault("rareza", "");
            String imagenUrl = body.getOrDefault("imagenUrl", "");

            if (nombre == null || nombre.isBlank()) {
                return ResponseEntity.badRequest().build();
            }

            CartaDTO.Response carta = cartaService.importarDesdeApiTcg(nombre, setCodigo, rareza, imagenUrl);
            return ResponseEntity.ok(carta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
