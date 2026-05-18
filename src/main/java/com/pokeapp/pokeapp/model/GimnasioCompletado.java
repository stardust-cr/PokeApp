package com.pokeapp.pokeapp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "gimnasios_completados")
public class GimnasioCompletado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private Run run;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gimnasio_id", nullable = false)
    private Gimnasios gimnasio;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    public GimnasioCompletado() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Run getRun() { return run; }
    public void setRun(Run run) { this.run = run; }

    public Gimnasios getGimnasio() { return gimnasio; }
    public void setGimnasio(Gimnasios gimnasio) { this.gimnasio = gimnasio; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
