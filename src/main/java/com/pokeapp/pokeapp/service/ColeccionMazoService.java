package com.pokeapp.pokeapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pokeapp.pokeapp.model.*;
import com.pokeapp.pokeapp.repository.*;
import java.util.List;

@Service
public class ColeccionMazoService {

    private final ColeccionRepository coleccionRepository;
    private final MazoRepository mazoRepository;
    private final MazoCartaRepository mazoCartaRepository;
    private final CartaRepository cartaRepository;
    private final UserRepository usuarioRepository;

    public ColeccionMazoService(ColeccionRepository coleccionRepository,
                                MazoRepository mazoRepository,
                                MazoCartaRepository mazoCartaRepository,
                                CartaRepository cartaRepository,
                                UserRepository usuarioRepository) {
        this.coleccionRepository = coleccionRepository;
        this.mazoRepository = mazoRepository;
        this.mazoCartaRepository = mazoCartaRepository;
        this.cartaRepository = cartaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Coleccion crearColeccion(Long userId, String nombreColeccion) {
        if (coleccionRepository.existsByUsuarioIdAndNombre(userId, nombreColeccion))
            throw new IllegalArgumentException("Ya existe una colección con ese nombre.");
        User usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        Coleccion c = new Coleccion();
        c.setUsuario(usuario);
        c.setNombre(nombreColeccion);
        return coleccionRepository.save(c);
    }

    public List<Coleccion> obtenerColeccionesUsuario(Long userId) {
        return coleccionRepository.findByUsuarioId(userId);
    }

    @Transactional
    public void eliminarColeccion(Long coleccionId) {
        if (!coleccionRepository.existsById(coleccionId))
            throw new IllegalArgumentException("Colección no encontrada.");
        coleccionRepository.deleteById(coleccionId);
    }

    @Transactional
    public Mazos crearMazo(Long userId, String nombreMazo, String descripcion, boolean esPublico) {
        User usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        Mazos m = new Mazos();
        m.setUsuario(usuario);
        m.setNombre(nombreMazo);
        m.setDescripcion(descripcion != null ? descripcion : "");
        m.setEsPublico(esPublico);
        return mazoRepository.save(m);
    }

    public List<Mazos> obtenerMazosUsuario(Long userId) {
        return mazoRepository.findByUsuarioId(userId);
    }

    public List<Mazos> obtenerMazosPublicos() {
        return mazoRepository.findByEsPublicoTrue();
    }

    public Mazos obtenerMazoPorId(Long mazoId) {
        return mazoRepository.findById(mazoId)
                .orElseThrow(() -> new IllegalArgumentException("Mazo no encontrado."));
    }

    @Transactional
    public Mazos_Cartas agregarCartaAMazo(Long mazoId, Long cartaId, Integer cantidad) {
        Mazos mazo = mazoRepository.findById(mazoId)
                .orElseThrow(() -> new IllegalArgumentException("Mazo no encontrado."));
        Carta carta = cartaRepository.findById(cartaId)
                .orElseThrow(() -> new IllegalArgumentException("Carta no encontrada."));
        if (mazoCartaRepository.existsById_MazoIdAndId_CartaId(mazoId, cartaId))
            throw new IllegalArgumentException("La carta ya está en el mazo.");
        Mazos_Cartas mc = new Mazos_Cartas();
        mc.setMazo(mazo);
        mc.setCarta(carta);
        mc.setCantidad(cantidad != null ? cantidad : 1);
        return mazoCartaRepository.save(mc);
    }

    @Transactional
    public void eliminarCartaDeMazo(Long mazoId, Long cartaId) {
        if (!mazoCartaRepository.existsById_MazoIdAndId_CartaId(mazoId, cartaId))
            throw new IllegalArgumentException("La carta no está en el mazo.");
        mazoCartaRepository.deleteById_MazoIdAndId_CartaId(mazoId, cartaId);
    }

    public List<Mazos_Cartas> obtenerCartasDeMazo(Long mazoId) {
        return mazoCartaRepository.findById_MazoId(mazoId);
    }

    @Transactional
    public void cambiarVisibilidad(Long mazoId, boolean esPublico) {
        Mazos mazo = mazoRepository.findById(mazoId)
                .orElseThrow(() -> new IllegalArgumentException("Mazo no encontrado."));
        mazo.setEsPublico(esPublico);
        mazoRepository.save(mazo);
    }

    @Transactional
    public void eliminarMazo(Long mazoId) {
        if (!mazoRepository.existsById(mazoId))
            throw new IllegalArgumentException("Mazo no encontrado.");
        mazoRepository.deleteById(mazoId);
    }
}