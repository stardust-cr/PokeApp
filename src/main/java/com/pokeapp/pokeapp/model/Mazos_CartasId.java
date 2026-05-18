package com.pokeapp.pokeapp.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Mazos_CartasId implements Serializable {
    private Long mazoId;
    private Long cartaId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mazos_CartasId that = (Mazos_CartasId) o;
        return Objects.equals(mazoId, that.mazoId) && Objects.equals(cartaId, that.cartaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mazoId, cartaId);
    }
}