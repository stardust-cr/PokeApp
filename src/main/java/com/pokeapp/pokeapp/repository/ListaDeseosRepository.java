package com.pokeapp.pokeapp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.pokeapp.pokeapp.model.Listadeseos;

@Repository
// FIX: faltaba "extends JpaRepository<Listadeseos, Long>" — los tipos genéricos eran texto plano
public interface ListaDeseosRepository extends JpaRepository<Listadeseos, Long> {

    List<Listadeseos> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndCartaId(Long usuarioId, Long cartaId);

    void deleteByUsuarioIdAndCartaId(Long usuarioId, Long cartaId);

    @Query("""
        SELECT ld FROM Listadeseos ld
        JOIN Venta v ON v.carta.id = ld.carta.id
        WHERE ld.alertaActiva = true
          AND v.estado = 'PENDIENTE'
          AND (ld.precioObjetivo IS NULL OR v.precio <= ld.precioObjetivo)
    """)
    List<Listadeseos> findDeseosConOfertaDisponible();
}
