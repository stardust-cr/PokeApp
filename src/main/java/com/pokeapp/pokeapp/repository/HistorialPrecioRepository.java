package com.pokeapp.pokeapp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.pokeapp.pokeapp.model.Carta;
import com.pokeapp.pokeapp.model.Historial_Precios;

@Repository
public interface HistorialPrecioRepository extends JpaRepository<Historial_Precios, Long> {

    // FIX: "fecha" no existe en el modelo → el campo se llama "fechaCambio"
    List<Historial_Precios> findByCartaIdOrderByFechaCambioDesc(Long cartaId);

    @Query("SELECT h FROM Historial_Precios h WHERE h.carta.id = :cartaId ORDER BY h.fechaCambio DESC LIMIT 1")
    Optional<Historial_Precios> findUltimoPrecioByCarta(@Param("cartaId") Long cartaId);

    List<Historial_Precios> findByCarta(Carta carta);

    Optional<Historial_Precios> findTopByCartaOrderByFechaCambioDesc(Carta carta);

    void deleteByCartaId(Long cartaId);
}