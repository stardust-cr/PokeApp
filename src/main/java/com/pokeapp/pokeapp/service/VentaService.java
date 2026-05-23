package com.pokeapp.pokeapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pokeapp.pokeapp.dto.VentaDTO;
import com.pokeapp.pokeapp.model.Carta;
import com.pokeapp.pokeapp.model.Listadeseos;
import com.pokeapp.pokeapp.model.User;
import com.pokeapp.pokeapp.model.Valoracion;
import com.pokeapp.pokeapp.model.Venta;
import com.pokeapp.pokeapp.repository.CartaRepository;
import com.pokeapp.pokeapp.repository.ListaDeseosRepository;
import com.pokeapp.pokeapp.repository.UserRepository;
import com.pokeapp.pokeapp.repository.ValoracionRepository;
import com.pokeapp.pokeapp.repository.VentaRepository;

import jakarta.transaction.Transactional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final UserRepository userRepository;
    private final CartaRepository cartaRepository;
    private final ValoracionRepository valoracionRepository;
    private final ListaDeseosRepository listaDeseosRepository;

    public VentaService(VentaRepository ventaRepository,
                        UserRepository userRepository,
                        CartaRepository cartaRepository,
                        ValoracionRepository valoracionRepository,
                        ListaDeseosRepository listaDeseosRepository) {
        this.ventaRepository = ventaRepository;
        this.userRepository = userRepository;
        this.cartaRepository = cartaRepository;
        this.valoracionRepository = valoracionRepository;
        this.listaDeseosRepository = listaDeseosRepository;
    }

    @Transactional
    public VentaDTO.Response publicar(Long vendedorId, VentaDTO.CrearRequest request) {
        // FIX 1: SUBASTA no existe en TipoVenta → el enum solo tiene VENTA y TRUEQUE
        if (request.getTipo() == Venta.TipoVenta.VENTA && request.getPrecio() == null)
            throw new IllegalArgumentException("El precio es obligatorio para ventas de tipo VENTA.");
        User vendedor = userRepository.findById(vendedorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendedor no encontrado."));
        Carta carta = cartaRepository.findById(request.getCartaId())
                .orElseThrow(() -> new IllegalArgumentException("Carta no encontrada."));
        Venta venta = new Venta();
        venta.setVendedor(vendedor);
        venta.setCarta(carta);
        venta.setTipo(request.getTipo());
        venta.setPrecio(request.getPrecio());
        return toResponse(ventaRepository.save(venta));
    }

    @Transactional
    public VentaDTO.Response comprar(Long ventaId, VentaDTO.ComprarRequest request) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada."));
        if (venta.getEstado() != Venta.EstadoVenta.PENDIENTE)
            throw new IllegalStateException("La venta no está disponible para comprar.");
        if (venta.getVendedor().getId().equals(request.getCompradorId()))
            throw new IllegalArgumentException("No puedes comprar tu propia carta.");
        User comprador = userRepository.findById(request.getCompradorId())
                .orElseThrow(() -> new IllegalArgumentException("Comprador no encontrado."));
        venta.setComprador(comprador);
        venta.setEstado(Venta.EstadoVenta.COMPLETADA);
        return toResponse(ventaRepository.save(venta));
    }

    @Transactional
    public VentaDTO.Response cancelar(Long ventaId, Long vendedorId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada."));
        if (!venta.getVendedor().getId().equals(vendedorId))
            throw new IllegalArgumentException("Solo el vendedor puede cancelar la venta.");
        if (venta.getEstado() != Venta.EstadoVenta.PENDIENTE)
            throw new IllegalStateException("Solo las ventas pendientes pueden ser canceladas.");
        venta.setEstado(Venta.EstadoVenta.CANCELADA);
        return toResponse(ventaRepository.save(venta));
    }

    public List<VentaDTO.Response> obtenerMercado() {
        return ventaRepository.findByEstado(Venta.EstadoVenta.PENDIENTE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<VentaDTO.Response> obtenerPorTipo(Venta.TipoVenta tipo) {
        return ventaRepository.findByTipoAndEstado(tipo, Venta.EstadoVenta.PENDIENTE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // FIX 2: findByVendedorIdOrCompradorId no existe en VentaRepository → usar findByVendedorId
    public List<VentaDTO.Response> obtenerVentasUsuario(Long userId) {
        return ventaRepository.findByVendedorId(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<VentaDTO.Response> obtenerComprasUsuario(Long userId) {
        return ventaRepository.findByCompradorId(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<VentaDTO.Response> obtenerOfertasDeCarta(Long cartaId) {
        return ventaRepository.findActivasByCartaId(cartaId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public Valoracion valorar(Long ventaId, VentaDTO.ValorarRequest request) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada."));
        if (venta.getEstado() != Venta.EstadoVenta.COMPLETADA)
            throw new IllegalStateException("Solo las ventas completadas pueden ser valoradas.");
        if (valoracionRepository.existsByVentaIdAndUsuarioId(venta.getId(), request.getAutorId()))
            throw new IllegalStateException("Esta venta ya ha sido valorada.");
        Long vendedorId = venta.getVendedor().getId();
        // FIX 3: getComprador() puede ser null → comprobar antes de llamar getId()
        Long compradorId = venta.getComprador() != null ? venta.getComprador().getId() : null;
        if (!request.getAutorId().equals(vendedorId) && !request.getAutorId().equals(compradorId))
            throw new IllegalArgumentException("Solo el comprador o el vendedor pueden valorar esta venta.");
        User autor = userRepository.findById(request.getAutorId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        Valoracion valoracion = new Valoracion();
        valoracion.setVenta(venta);
        valoracion.setUsuario(autor);
        valoracion.setPuntuacion(request.getPuntuacion());
        valoracion.setComentario(request.getComentario());
        return valoracionRepository.save(valoracion);
    }

    public List<Valoracion> obtenerValoracionesDeVendedor(Long vendedorId) {
        return valoracionRepository.findByVendedorId(vendedorId);
    }

    @Transactional
    public Listadeseos agregarADeseos(Long usuarioId, VentaDTO.DeseoRequest request) {
        if (listaDeseosRepository.existsByUsuarioIdAndCartaId(usuarioId, request.getCartaId()))
            throw new IllegalArgumentException("La carta ya está en la lista de deseos.");
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        Carta carta = cartaRepository.findById(request.getCartaId())
                .orElseThrow(() -> new IllegalArgumentException("Carta no encontrada."));
        Listadeseos deseo = new Listadeseos();
        deseo.setUsuario(usuario);
        deseo.setCarta(carta);
        deseo.setPrecioObjetivo(request.getPrecioObjetivo());
        deseo.setAlertaActiva(request.getAlertaActiva() != null ? request.getAlertaActiva() : Boolean.TRUE);
        return listaDeseosRepository.save(deseo);
    }

    @Transactional
public void eliminarDeDeseosPorId(Long itemId) {
    if (!listaDeseosRepository.existsById(itemId))
        throw new IllegalArgumentException("El item no existe en la lista de deseos.");
    listaDeseosRepository.deleteById(itemId);
}

    public List<Listadeseos> obtenerDeseosUsuario(Long usuarioId) {
        return listaDeseosRepository.findByUsuarioId(usuarioId);
    }

    private VentaDTO.Response toResponse(Venta venta) {
        VentaDTO.Response r = new VentaDTO.Response();
        r.setId(venta.getId());
        r.setVendedorId(venta.getVendedor().getId());
        r.setVendedorNombre(venta.getVendedor().getUsername());
        if (venta.getComprador() != null) {
            r.setCompradorId(venta.getComprador().getId());
            r.setCompradorNombre(venta.getComprador().getUsername());
        }
        r.setCartaId(venta.getCarta().getId());
        r.setCartaNombre(venta.getCarta().getNombre());
        r.setPrecio(venta.getPrecio());
        r.setTipo(venta.getTipo());
        r.setEstado(venta.getEstado());
        r.setFecha(venta.getFecha());
        return r;
    }
}
