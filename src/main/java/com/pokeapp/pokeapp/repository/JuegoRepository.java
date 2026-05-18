package com.pokeapp.pokeapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pokeapp.pokeapp.model.Juegos;

@Repository
public interface JuegoRepository extends JpaRepository<Juegos, Long> {
    List<Juegos> findByRegion(String region);
    Optional<Juegos> findByNombre(String nombre);
    List<Juegos> findByGeneracionOrderByNombreAsc(Integer generacion);
    List<Juegos> findAllByOrderByGeneracionAsc();
}