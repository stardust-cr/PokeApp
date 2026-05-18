package com.pokeapp.pokeapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pokeapp.pokeapp.model.Gimnasios;

@Repository
public interface GimnasioRepository extends JpaRepository<Gimnasios, Long> {
    List<Gimnasios> findByJuegoIdOrderByOrdenAsc(Long juegoId);
}