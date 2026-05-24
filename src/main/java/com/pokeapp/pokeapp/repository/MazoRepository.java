package com.pokeapp.pokeapp.repository;
import java.util.List;
import com.pokeapp.pokeapp.model.Mazos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MazoRepository extends JpaRepository<Mazos, Long> {
 
    List<Mazos> findByUsuarioId(Long usuarioId);
 
    // Mazos públicos de todos los usuarios
    List<Mazos> findByEsPublicoTrue();

    // Buscar mazos públicos por nombre (contiene, case-insensitive)
    List<Mazos> findByEsPublicoTrueAndNombreContainingIgnoreCase(String nombre);
 
    // Mazos públicos de un usuario concreto
    List<Mazos> findByUsuarioIdAndEsPublicoTrue(Long usuarioId);
}
