package com.pokeapp.pokeapp.service;

import com.pokeapp.pokeapp.dto.PropuestaTruequeDTO;
import com.pokeapp.pokeapp.model.*;
import com.pokeapp.pokeapp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TruequeService {

    private final PropuestaTruequeRepository propuestaRepository;
    private final VentaRepository ventaRepository;
    private final UserRepository userRepository;
    private final CartaRepository cartaRepository;
    private final ColeccionCartaRepository coleccionCartaRepository;

    public TruequeService(PropuestaTruequeRepository propuestaRepository,
                          VentaRepository ventaRepository,
                          UserRepository userRepository,
                          CartaRepository cartaRepository,
                          ColeccionCartaRepository coleccionCartaRepository) {
        this.propuestaRepository      = propuestaRepository;
        this.ventaRepository          = ventaRepository;
        this.userRepository           = userRepository;
        this.cartaRepository          = cartaRepository;
        this.coleccionCartaRepository = coleccionCartaRepository;
    }

    /**
     * Intercambio directo: usa la primera carta de la colección del usuario
     * y completa el trueque al instante, sin diálogo.
     */
    @Transactional
    public PropuestaTruequeDTO.Response intercambioDirecto(Long ventaId, Long proponenteId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        if (venta.getTipo() != Venta.TipoVenta.TRUEQUE)
            throw new IllegalArgumentException("Esta publicación no es de tipo trueque");
        if (venta.getEstado() != Venta.EstadoVenta.PENDIENTE)
            throw new IllegalStateException("Esta publicación ya no está disponible");
        if (venta.getVendedor().getId().equals(proponenteId))
            throw new IllegalArgumentException("No puedes intercambiar con tu propia publicación");

        User proponente = userRepository.findById(proponenteId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Primera carta disponible en la colección del usuario
        List<Carta> cartas = coleccionCartaRepository.findCartasByUsuarioId(proponenteId);
        if (cartas.isEmpty())
            throw new IllegalStateException("No tienes cartas en tu colección para intercambiar");

        Carta cartaOfrecida = cartas.get(0);

        // Crear y aceptar la propuesta directamente
        PropuestaTrueque p = new PropuestaTrueque();
        p.setVenta(venta);
        p.setProponente(proponente);
        p.setCartaOfrecida(cartaOfrecida);
        p.setMensaje(null);
        p.setEstado(PropuestaTrueque.EstadoPropuesta.ACEPTADA);
        p.setFechaRespuesta(LocalDateTime.now());

        // Completar la venta
        venta.setComprador(proponente);
        venta.setEstado(Venta.EstadoVenta.COMPLETADA);
        ventaRepository.save(venta);

        // Rechazar otras propuestas pendientes de la misma venta
        propuestaRepository.findByVentaId(ventaId).forEach(otra -> {
            if (otra.getEstado() == PropuestaTrueque.EstadoPropuesta.PENDIENTE) {
                otra.setEstado(PropuestaTrueque.EstadoPropuesta.RECHAZADA);
                otra.setFechaRespuesta(LocalDateTime.now());
                propuestaRepository.save(otra);
            }
        });

        return toResponse(propuestaRepository.save(p));
    }

    public List<PropuestaTruequeDTO.Response> propuestasDeVenta(Long ventaId) {
        return propuestaRepository.findByVentaId(ventaId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PropuestaTruequeDTO.Response> propuestasRecibidas(Long vendedorId) {
        return propuestaRepository.findByVenta_Vendedor_IdAndEstado(
                        vendedorId, PropuestaTrueque.EstadoPropuesta.PENDIENTE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PropuestaTruequeDTO.Response> propuestasEnviadas(Long proponenteId) {
        return propuestaRepository.findByProponenteId(proponenteId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public long contarPropuestasPendientes(Long vendedorId) {
        return propuestaRepository.countByVenta_Vendedor_IdAndEstado(
                vendedorId, PropuestaTrueque.EstadoPropuesta.PENDIENTE);
    }

    public List<PropuestaTruequeDTO.Response> todasPropuestasRecibidas(Long vendedorId) {
        return propuestaRepository.findByVenta_Vendedor_Id(vendedorId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private PropuestaTruequeDTO.Response toResponse(PropuestaTrueque p) {
        PropuestaTruequeDTO.Response r = new PropuestaTruequeDTO.Response();
        r.setId(p.getId());
        r.setVentaId(p.getVenta().getId());
        r.setCartaVentaNombre(p.getVenta().getCarta().getNombre());
        r.setCartaVentaImagenUrl(p.getVenta().getCarta().getImagenUrl());
        r.setVendedorId(p.getVenta().getVendedor().getId());
        r.setVendedorNombre(p.getVenta().getVendedor().getUsername());
        r.setProponenteId(p.getProponente().getId());
        r.setProponenteNombre(p.getProponente().getUsername());
        r.setCartaOfrecidaId(p.getCartaOfrecida().getId());
        r.setCartaOfrecidaNombre(p.getCartaOfrecida().getNombre());
        r.setCartaOfrecidaImagenUrl(p.getCartaOfrecida().getImagenUrl());
        r.setMensaje(p.getMensaje());
        r.setEstado(p.getEstado());
        r.setFechaPropuesta(p.getFechaPropuesta());
        r.setFechaRespuesta(p.getFechaRespuesta());
        return r;
    }
}

