package com.pokeapp.pokeapp.repository;

import com.pokeapp.pokeapp.model.ColeccionCarta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ColeccionCartaRepository extends JpaRepository<ColeccionCarta, Long> {

    List<ColeccionCarta> findByColeccionId(Long coleccionId);

    boolean existsByColeccionIdAndCartaId(Long coleccionId, Long cartaId);

    void deleteByColeccionIdAndCartaId(Long coleccionId, Long cartaId);

    void deleteByCartaId(Long cartaId);

    // Todas las cartas distintas que un usuario tiene en cualquier colección
    @Query("SELECT DISTINCT cc.carta FROM ColeccionCarta cc WHERE cc.coleccion.usuario.id = :usuarioId")
    List<com.pokeapp.pokeapp.model.Carta> findCartasByUsuarioId(Long usuarioId);
}
