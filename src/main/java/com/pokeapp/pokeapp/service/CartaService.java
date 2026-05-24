package com.pokeapp.pokeapp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pokeapp.pokeapp.dto.CartaDTO;
import com.pokeapp.pokeapp.model.Carta;
import com.pokeapp.pokeapp.model.Historial_Precios;

import jakarta.transaction.Transactional;

import com.pokeapp.pokeapp.repository.CartaRepository;
import com.pokeapp.pokeapp.repository.HistorialPrecioRepository;
import com.pokeapp.pokeapp.service.ResourceNotFoundException;

@Service
public class CartaService {

    private final CartaRepository cartaRepository;
    private final HistorialPrecioRepository historialPrecioRepository;

    private final String API_URL = "https://api.pokemontcg.io/v2/cards";

    public List<CartaDTO.Response> buscarCartasEnApi(String nombre) {
    RestTemplate restTemplate = new RestTemplate();
    String url = API_URL + "?q=name:\"" + nombre + "\"&pageSize=10";
    
    Map<String, Object> response = restTemplate.getForObject(url, Map.class);
    if (response == null || response.get("data") == null) return List.of();
    
    List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");

    return data.stream().map(cardMap -> {
        CartaDTO.Response dto = new CartaDTO.Response();
        dto.setNombre((String) cardMap.get("name"));
        
        Map<String, String> images = (Map<String, String>) cardMap.get("images");
        if (images != null) dto.setImagenUrl(images.get("small"));
        
        // Mapear rareza
        String rarezaTcg = (String) cardMap.get("rarity");
        Carta.Rareza rareza = Carta.Rareza.COMUN;
        if (rarezaTcg != null) {
            String r = rarezaTcg.toLowerCase();
            if (r.contains("secret") || r.contains("rainbow")) rareza = Carta.Rareza.SECRETA;
            else if (r.contains("ultra") || r.contains("vmax") || r.contains("vstar")) rareza = Carta.Rareza.ULTRA_RARA;
            else if (r.contains("rare")) rareza = Carta.Rareza.RARA;
            else if (r.contains("uncommon")) rareza = Carta.Rareza.POCO_COMUN;
        }
        dto.setRareza(rareza);
        dto.setSetCodigo((String) ((Map<?,?>)cardMap.getOrDefault("set", Map.of())).get("id"));
        
        // id = null porque aún no está en la BD, se importará al seleccionarla
        return dto;
    }).collect(Collectors.toList());
}

    public CartaService(CartaRepository cartaRepository,
                        HistorialPrecioRepository historialPrecioRepository) {
        this.cartaRepository = cartaRepository;
        this.historialPrecioRepository = historialPrecioRepository;
    }

    @Transactional
    public CartaDTO.Response crear(CartaDTO.Request request) {
        Carta carta = new Carta();
        carta.setNombre(request.getNombre());
        carta.setTipo(request.getTipo());
        carta.setRareza(request.getRareza());
        carta.setSetCodigo(request.getSetCodigo());
        carta.setNumeroCartas(request.getNumeroCartas());
        carta.setImagenUrl(request.getImagenUrl());
        carta.setPrecioBase(request.getPrecioBase());
        return toResponse(cartaRepository.save(carta));
    }

    public CartaDTO.Response obtenerPorId(Long id) {
        Carta carta = cartaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carta no encontrada: " + id));
        return toResponse(carta);
    }

    public List<CartaDTO.Response> buscarPorNombre(String nombre) {
        return cartaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Importa una carta desde la API de PokémonTCG al servidor local.
     * Si ya existe una carta con el mismo nombre y set, la devuelve sin duplicar.
     * Si no existe, la crea automáticamente.
     */
    @Transactional
    public CartaDTO.Response importarDesdeApiTcg(String nombre, String setCodigo, String rareza, String imagenUrl) {
        // Buscar si ya existe por nombre + set
        if (setCodigo != null && !setCodigo.isBlank()) {
            var existente = cartaRepository.findByNombreAndSetCodigo(nombre, setCodigo);
            if (existente.isPresent()) {
                Carta c = existente.get();
                // Actualizar imagenUrl si la carta guardada no tiene imagen pero la nueva sí
                if ((c.getImagenUrl() == null || c.getImagenUrl().isBlank())
                        && imagenUrl != null && !imagenUrl.isBlank()) {
                    c.setImagenUrl(imagenUrl);
                    cartaRepository.save(c);
                }
                return toResponse(c);
            }
        } else {
            // Sin set: buscar solo por nombre exacto
            var coincidencias = cartaRepository.findByNombreContainingIgnoreCase(nombre)
                    .stream().filter(c -> c.getNombre().equalsIgnoreCase(nombre)).toList();
            if (!coincidencias.isEmpty()) {
                Carta c = coincidencias.get(0);
                // Actualizar imagenUrl si la carta guardada no tiene imagen pero la nueva sí
                if ((c.getImagenUrl() == null || c.getImagenUrl().isBlank())
                        && imagenUrl != null && !imagenUrl.isBlank()) {
                    c.setImagenUrl(imagenUrl);
                    cartaRepository.save(c);
                }
                return toResponse(c);
            }
        }

        // No existe → crear nueva entrada
        Carta carta = new Carta();
        carta.setNombre(nombre);
        carta.setSetCodigo(setCodigo != null ? setCodigo : "");
        carta.setImagenUrl(imagenUrl);

        // Mapear rareza del texto TCG al enum local
        Carta.Rareza rarezaEnum = Carta.Rareza.COMUN;
        if (rareza != null) {
            String r = rareza.toLowerCase();
            if (r.contains("rainbow") || r.contains("secret")) rarezaEnum = Carta.Rareza.SECRETA;
            else if (r.contains("ultra") || r.contains("v") || r.contains("ex")) rarezaEnum = Carta.Rareza.ULTRA_RARA;
            else if (r.contains("rare")) rarezaEnum = Carta.Rareza.RARA;
            else if (r.contains("uncommon")) rarezaEnum = Carta.Rareza.POCO_COMUN;
        }
        carta.setRareza(rarezaEnum);

        return toResponse(cartaRepository.save(carta));
    }

    public List<CartaDTO.Response> buscarPorRareza(Carta.Rareza rareza) {
        return cartaRepository.findByRareza(rareza)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<CartaDTO.Response> buscarPorSetCodigo(String setCodigo) {
        return cartaRepository.findBySetCodigo(setCodigo)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<CartaDTO.Response> todas() {
        return cartaRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CartaDTO.Response actualizar(Long id, CartaDTO.Request request) {
        Carta carta = cartaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carta no encontrada: " + id));
        carta.setNombre(request.getNombre());
        carta.setTipo(request.getTipo());
        carta.setRareza(request.getRareza());
        carta.setSetCodigo(request.getSetCodigo());
        carta.setNumeroCartas(request.getNumeroCartas());
        carta.setImagenUrl(request.getImagenUrl());

        BigDecimal precioActual = carta.getPrecioBase();
        BigDecimal precioNuevo = request.getPrecioBase();
        boolean precioHaCambiado = precioNuevo != null
                && (precioActual == null || precioNuevo.compareTo(precioActual) != 0);

        if (precioHaCambiado) {
            Historial_Precios historial = new Historial_Precios();
            historial.setCarta(carta);
            historial.setPrecioAnterior(precioActual);
            historial.setPrecioNuevo(precioNuevo);
            historial.setFechaCambio(LocalDateTime.now());
            historialPrecioRepository.save(historial);
            carta.setPrecioBase(precioNuevo);
        }
        return toResponse(cartaRepository.save(carta));
    }

    @Transactional
    public Historial_Precios registrarPrecio(Long cartaId, BigDecimal precio, String fuente) {
        Carta carta = cartaRepository.findById(cartaId)
                .orElseThrow(() -> new ResourceNotFoundException("Carta no encontrada: " + cartaId));
        Historial_Precios historial = new Historial_Precios();
        historial.setCarta(carta);
        historial.setPrecio(precio);
        historial.setFechaCambio(LocalDateTime.now());
        historial.setFuente(fuente != null ? fuente : "TCGPlayer");
        return historialPrecioRepository.save(historial);
    }

    public List<Historial_Precios> obtenerHistorialPrecios(Long cartaId) {
        Carta carta = cartaRepository.findById(cartaId)
                .orElseThrow(() -> new ResourceNotFoundException("Carta no encontrada: " + cartaId));
        return historialPrecioRepository.findByCarta(carta);
    }

    public List<Historial_Precios> obtenerUltimoPrecioPorFuente(Long cartaId) {
        Carta carta = cartaRepository.findById(cartaId)
                .orElseThrow(() -> new ResourceNotFoundException("Carta no encontrada: " + cartaId));
        return historialPrecioRepository.findTopByCartaOrderByFechaCambioDesc(carta)
                .map(List::of).orElse(List.of());
    }

    private CartaDTO.Response toResponse(Carta carta) {
        CartaDTO.Response r = new CartaDTO.Response();
        r.setId(carta.getId());
        r.setNombre(carta.getNombre());
        r.setTipo(carta.getTipo());
        r.setRareza(carta.getRareza());
        r.setSetCodigo(carta.getSetCodigo());
        r.setNumeroCartas(carta.getNumeroCartas());
        r.setImagenUrl(carta.getImagenUrl());
        r.setPrecioBase(carta.getPrecioBase());
        return r;
    }
}