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

    public TruequeService(PropuestaTruequeRepository propuestaRepository,
                          VentaRepository ventaRepository,
                          UserRepository userRepository,
                          CartaRepository cartaRepository) {
        this.propuestaRepository = propuestaRepository;
        this.ventaRepository     = ventaRepository;
        this.userRepository      = userRepository;
        this.cartaRepository     = cartaRepository;
    }

    @Transactional
    public PropuestaTruequeDTO.Response proponerTrueque(PropuestaTruequeDTO.CrearRequest req) {
        Venta venta = ventaRepository.findById(req.getVentaId())
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        if (venta.getTipo() != Venta.TipoVenta.TRUEQUE)
            throw new IllegalArgumentException("Esta publicación no es de tipo trueque");
        if (venta.getEstado() != Venta.EstadoVenta.PENDIENTE)
            throw new IllegalStateException("La publicación ya no está disponible");
        if (venta.getVendedor().getId().equals(req.getProponenteId()))
            throw new IllegalArgumentException("No puedes proponer un trueque a tu propia publicación");

        User proponente = userRepository.findById(req.getProponenteId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Carta cartaOfrecida = cartaRepository.findById(req.getCartaOfrecidaId())
                .orElseThrow(() -> new IllegalArgumentException("Carta ofrecida no encontrada"));

        PropuestaTrueque p = new PropuestaTrueque();
        p.setVenta(venta);
        p.setProponente(proponente);
        p.setCartaOfrecida(cartaOfrecida);
        p.setMensaje(req.getMensaje());

        return toResponse(propuestaRepository.save(p));
    }

    @Transactional
    public PropuestaTruequeDTO.Response responderPropuesta(Long propuestaId, Long vendedorId, boolean aceptar) {
        PropuestaTrueque p = propuestaRepository.findById(propuestaId)
                .orElseThrow(() -> new IllegalArgumentException("Propuesta no encontrada"));
        if (!p.getVenta().getVendedor().getId().equals(vendedorId))
            throw new IllegalArgumentException("Solo el vendedor puede responder esta propuesta");
        if (p.getEstado() != PropuestaTrueque.EstadoPropuesta.PENDIENTE)
            throw new IllegalStateException("Esta propuesta ya fue respondida");

        p.setEstado(aceptar ? PropuestaTrueque.EstadoPropuesta.ACEPTADA : PropuestaTrueque.EstadoPropuesta.RECHAZADA);
        p.setFechaRespuesta(LocalDateTime.now());

        if (aceptar) {
            // Marcar la venta como completada y rechazar el resto de propuestas
            Venta venta = p.getVenta();
            venta.setComprador(p.getProponente());
            venta.setEstado(Venta.EstadoVenta.COMPLETADA);
            ventaRepository.save(venta);

            // Rechazar automáticamente el resto de propuestas pendientes
            propuestaRepository.findByVentaId(venta.getId()).forEach(otra -> {
                if (!otra.getId().equals(propuestaId) && otra.getEstado() == PropuestaTrueque.EstadoPropuesta.PENDIENTE) {
                    otra.setEstado(PropuestaTrueque.EstadoPropuesta.RECHAZADA);
                    otra.setFechaRespuesta(LocalDateTime.now());
                    propuestaRepository.save(otra);
                }
            });
        }

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

    public List<PropuestaTruequeDTO.Response> todasPropuestasRecibidas(Long vendedorId) {
    return propuestaRepository.findByVenta_Vendedor_Id(vendedorId)
            .stream().map(this::toResponse).collect(Collectors.toList());
}
}

