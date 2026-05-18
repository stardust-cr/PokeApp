package com.pokeapp.pokeapp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.pokeapp.pokeapp.model.Valoracion;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    List<Valoracion> findByVentaId(Long ventaId);

    @Query("SELECT val FROM Valoracion val JOIN val.venta v WHERE v.vendedor.id = :vendedorId")
    List<Valoracion> findByVendedorId(@Param("vendedorId") Long vendedorId);

    // FIX: devuelve boolean (no List) + campo correcto es "usuario" (según modelo Valoracion)
    boolean existsByVentaIdAndUsuarioId(Long ventaId, Long usuarioId);
}
