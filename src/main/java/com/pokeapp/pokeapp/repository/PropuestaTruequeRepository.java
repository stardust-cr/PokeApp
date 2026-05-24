package com.pokeapp.pokeapp.repository;

import com.pokeapp.pokeapp.model.PropuestaTrueque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropuestaTruequeRepository extends JpaRepository<PropuestaTrueque, Long> {

    // Todas las propuestas sobre una venta (para el vendedor)
    List<PropuestaTrueque> findByVentaId(Long ventaId);

    // Propuestas enviadas por un usuario
    List<PropuestaTrueque> findByProponenteId(Long proponenteId);

    // Propuestas pendientes recibidas para las ventas de un vendedor
    List<PropuestaTrueque> findByVenta_Vendedor_IdAndEstado(Long vendedorId, PropuestaTrueque.EstadoPropuesta estado);

    // Contar pendientes para el badge de notificaciones
    long countByVenta_Vendedor_IdAndEstado(Long vendedorId, PropuestaTrueque.EstadoPropuesta estado);

    List<PropuestaTrueque> findByVenta_Vendedor_Id(Long vendedorId);
}

