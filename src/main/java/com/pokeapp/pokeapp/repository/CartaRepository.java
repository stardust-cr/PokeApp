package com.pokeapp.pokeapp.repository;

import java.util.List;
import com.pokeapp.pokeapp.model.Carta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartaRepository extends JpaRepository<Carta, Long> {
 
    List<Carta> findByNombreContainingIgnoreCase(String nombre);

    List<Carta> findByRareza(Carta.Rareza rareza);

    List<Carta> findBySetCodigo(String setCodigo);

    List<Carta> findByTipo(String tipo);

    java.util.Optional<Carta> findByNombreAndSetCodigo(String nombre, String setCodigo);
}
