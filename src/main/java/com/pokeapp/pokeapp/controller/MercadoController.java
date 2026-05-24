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

    public MercadoController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping("/publicar")
    public ResponseEntity<VentaDTO.Response> publicarVenta(@RequestParam Long vendedorId, @RequestBody VentaDTO.CrearRequest request) {
        VentaDTO.Response response = ventaService.publicar(vendedorId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{ventaId}/comprar")
    public ResponseEntity<VentaDTO.Response> comprarVenta(@PathVariable Long ventaId, @RequestBody VentaDTO.ComprarRequest request) {
        VentaDTO.Response response = ventaService.comprar(ventaId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{ventaId}/cancelar")
    public ResponseEntity<VentaDTO.Response> cancelarVenta(@PathVariable Long ventaId, @RequestParam Long vendedorId) {
        VentaDTO.Response response = ventaService.cancelar(ventaId, vendedorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<VentaDTO.Response>> obtenerMercado() {
        List<VentaDTO.Response> ventas = ventaService.obtenerMercado();
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<VentaDTO.Response>> obtenerPorTipo(@PathVariable Venta.TipoVenta tipo) {
        List<VentaDTO.Response> ventas = ventaService.obtenerPorTipo(tipo);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<VentaDTO.Response>> obtenerVentasUsuario(@PathVariable Long userId) {
        List<VentaDTO.Response> ventas = ventaService.obtenerVentasUsuario(userId);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/compras/{userId}")
    public ResponseEntity<List<VentaDTO.Response>> obtenerComprasUsuario(@PathVariable Long userId) {
        List<VentaDTO.Response> ventas = ventaService.obtenerComprasUsuario(userId);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/carta/{cartaId}/ofertas")
    public ResponseEntity<List<VentaDTO.Response>> obtenerOfertasDeCarta(@PathVariable Long cartaId) {
        List<VentaDTO.Response> ofertas = ventaService.obtenerOfertasDeCarta(cartaId);
        return ResponseEntity.ok(ofertas);
    }

    @PostMapping("/valorar")
    public ResponseEntity<Valoracion> valorarVenta(@RequestParam Long ventaId, @RequestBody VentaDTO.ValorarRequest request) {
        Valoracion valoracion = ventaService.valorar(ventaId, request);
        return ResponseEntity.ok(valoracion);
    }

    @GetMapping("/valoraciones/vendedor/{vendedorId}")
    public ResponseEntity<List<Valoracion>> obtenerValoracionesDeVendedor(@PathVariable Long vendedorId) {
        List<Valoracion> valoraciones = ventaService.obtenerValoracionesDeVendedor(vendedorId);
        return ResponseEntity.ok(valoraciones);
    }

    @PostMapping("/deseos")
    public ResponseEntity<Listadeseos> agregarADeseos(@RequestBody VentaDTO.DeseoRequest request) {
        Listadeseos deseo = ventaService.agregarADeseos(request.getUsuarioId(), request);
        return ResponseEntity.ok(deseo);
    }

    @DeleteMapping("/deseos")
    public ResponseEntity<Void> eliminarDeDeseos(@RequestParam Long usuarioId, @RequestParam Long itemId) {
        ventaService.eliminarDeDeseosPorId(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deseos/{usuarioId}")
    public ResponseEntity<List<Listadeseos>> obtenerDeseosUsuario(@PathVariable Long usuarioId) {
        List<Listadeseos> deseos = ventaService.obtenerDeseosUsuario(usuarioId);
        return ResponseEntity.ok(deseos);
    }

    @GetMapping("/activas")
public ResponseEntity<List<VentaDTO.Response>> obtenerVentasActivas() {
    List<VentaDTO.Response> ventas = ventaService.obtenerMercado();
    return ResponseEntity.ok(ventas);
}

@GetMapping("/mercado/propuestas-recibidas")
public String propuestasRecibidas(HttpSession session, Model model) {
    if (session.getAttribute("username") == null) return "redirect:/web/login";
    String username = (String) session.getAttribute("username");
    User usuario = UserRepository.findByUsername(username).orElse(null);
    model.addAttribute("username", username);
    model.addAttribute("usuarioId", usuario != null ? usuario.getId() : null);
    return "propuestas-recibidas";
}
}