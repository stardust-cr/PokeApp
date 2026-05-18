package com.pokeapp.pokeapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pokeapp.pokeapp.model.Juegos;
import com.pokeapp.pokeapp.repository.JuegoRepository;

@Service
public class JuegoService {

    private final JuegoRepository juegoRepository;

    public JuegoService(JuegoRepository juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    public List<Juegos> getTodosLosJuegos() {
        return juegoRepository.findAllByOrderByGeneracionAsc();
    }

    public Juegos getJuego(Long id) {
        return juegoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado: " + id));
    }
}
