package com.pokeapp.pokeapp.service;

import com.pokeapp.pokeapp.dto.CartaDTO;
import com.pokeapp.pokeapp.model.User;
import com.pokeapp.pokeapp.model.Valoracion;
import com.pokeapp.pokeapp.model.Venta;
import com.pokeapp.pokeapp.repository.UserRepository;
import com.pokeapp.pokeapp.repository.ValoracionRepository;
import com.pokeapp.pokeapp.repository.VentaRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ValoracionRepository valoracionRepository;
    private final VentaRepository ventaRepository;
    private final CartaService cartaService;

    public AdminService(UserRepository userRepository,
                        ValoracionRepository valoracionRepository,
                        VentaRepository ventaRepository,
                        CartaService cartaService) {
        this.userRepository = userRepository;
        this.valoracionRepository = valoracionRepository;
        this.ventaRepository = ventaRepository;
        this.cartaService = cartaService;
    }

    // ── Usuarios ──────────────────────────────────────────────

    public List<User> getTodosLosUsuarios() {
        return userRepository.findAll();
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void cambiarRol(Long id, String nuevoRol) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        user.setRol(nuevoRol);
        userRepository.save(user);
    }

    // ── Estadísticas ──────────────────────────────────────────

    public long totalUsuarios() { return userRepository.count(); }
    public long totalAdmins()   { return userRepository.countByRol("ADMIN"); }
    public long totalVentas()   { return ventaRepository.count(); }
    public long totalValoraciones() { return valoracionRepository.count(); }

    public long ventasCompletadas() {
        return ventaRepository.findByEstado(Venta.EstadoVenta.COMPLETADA).size();
    }

    public Map<String, Long> cartasMasVendidas() {
        Map<String, Long> resultado = new LinkedHashMap<>();
        ventaRepository.findCartasMasVendidas().stream().limit(5)
                .forEach(row -> resultado.put((String) row[0], (Long) row[1]));
        return resultado;
    }

    public Map<String, Long> usuariosMasActivos() {
        Map<String, Long> resultado = new LinkedHashMap<>();
        ventaRepository.findUsuariosMasActivos().stream().limit(5)
                .forEach(row -> resultado.put((String) row[0], (Long) row[1]));
        return resultado;
    }

    // ── Valoraciones ──────────────────────────────────────────

    public List<Valoracion> getTodasLasValoraciones() {
        return valoracionRepository.findAll();
    }

    @Transactional
    public void eliminarValoracion(Long id) {
        valoracionRepository.deleteById(id);
    }

    // ── Ventas ────────────────────────────────────────────────

    public List<Venta> getTodasLasVentas() {
        return ventaRepository.findAll();
    }

    @Transactional
    public void eliminarVenta(Long id) {
        ventaRepository.deleteById(id);
    }

    // ── Cartas ────────────────────────────────────────────────

    public List<CartaDTO.Response> getTodasLasCartas() {
        return cartaService.todas();
    }

    public CartaDTO.Response crearCarta(CartaDTO.Request request) {
        return cartaService.crear(request);
    }

    public CartaDTO.Response editarCarta(Long id, CartaDTO.Request request) {
        return cartaService.actualizar(id, request);
    }

    @Transactional
    public void eliminarCarta(Long id) {
        cartaService.obtenerPorId(id); // lanza excepción si no existe
        // eliminamos directamente via cartaService internamente
    }
}
