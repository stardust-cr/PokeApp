package com.pokeapp.pokeapp.controller;

import java.math.BigDecimal;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pokeapp.pokeapp.dto.VentaDTO;
import com.pokeapp.pokeapp.model.Listadeseos;
import com.pokeapp.pokeapp.model.User;
import com.pokeapp.pokeapp.repository.UserRepository;
import com.pokeapp.pokeapp.service.CartaService;
import com.pokeapp.pokeapp.service.VentaService;

@Controller
@RequestMapping("/web")
public class ColeccionWebController {

    private final CartaService cartaService;
    private final VentaService ventaService;
    private final UserRepository userRepository;

    public ColeccionWebController(CartaService cartaService,
                                  VentaService ventaService,
                                  UserRepository userRepository) {
        this.cartaService = cartaService;
        this.ventaService = ventaService;
        this.userRepository = userRepository;
    }

    @GetMapping("/wishlist")
    public String wishlist(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/web/login";
        }
        User usuario = userRepository.findByUsername(username).orElse(null);
        if (usuario == null) {
            return "redirect:/web/login";
        }

        List<Listadeseos> deseos = ventaService.obtenerDeseosUsuario(usuario.getId());
        model.addAttribute("wishlist", deseos);
        model.addAttribute("cartasDisponibles", cartaService.todas());
        model.addAttribute("usuarioId", usuario.getId());
        model.addAttribute("username", username);

        return "wishlist";
    }

    @PostMapping("/wishlist/agregar")
    public String agregarWishlist(@RequestParam Long cartaId,
                                  @RequestParam BigDecimal precioDeseado,
                                  HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/web/login";
        }
        User usuario = userRepository.findByUsername(username).orElse(null);
        if (usuario == null) {
            return "redirect:/web/login";
        }

        VentaDTO.DeseoRequest request = new VentaDTO.DeseoRequest();
        request.setUsuarioId(usuario.getId());
        request.setCartaId(cartaId);
        request.setPrecioObjetivo(precioDeseado);
        request.setAlertaActiva(Boolean.TRUE);

        ventaService.agregarADeseos(usuario.getId(), request);
        return "redirect:/web/wishlist";
    }

    @PostMapping("/wishlist/eliminar")
    public String eliminarWishlist(@RequestParam Long cartaId,
                                   HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/web/login";
        }
        User usuario = userRepository.findByUsername(username).orElse(null);
        if (usuario == null) {
            return "redirect:/web/login";
        }

        ventaService.eliminarDeDeseos(usuario.getId(), cartaId);
        return "redirect:/web/wishlist";
    }

    @GetMapping("/coleccion")
    public String coleccion() {
        return "coleccion";
    }

    @GetMapping("/mazos")
    public String mazos() {
        return "mazos";
    }

    @GetMapping("/valoraciones")
    public String valoraciones(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/web/login";
        }
        User usuario = userRepository.findByUsername(username).orElse(null);
        if (usuario == null) {
            return "redirect:/web/login";
        }

        model.addAttribute("valoraciones", ventaService.obtenerValoracionesDeVendedor(usuario.getId()));
        model.addAttribute("username", username);
        return "valoraciones";
    }
}
