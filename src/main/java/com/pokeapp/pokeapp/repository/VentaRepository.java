package com.pokeapp.pokeapp.repository;

import com.pokeapp.pokeapp.model.Venta;
import com.pokeapp.pokeapp.model.Venta.EstadoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByVendedorId(Long vendedorId);
    List<Venta> findByCompradorId(Long compradorId);
    List<Venta> findByEstado(EstadoVenta estado);
    List<Venta> findByTipoAndEstado(Venta.TipoVenta tipo, Venta.EstadoVenta estado);
    List<Venta> findByVendedorIdOrCompradorId(Long userId, Long userId2);

    @Query("SELECT v FROM Venta v WHERE v.carta.id = :cartaId AND v.estado = 'PENDIENTE'")
    List<Venta> findActivasByCartaId(@Param("cartaId") Long cartaId);

    @Query("SELECT v.carta.nombre, COUNT(v) as total FROM Venta v WHERE v.estado = 'COMPLETADA' GROUP BY v.carta.nombre ORDER BY total DESC")
    List<Object[]> findCartasMasVendidas();

    @Query("SELECT v.vendedor.username, COUNT(v) as total FROM Venta v WHERE v.estado = 'COMPLETADA' GROUP BY v.vendedor.username ORDER BY total DESC")
    List<Object[]> findUsuariosMasActivos();
}