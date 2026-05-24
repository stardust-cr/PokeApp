package com.pokeapp.pokeapp.service;

import com.pokeapp.pokeapp.model.Carta;
import com.pokeapp.pokeapp.model.Coleccion;
import com.pokeapp.pokeapp.model.ColeccionCarta;
import com.pokeapp.pokeapp.model.Mazos;
import com.pokeapp.pokeapp.model.Mazos_Cartas;
import com.pokeapp.pokeapp.model.Mazos_CartasId;
import com.pokeapp.pokeapp.model.User;
import com.pokeapp.pokeapp.repository.CartaRepository;
import com.pokeapp.pokeapp.repository.ColeccionRepository;
import com.pokeapp.pokeapp.repository.MazoCartaRepository;
import com.pokeapp.pokeapp.repository.MazoRepository;
import com.pokeapp.pokeapp.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class ColeccionMazoService {

    private final MazoRepository mazoRepository;
    private final MazoCartaRepository mazoCartaRepository;
    private final CartaRepository cartaRepository;
    private final ColeccionRepository coleccionRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public ColeccionMazoService(MazoRepository mazoRepository,
                                MazoCartaRepository mazoCartaRepository,
                                CartaRepository cartaRepository,
                                ColeccionRepository coleccionRepository,
                                UserRepository userRepository,
                                EntityManager entityManager) {
        this.mazoRepository = mazoRepository;
        this.mazoCartaRepository = mazoCartaRepository;
        this.cartaRepository = cartaRepository;
        this.coleccionRepository = coleccionRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    public List<Mazos> obtenerMazosUsuario(Long usuarioId) {
        return mazoRepository.findByUsuarioId(usuarioId);
    }

    public List<Mazos> obtenerMazosPublicos() {
        return mazoRepository.findByEsPublicoTrue();
    }

    public Mazos crearMazo(Long usuarioId, String nombre, String descripcion, boolean esPublico) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Mazos mazo = new Mazos();
        mazo.setUsuario(usuario);
        mazo.setNombre(nombre);
        mazo.setDescripcion(descripcion);
        mazo.setEsPublico(esPublico);
        return mazoRepository.save(mazo);
    }

    public void eliminarMazo(Long mazoId) {
        if (!mazoRepository.existsById(mazoId)) {
            throw new IllegalArgumentException("Mazo no encontrado");
        }
        mazoRepository.deleteById(mazoId);
    }

    public List<Mazos_Cartas> obtenerCartasDeMazo(Long mazoId) {
        return mazoCartaRepository.findById_MazoId(mazoId);
    }

    public Mazos_Cartas agregarCartaAMazo(Long mazoId, Long cartaId, Integer cantidad) {
        Mazos mazo = mazoRepository.findById(mazoId)
                .orElseThrow(() -> new IllegalArgumentException("Mazo no encontrado"));
        Carta carta = cartaRepository.findById(cartaId)
                .orElseThrow(() -> new IllegalArgumentException("Carta no encontrada"));
        List<Mazos_Cartas> existentes = mazoCartaRepository.findById_MazoId(mazoId);
        for (Mazos_Cartas mc : existentes) {
            if (Objects.equals(mc.getCarta().getId(), cartaId)) {
                mc.setCantidad(mc.getCantidad() + (cantidad != null ? cantidad : 1));
                return mazoCartaRepository.save(mc);
            }
        }
        Mazos_Cartas mc = new Mazos_Cartas();
        Mazos_CartasId id = new Mazos_CartasId();
        id.setMazoId(mazoId);
        id.setCartaId(cartaId);
        mc.setId(id);
        mc.setMazo(mazo);
        mc.setCarta(carta);
        mc.setCantidad(cantidad != null ? cantidad : 1);
        return mazoCartaRepository.save(mc);
    }

    public void eliminarCartaDeMazo(Long mazoId, Long cartaId) {
        if (!mazoCartaRepository.existsById_MazoIdAndId_CartaId(mazoId, cartaId)) {
            throw new IllegalArgumentException("La carta no existe en el mazo");
        }
        mazoCartaRepository.deleteById_MazoIdAndId_CartaId(mazoId, cartaId);
    }

    public void cambiarVisibilidad(Long mazoId, boolean esPublico) {
        Mazos mazo = mazoRepository.findById(mazoId)
                .orElseThrow(() -> new IllegalArgumentException("Mazo no encontrado"));
        mazo.setEsPublico(esPublico);
        mazoRepository.save(mazo);
    }

    public List<Coleccion> obtenerColeccionesUsuario(Long usuarioId) {
        return coleccionRepository.findByUsuarioId(usuarioId);
    }

    public Coleccion crearColeccion(Long usuarioId, String nombre) {
        if (coleccionRepository.existsByUsuarioIdAndNombre(usuarioId, nombre)) {
            throw new IllegalArgumentException("Ya existe una colección con ese nombre");
        }
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Coleccion coleccion = new Coleccion();
        coleccion.setUsuario(usuario);
        coleccion.setNombre(nombre);
        return coleccionRepository.save(coleccion);
    }

    public void eliminarColeccion(Long coleccionId) {
        if (!coleccionRepository.existsById(coleccionId)) {
            throw new IllegalArgumentException("Colección no encontrada");
        }
        coleccionRepository.deleteById(coleccionId);
    }

    public List<ColeccionCarta> obtenerCartasDeColeccion(Long coleccionId) {
        TypedQuery<ColeccionCarta> query = entityManager.createQuery(
                "SELECT cc FROM ColeccionCarta cc WHERE cc.coleccion.id = :coleccionId",
                ColeccionCarta.class);
        return query.setParameter("coleccionId", coleccionId).getResultList();
    }

    public ColeccionCarta agregarCartaAColeccion(Long coleccionId, Long cartaId, Integer cantidad) {
        Coleccion coleccion = coleccionRepository.findById(coleccionId)
                .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));
        Carta carta = cartaRepository.findById(cartaId)
                .orElseThrow(() -> new IllegalArgumentException("Carta no encontrada"));
        TypedQuery<ColeccionCarta> existingQuery = entityManager.createQuery(
                "SELECT cc FROM ColeccionCarta cc WHERE cc.coleccion.id = :coleccionId AND cc.carta.id = :cartaId",
                ColeccionCarta.class);
        existingQuery.setParameter("coleccionId", coleccionId);
        existingQuery.setParameter("cartaId", cartaId);
        List<ColeccionCarta> existentes = existingQuery.getResultList();
        if (!existentes.isEmpty()) {
            ColeccionCarta cc = existentes.get(0);
            cc.setCantidad(cc.getCantidad() + (cantidad != null ? cantidad : 1));
            return entityManager.merge(cc);
        }
        ColeccionCarta coleccionCarta = new ColeccionCarta();
        coleccionCarta.setColeccion(coleccion);
        coleccionCarta.setCarta(carta);
        coleccionCarta.setCantidad(cantidad != null ? cantidad : 1);
        entityManager.persist(coleccionCarta);
        return coleccionCarta;
    }

    public void eliminarCartaDeColeccion(Long coleccionId, Long cartaId) {
        int deleted = entityManager.createQuery(
                "DELETE FROM ColeccionCarta cc WHERE cc.coleccion.id = :coleccionId AND cc.carta.id = :cartaId")
                .setParameter("coleccionId", coleccionId)
                .setParameter("cartaId", cartaId)
                .executeUpdate();
        if (deleted == 0) {
            throw new IllegalArgumentException("La carta no existe en la colección");
        }
    }

    public List<Carta> obtenerCartasDeUsuario(Long usuarioId) {
        TypedQuery<Carta> query = entityManager.createQuery(
                "SELECT DISTINCT cc.carta FROM ColeccionCarta cc WHERE cc.coleccion.usuario.id = :usuarioId",
                Carta.class);
        List<Carta> cartasDeColecciones = query.setParameter("usuarioId", usuarioId).getResultList();
        List<Carta> cartasDeMazos = mazoRepository.findByUsuarioId(usuarioId)
                .stream()
                .flatMap(m -> m.getCartas().stream())
                .map(Mazos_Cartas::getCarta)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<Carta> todasCartas = new ArrayList<>(cartasDeColecciones);
        todasCartas.addAll(cartasDeMazos);
        return todasCartas.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }
}
