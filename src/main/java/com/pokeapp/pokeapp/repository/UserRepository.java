package com.pokeapp.pokeapp.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pokeapp.pokeapp.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    static Optional<User> findByUsername(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUsername'");
    }
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRol(String rol);
    long countByRol(String rol);
}