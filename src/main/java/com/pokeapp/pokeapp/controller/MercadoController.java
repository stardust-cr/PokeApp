package com.pokeapp.pokeapp.controller;

import com.pokeapp.pokeapp.dto.VentaDTO;
import com.pokeapp.pokeapp.model.Listadeseos;
import com.pokeapp.pokeapp.model.User;
import com.pokeapp.pokeapp.model.Valoracion;
import com.pokeapp.pokeapp.model.Venta;
import com.pokeapp.pokeapp.repository.UserRepository;
import com.pokeapp.pokeapp.service.VentaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mercado")
public class MercadoController {

    private final VentaService ventaService;
    private final UserRepository userRepository; // ✅ AÑADIDO

    public MercadoController(VentaService ventaService,
                              UserRepository userRepository) { // ✅ AÑADIDO
        this.ventaService = ventaService;
        this.userRepository = userRepository;
    }

    @PostMapping("/publicar")
    public ResponseEntity<VentaDTO.Response> publicarVenta(@RequestParam Long vendedorId, @RequestBody VentaDTO.CrearRequest request) {
        return ResponseEntity.ok(ventaService.publicar(vendedorId, request));
    }

    @PostMapping("/{ventaId}/comprar")
    public ResponseEntity<VentaDTO.Response> comprarVenta(@PathVariable Long ventaId, @RequestBody VentaDTO.ComprarRequest request) {
        return ResponseEntity.ok(ventaService.comprar(ventaId, request));
    }

    @PostMapping("/{ventaId}/cancelar")
    public ResponseEntity<VentaDTO.Response> cancelarVenta(@PathVariable Long ventaId, @RequestParam Long vendedorId) {
        return ResponseEntity.ok(ventaService.cancelar(ventaId, vendedorId));
    }

    @GetMapping
    public ResponseEntity<List<VentaDTO.Response>> obtenerMercado() {
        return ResponseEntity.ok(ventaService.obtenerMercado());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<VentaDTO.Response>> obtenerPorTipo(@PathVariable Venta.TipoVenta tipo) {
        return ResponseEntity.ok(ventaService.obtenerPorTipo(tipo));
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<VentaDTO.Response>> obtenerVentasUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(ventaService.obtenerVentasUsuario(userId));
    }

    @GetMapping("/compras/{userId}")
    public ResponseEntity<List<VentaDTO.Response>> obtenerComprasUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(ventaService.obtenerComprasUsuario(userId));
    }

    @GetMapping("/carta/{cartaId}/ofertas")
    public ResponseEntity<List<VentaDTO.Response>> obtenerOfertasDeCarta(@PathVariable Long cartaId) {
        return ResponseEntity.ok(ventaService.obtenerOfertasDeCarta(cartaId));
    }

    @PostMapping("/valorar")
    public ResponseEntity<Valoracion> valorarVenta(@RequestParam Long ventaId, @RequestBody VentaDTO.ValorarRequest request) {
        return ResponseEntity.ok(ventaService.valorar(ventaId, request));
    }

    @GetMapping("/valoraciones/vendedor/{vendedorId}")
    public ResponseEntity<List<Valoracion>> obtenerValoracionesDeVendedor(@PathVariable Long vendedorId) {
        return ResponseEntity.ok(ventaService.obtenerValoracionesDeVendedor(vendedorId));
    }

    @PostMapping("/deseos")
    public ResponseEntity<Listadeseos> agregarADeseos(@RequestBody VentaDTO.DeseoRequest request) {
        return ResponseEntity.ok(ventaService.agregarADeseos(request.getUsuarioId(), request));
    }

    @DeleteMapping("/deseos")
    public ResponseEntity<Void> eliminarDeDeseos(@RequestParam Long usuarioId, @RequestParam Long itemId) {
        ventaService.eliminarDeDeseosPorId(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deseos/{usuarioId}")
    public ResponseEntity<List<Listadeseos>> obtenerDeseosUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ventaService.obtenerDeseosUsuario(usuarioId));
    }

    @GetMapping("/activas")
    public ResponseEntity<List<VentaDTO.Response>> obtenerVentasActivas() {
        return ResponseEntity.ok(ventaService.obtenerMercado());
    }

    @GetMapping("/mercado/propuestas-recibidas")
    public String propuestasRecibidas(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/web/login";
        String username = (String) session.getAttribute("username");
        User usuario = userRepository.findByUsername(username).orElse(null); // ✅ CORREGIDO
        model.addAttribute("username", username);
        model.addAttribute("usuarioId", usuario != null ? usuario.getId() : null);
        return "propuestas-recibidas";
    }
}