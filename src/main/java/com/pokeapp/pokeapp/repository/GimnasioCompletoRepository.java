package com.pokeapp.pokeapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pokeapp.pokeapp.model.GimnasioCompletado;

public interface GimnasioCompletoRepository extends JpaRepository<GimnasioCompletado, Long> {
    List<GimnasioCompletado> findByRunId(Long runId);
    boolean existsByRunIdAndGimnasioId(Long runId, Long gimnasioId);
}
