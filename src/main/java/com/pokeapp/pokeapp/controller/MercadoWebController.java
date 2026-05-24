package com.pokeapp.pokeapp.controller;

import com.pokeapp.pokeapp.dto.VentaDTO;
import com.pokeapp.pokeapp.model.Carta;
import com.pokeapp.pokeapp.model.Listadeseos;
import com.pokeapp.pokeapp.model.Mazos;
import com.pokeapp.pokeapp.model.Mazos_Cartas;
import com.pokeapp.pokeapp.model.User;
import com.pokeapp.pokeapp.model.Venta;
import com.pokeapp.pokeapp.repository.CartaRepository;
import com.pokeapp.pokeapp.repository.ListaDeseosRepository;
import com.pokeapp.pokeapp.repository.MazoCartaRepository;
import com.pokeapp.pokeapp.repository.MazoRepository;
import com.pokeapp.pokeapp.repository.UserRepository;
import com.pokeapp.pokeapp.service.VentaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")
public class MercadoWebController {

    private final VentaService ventaService;
    private final UserRepository userRepository;
    private final CartaRepository cartaRepository;
    private final ListaDeseosRepository listaDeseosRepository;
    private final MazoRepository mazoRepository;
    private final MazoCartaRepository mazoCartaRepository;

    public MercadoWebController(VentaService ventaService,
                                 UserRepository userRepository,
                                 CartaRepository cartaRepository,
                                 ListaDeseosRepository listaDeseosRepository,
                                 MazoRepository mazoRepository,
                                 MazoCartaRepository mazoCartaRepository) {
        this.ventaService          = ventaService;
        this.userRepository        = userRepository;
        this.cartaRepository       = cartaRepository;
        this.listaDeseosRepository = listaDeseosRepository;
        this.mazoRepository        = mazoRepository;
        this.mazoCartaRepository   = mazoCartaRepository;
    }

    @GetMapping("/mercado")
    public String verMercado(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/web/login";
        String username = (String) session.getAttribute("username");
        User usuario = userRepository.findByUsername(username).orElse(null);
        model.addAttribute("username", username);
        model.addAttribute("usuarioId", usuario != null ? usuario.getId() : null);
        return "mercado";
    }

    @GetMapping("/venta/nueva")
    public String formularioVenta(
            @RequestParam(value = "fuente", defaultValue = "coleccion") String fuente,
            @RequestParam(value = "mazoId", required = false) Long mazoId,
            HttpSession session, Model model) {

        if (session.getAttribute("username") == null) return "redirect:/web/login";

        String username = (String) session.getAttribute("username");
        User usuario = userRepository.findByUsername(username).orElse(null);
        Long usuarioId = usuario != null ? usuario.getId() : null;

        List<Carta> misCartas = new ArrayList<>();

        if (usuarioId != null) {
            if ("wishlist".equals(fuente)) {
                misCartas = listaDeseosRepository.findByUsuarioId(usuarioId)
                        .stream()
                        .map(Listadeseos::getCarta)
                        .distinct()
                        .collect(Collectors.toList());

            } else if ("mazo".equals(fuente)) {
                if (mazoId != null) {
                    misCartas = mazoCartaRepository.findById_MazoId(mazoId)
                            .stream()
                            .map(Mazos_Cartas::getCarta)
                            .distinct()
                            .collect(Collectors.toList());
                } else {
                    misCartas = mazoRepository.findByUsuarioId(usuarioId)
                            .stream()
                            .flatMap(m -> m.getCartas().stream())
                            .map(Mazos_Cartas::getCarta)
                            .distinct()
                            .collect(Collectors.toList());
                }
            } else {
                misCartas = cartaRepository.findAll();
            }
        }

        List<Mazos> misMazos = usuarioId != null
                ? mazoRepository.findByUsuarioId(usuarioId)
                : List.of();

        model.addAttribute("username", username);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("misCartas", misCartas);
        model.addAttribute("misMazos", misMazos);
        model.addAttribute("fuenteActiva", fuente);
        model.addAttribute("mazoIdActivo", mazoId);

        return "venta";
    }

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

    @GetMapping("/venta")
    public String formularioVentaLegacy(HttpSession session) {
        if (session.getAttribute("username") == null) return "redirect:/web/login";
        return "redirect:/web/venta/nueva";
    }

    @GetMapping("/mercado/mis-ventas")
    public String misVentas(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/web/login";
        String username = (String) session.getAttribute("username");
        User usuario = userRepository.findByUsername(username).orElse(null);
        model.addAttribute("username", username);
        model.addAttribute("usuarioId", usuario != null ? usuario.getId() : null);
        return "MisVentas";
    }

    @GetMapping("/mercado/mis-compras")
    public String misCompras(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/web/login";
        String username = (String) session.getAttribute("username");
        User usuario = userRepository.findByUsername(username).orElse(null);
        model.addAttribute("username", username);
        model.addAttribute("usuarioId", usuario != null ? usuario.getId() : null);
        return "MisCompras";
    }

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