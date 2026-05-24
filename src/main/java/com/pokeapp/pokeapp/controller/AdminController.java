package com.pokeapp.pokeapp.controller;

import com.pokeapp.pokeapp.dto.CartaDTO;
import com.pokeapp.pokeapp.model.Carta;
import com.pokeapp.pokeapp.model.User;
import com.pokeapp.pokeapp.repository.CartaRepository;
import com.pokeapp.pokeapp.repository.ColeccionCartaRepository;
import com.pokeapp.pokeapp.repository.HistorialPrecioRepository;
import com.pokeapp.pokeapp.repository.ListaDeseosRepository;
import com.pokeapp.pokeapp.repository.MazoCartaRepository;
import com.pokeapp.pokeapp.repository.UserRepository;
import com.pokeapp.pokeapp.repository.VentaRepository;
import com.pokeapp.pokeapp.repository.ValoracionRepository;
import com.pokeapp.pokeapp.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Controller
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final CartaRepository cartaRepository;
    private final ColeccionCartaRepository coleccionCartaRepository;
    private final MazoCartaRepository mazoCartaRepository;
    private final HistorialPrecioRepository historialPrecioRepository;
    private final ListaDeseosRepository listaDeseosRepository;
    private final VentaRepository ventaRepository;
    private final ValoracionRepository valoracionRepository;

    public AdminController(AdminService adminService,
                           UserRepository userRepository,
                           CartaRepository cartaRepository,
                           ColeccionCartaRepository coleccionCartaRepository,
                           MazoCartaRepository mazoCartaRepository,
                           HistorialPrecioRepository historialPrecioRepository,
                           ListaDeseosRepository listaDeseosRepository,
                           VentaRepository ventaRepository,
                           ValoracionRepository valoracionRepository) {
        this.adminService = adminService;
        this.userRepository = userRepository;
        this.cartaRepository = cartaRepository;
        this.coleccionCartaRepository = coleccionCartaRepository;
        this.mazoCartaRepository = mazoCartaRepository;
        this.historialPrecioRepository = historialPrecioRepository;
        this.listaDeseosRepository = listaDeseosRepository;
        this.ventaRepository = ventaRepository;
        this.valoracionRepository = valoracionRepository;
    }

    private boolean esAdmin(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) return false;
        User user = userRepository.findByUsername(username).orElse(null);
        return user != null && "ADMIN".equals(user.getRol());
    }

    // ── Panel principal ───────────────────────────────────────

    @GetMapping({"/web/admin", "/web/admin/"})
    public String panel(Model model, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/web/home";
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("totalUsuarios", adminService.totalUsuarios());
        model.addAttribute("totalAdmins", adminService.totalAdmins());
        model.addAttribute("totalVentas", adminService.totalVentas());
        model.addAttribute("totalValoraciones", adminService.totalValoraciones());
        model.addAttribute("ventasCompletadas", adminService.ventasCompletadas());
        model.addAttribute("cartasMasVendidas", adminService.cartasMasVendidas());
        model.addAttribute("usuariosMasActivos", adminService.usuariosMasActivos());
        return "admin/panel";
    }

    // ── Usuarios ──────────────────────────────────────────────

    @GetMapping("/web/admin/usuarios")
    public String usuarios(Model model, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/web/home";
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("usuarios", adminService.getTodosLosUsuarios());
        return "admin/usuarios";
    }

    @PostMapping("/web/admin/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/web/home";
        adminService.eliminarUsuario(id);
        return "redirect:/web/admin/usuarios";
    }

    @PostMapping("/web/admin/usuarios/{id}/rol")
    public String cambiarRol(@PathVariable Long id, @RequestParam String rol, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/web/home";
        adminService.cambiarRol(id, rol);
        return "redirect:/web/admin/usuarios";
    }

    // ── Valoraciones ──────────────────────────────────────────

    @GetMapping("/web/admin/valoraciones")
    public String valoraciones(Model model, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/web/home";
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("valoraciones", adminService.getTodasLasValoraciones());
        return "admin/valoraciones";
    }

    @PostMapping("/web/admin/valoraciones/{id}/eliminar")
    public String eliminarValoracion(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/web/home";
        adminService.eliminarValoracion(id);
        return "redirect:/web/admin/valoraciones";
    }

    // ── Ventas ────────────────────────────────────────────────

    @GetMapping("/web/admin/ventas")
    public String ventas(Model model, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/web/home";
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("ventas", adminService.getTodasLasVentas());
        return "admin/ventas";
    }

    @PostMapping("/web/admin/ventas/{id}/eliminar")
    public String eliminarVenta(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/web/home";
        adminService.eliminarVenta(id);
        return "redirect:/web/admin/ventas";
    }

    // ── Cartas ────────────────────────────────────────────────

    @GetMapping("/web/admin/cartas")
    public String cartas(Model model, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/web/home";
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("cartas", adminService.getTodasLasCartas());
        model.addAttribute("rarezas", Carta.Rareza.values());
        return "admin/cartas";
    }

    @PostMapping("/web/admin/cartas/crear")
    public String crearCarta(@RequestParam String nombre,
                              @RequestParam String tipo,
                              @RequestParam String setCodigo,
                              @RequestParam String rareza,
                              @RequestParam String imagenUrl,
                              @RequestParam(required = false) String precioBase,
                              HttpSession session) {
        if (!esAdmin(session)) return "redirect:/web/home";
        CartaDTO.Request req = new CartaDTO.Request();
        req.setNombre(nombre);
        req.setTipo(tipo);
        req.setSetCodigo(setCodigo);
        req.setRareza(Carta.Rareza.valueOf(rareza));
        req.setImagenUrl(imagenUrl);
        if (precioBase != null && !precioBase.isBlank())
            req.setPrecioBase(new BigDecimal(precioBase));
        adminService.crearCarta(req);
        return "redirect:/web/admin/cartas";
    }

    @PostMapping("/web/admin/cartas/{id}/editar")
    public String editarCarta(@PathVariable Long id,
                               @RequestParam String nombre,
                               @RequestParam String tipo,
                               @RequestParam String setCodigo,
                               @RequestParam String rareza,
                               @RequestParam String imagenUrl,
                               @RequestParam(required = false) String precioBase,
                               HttpSession session) {
        if (!esAdmin(session)) return "redirect:/web/home";
        CartaDTO.Request req = new CartaDTO.Request();
        req.setNombre(nombre);
        req.setTipo(tipo);
        req.setSetCodigo(setCodigo);
        req.setRareza(Carta.Rareza.valueOf(rareza));
        req.setImagenUrl(imagenUrl);
        if (precioBase != null && !precioBase.isBlank())
            req.setPrecioBase(new BigDecimal(precioBase));
        adminService.editarCarta(id, req);
        return "redirect:/web/admin/cartas";
    }

    @PostMapping("/web/admin/cartas/{id}/eliminar")
    @Transactional
    public String eliminarCarta(@PathVariable Long id, HttpSession session) {
        if (!esAdmin(session)) return "redirect:/web/home";
        if (!cartaRepository.existsById(id)) return "redirect:/web/admin/cartas";

        // 1. Borrar historial de precios
        historialPrecioRepository.deleteByCartaId(id);

        // 2. Borrar de listas de deseos
        listaDeseosRepository.deleteByCartaId(id);

        // 3. Borrar de colecciones
        coleccionCartaRepository.deleteByCartaId(id);

        // 4. Borrar de mazos
        mazoCartaRepository.deleteByCartaId(id);

        // 5. Para ventas: primero borrar valoraciones asociadas, luego las ventas
        ventaRepository.findByCartaId(id).forEach(v -> {
            valoracionRepository.deleteByVentaId(v.getId());
            ventaRepository.delete(v);
        });

        // 6. Finalmente borrar la carta
        cartaRepository.deleteById(id);
        return "redirect:/web/admin/cartas";
    }
}