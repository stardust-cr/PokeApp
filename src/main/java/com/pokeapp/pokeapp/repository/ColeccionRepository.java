package com.pokeapp.pokeapp.repository;

import java.util.List;
import com.pokeapp.pokeapp.model.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ColeccionRepository extends JpaRepository<Coleccion, Long> {
 
    List<Coleccion> findByUsuarioId(Long usuarioId);
 
    boolean existsByUsuarioIdAndNombre(Long usuarioId, String nombre);
}
