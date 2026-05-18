package com.pokeapp.pokeapp.controller;

import com.pokeapp.pokeapp.model.User;
import com.pokeapp.pokeapp.repository.UserRepository;
import com.pokeapp.pokeapp.service.VentaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web")
public class MercadoWebController {

    private final VentaService ventaService;
    private final UserRepository userRepository;

    public MercadoWebController(VentaService ventaService, UserRepository userRepository) {
        this.ventaService = ventaService;
        this.userRepository = userRepository;
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

    // GET /web/venta  (formulario publicar)
    @GetMapping("/venta")
    public String formularioVenta(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/web/login";

        String username = (String) session.getAttribute("username");
        User usuario = userRepository.findByUsername(username).orElse(null);

        model.addAttribute("username", username);
        model.addAttribute("usuarioId", usuario != null ? usuario.getId() : null);

        return "venta";
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
