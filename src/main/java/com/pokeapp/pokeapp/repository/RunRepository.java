package com.pokeapp.pokeapp.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pokeapp.pokeapp.model.EstadoRun;
import com.pokeapp.pokeapp.model.Run;

@Repository
public interface RunRepository extends JpaRepository<Run, Long> {
    List<Run> findByUsuarioId(Long usuarioId);
    List<Run> findByUsuarioIdAndEstado(Long usuarioId, EstadoRun estado);

}
