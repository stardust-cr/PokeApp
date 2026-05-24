package com.pokeapp.pokeapp.controller;

import com.pokeapp.pokeapp.dto.VentaDTO;
import com.pokeapp.pokeapp.model.Carta;
import com.pokeapp.pokeapp.model.User;
import com.pokeapp.pokeapp.model.Venta;
import com.pokeapp.pokeapp.repository.CartaRepository;
import com.pokeapp.pokeapp.repository.UserRepository;
import com.pokeapp.pokeapp.service.VentaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/web")
public class MercadoWebController {

    private final VentaService ventaService;
    private final UserRepository userRepository;
    private final CartaRepository cartaRepository;

    public MercadoWebController(VentaService ventaService,
                                 UserRepository userRepository,
                                 CartaRepository cartaRepository) {
        this.ventaService    = ventaService;
        this.userRepository  = userRepository;
        this.cartaRepository = cartaRepository;
    }

    // GET /web/mercado
    @GetMapping("/mercado")
    public String verMercado(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/web/login";

        String username = (String) session.getAttribute("username");
        User usuario = userRepository.findByUsername(username).orElse(null);

        model.addAttribute("username", username);
        model.addAttribute("usuarioId", usuario != null ? usuario.getId() : null);

        return "mercado";
    }

    // GET /web/venta/nueva  (formulario publicar)
    @GetMapping("/venta/nueva")
    public String formularioVenta(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/web/login";

        String username = (String) session.getAttribute("username");
        User usuario = userRepository.findByUsername(username).orElse(null);

        List<Carta> todasLasCartas = cartaRepository.findAll();

        model.addAttribute("username", username);
        model.addAttribute("usuarioId", usuario != null ? usuario.getId() : null);
        model.addAttribute("misCartas", todasLasCartas);

        return "venta";
    }

    // POST /web/venta/crear  (publicar venta desde formulario HTML)
    @PostMapping("/venta/crear")
    public String crearVenta(@RequestParam Long vendedorId,
                              @RequestParam Long cartaId,
                              @RequestParam String tipo,
                              @RequestParam(required = false) String precio,
                              @RequestParam(required = false) String descripcion,
                              HttpSession session,
                              RedirectAttributes ra) {
        if (session.getAttribute("username") == null) return "redirect:/web/login";
        try {
            VentaDTO.CrearRequest req = new VentaDTO.CrearRequest();
            req.setCartaId(cartaId);
            req.setTipo(Venta.TipoVenta.valueOf(tipo));
            if (precio != null && !precio.isBlank()) {
                req.setPrecio(new BigDecimal(precio));
            }
            ventaService.publicar(vendedorId, req);
            ra.addFlashAttribute("exito", "¡Anuncio publicado correctamente!");
            return "redirect:/web/mercado";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/venta/nueva";
        }
    }

    // GET /web/venta  (redirige a /nueva para no romper links existentes)
    @GetMapping("/venta")
    public String formularioVentaLegacy(HttpSession session) {
        if (session.getAttribute("username") == null) return "redirect:/web/login";
        return "redirect:/web/venta/nueva";
    }

    // GET /web/mercado/mis-ventas
    @GetMapping("/mercado/mis-ventas")
    public String misVentas(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/web/login";

        String username = (String) session.getAttribute("username");
        User usuario = userRepository.findByUsername(username).orElse(null);

        model.addAttribute("username", username);
        model.addAttribute("usuarioId", usuario != null ? usuario.getId() : null);

        return "MisVentas";
    }

    // GET /web/mercado/mis-compras
    @GetMapping("/mercado/mis-compras")
    public String misCompras(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/web/login";

        String username = (String) session.getAttribute("username");
        User usuario = userRepository.findByUsername(username).orElse(null);

        model.addAttribute("username", username);
        model.addAttribute("usuarioId", usuario != null ? usuario.getId() : null);

        return "MisCompras";
    }

    // GET /web/mercado/valoraciones/{vendedorId}  (perfil de un vendedor concreto)
    @GetMapping("/mercado/valoraciones/{vendedorId}")
    public String verValoracionesVendedor(@PathVariable Long vendedorId,
                                          HttpSession session,
                                          Model model) {
        if (session.getAttribute("username") == null) return "redirect:/web/login";

        String username = (String) session.getAttribute("username");
        User usuario = userRepository.findByUsername(username).orElse(null);

        model.addAttribute("username", username);
        model.addAttribute("usuarioId", usuario != null ? usuario.getId() : null);
        model.addAttribute("vendedorId", vendedorId);

        return "valoraciones";
    }
}