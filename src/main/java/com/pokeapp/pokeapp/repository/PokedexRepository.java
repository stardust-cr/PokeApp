package com.pokeapp.pokeapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pokeapp.pokeapp.model.Pokedex;

public interface PokedexRepository extends JpaRepository<Pokedex, Long> {
    List<Pokedex> findByNombreContainingIgnoreCase(String nombre);
    List<Pokedex> findAllByOrderByNumeroAsc();
}
