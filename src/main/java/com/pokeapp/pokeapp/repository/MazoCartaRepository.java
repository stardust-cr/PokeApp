package com.pokeapp.pokeapp.repository;
import java.util.List;
import com.pokeapp.pokeapp.model.Mazos_Cartas;
import com.pokeapp.pokeapp.model.Mazos_CartasId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MazoCartaRepository extends JpaRepository<Mazos_Cartas, Mazos_CartasId> {
 
    List<Mazos_Cartas> findById_MazoId(Long mazoId);

    void deleteById_MazoIdAndId_CartaId(Long mazoId, Long cartaId);

    boolean existsById_MazoIdAndId_CartaId(Long mazoId, Long cartaId);

    void deleteByCartaId(Long cartaId);
}
