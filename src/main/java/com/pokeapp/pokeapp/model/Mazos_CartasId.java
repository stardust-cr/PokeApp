package com.pokeapp.pokeapp.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class Mazos_CartasId implements Serializable {
    private Long mazoId;
    private Long cartaId;

    public Mazos_CartasId() {
    }

    public Mazos_CartasId(Long mazoId, Long cartaId) {
        this.mazoId = mazoId;
        this.cartaId = cartaId;
    }

    public Long getMazoId() {
        return mazoId;
    }

    public void setMazoId(Long mazoId) {
        this.mazoId = mazoId;
    }

    public Long getCartaId() {
        return cartaId;
    }

    public void setCartaId(Long cartaId) {
        this.cartaId = cartaId;
    }

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