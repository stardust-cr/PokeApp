package com.pokeapp.pokeapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pokeapp.pokeapp.model.EstadoPokemon;
import com.pokeapp.pokeapp.model.Pokemon;

public interface PokemonRunRepository extends JpaRepository<Pokemon, Long> {
    List<Pokemon> findByRunId(Long runId);
    List<Pokemon> findByRunIdAndEstado(Long runId, EstadoPokemon estado);
}
