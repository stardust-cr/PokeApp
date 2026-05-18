package com.pokeapp.pokeapp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pokeapp.pokeapp.model.EstadoPokemon;
import com.pokeapp.pokeapp.model.EstadoRun;
import com.pokeapp.pokeapp.model.GimnasioCompletado;
import com.pokeapp.pokeapp.model.Gimnasios;
import com.pokeapp.pokeapp.model.Juegos;
import com.pokeapp.pokeapp.model.Pokedex;
import com.pokeapp.pokeapp.model.Pokemon;
import com.pokeapp.pokeapp.model.Run;
import com.pokeapp.pokeapp.model.User;
import com.pokeapp.pokeapp.repository.GimnasioCompletoRepository;
import com.pokeapp.pokeapp.repository.GimnasioRepository;
import com.pokeapp.pokeapp.repository.JuegoRepository;
import com.pokeapp.pokeapp.repository.PokedexRepository;
import com.pokeapp.pokeapp.repository.PokemonRunRepository;
import com.pokeapp.pokeapp.repository.RunRepository;
import com.pokeapp.pokeapp.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class NuzlockeService {
    private final RunRepository runRepository;
    private final GimnasioCompletoRepository gimnasioCompletoRepository;
    private final GimnasioRepository gimnasioRepository;
    private final PokemonRunRepository pokemonRunRepository;
    private final JuegoRepository juegoRepository;
    private final UserRepository userRepository;
    private final PokedexRepository pokedexRepository;

    public NuzlockeService(RunRepository runRepository,
                           GimnasioCompletoRepository gimnasioCompletoRepository,
                           GimnasioRepository gimnasioRepository,
                           PokemonRunRepository pokemonRunRepository,
                           JuegoRepository juegoRepository,
                           UserRepository userRepository,
                           PokedexRepository pokedexRepository) {
        this.runRepository = runRepository;
        this.gimnasioCompletoRepository = gimnasioCompletoRepository;
        this.gimnasioRepository = gimnasioRepository;
        this.pokemonRunRepository = pokemonRunRepository;
        this.juegoRepository = juegoRepository;
        this.userRepository = userRepository;
        this.pokedexRepository = pokedexRepository;
    }

    public List<Run> getRunDeUsuario(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        return runRepository.findByUsuarioId(user.getId());
    }

    public Run getRun(Long runId) {
        return runRepository.findById(runId)
                .orElseThrow(() -> new RuntimeException("Run no encontrado: " + runId));
    }

    @Transactional
    public Run crearRun(String username, Long juegoId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        Juegos juego = juegoRepository.findById(juegoId)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado: " + juegoId));

        Run run = new Run();
        run.setUsuario(user);
        run.setJuego(juego);
        run.setFechaInicio(LocalDateTime.now());
        return runRepository.save(run);
    }

    @Transactional
    public Run finalizarRun(Long runId, EstadoRun nuevoEstado) {
        Run run = runRepository.findById(runId)
                .orElseThrow(() -> new RuntimeException("Run no encontrado: " + runId));
        run.setEstado(nuevoEstado);
        run.setFechaFin(LocalDateTime.now());
        return runRepository.save(run);
    }

    @Transactional
    public void eliminarRun(Long runId) {
        // Primero borrar dependientes, luego la run
        pokemonRunRepository.deleteAll(pokemonRunRepository.findByRunId(runId));
        gimnasioCompletoRepository.deleteAll(gimnasioCompletoRepository.findByRunId(runId));
        runRepository.deleteById(runId);
    }

    public List<Pokemon> getPokemonesDeRun(Long runId) {
        return pokemonRunRepository.findByRunId(runId);
    }

    public List<Pokemon> getPokemonesVivosDeRun(Long runId) {
        return pokemonRunRepository.findByRunIdAndEstado(runId, EstadoPokemon.VIVO);
    }

    public List<Pokemon> getPokemonesMuertosDeRun(Long runId) {
        return pokemonRunRepository.findByRunIdAndEstado(runId, EstadoPokemon.MUERTO);
    }

    @Transactional
    public Pokemon capturarPokemon(Long runId, Long especieId, String apodo,
                                   Integer nivelCaptura, String rutaCaptura) {
        Run run = runRepository.findById(runId)
                .orElseThrow(() -> new RuntimeException("Run no encontrado: " + runId));
        Pokedex especie = pokedexRepository.findById(especieId)
                .orElseThrow(() -> new RuntimeException("Especie no encontrada: " + especieId));

        Pokemon pokemon = new Pokemon();
        pokemon.setRun(run);
        pokemon.setEspecie(especie);
        pokemon.setApodo(apodo != null && !apodo.isBlank() ? apodo : especie.getNombre());
        pokemon.setNivel_captura(nivelCaptura);
        pokemon.setRuta_captura(rutaCaptura);
        pokemon.setEstado(EstadoPokemon.VIVO);
        return pokemonRunRepository.save(pokemon);
    }

    @Transactional
    public Pokemon marcarPokemonComoMuerto(Long pokemonId, Integer nivelMuerte, String causaMuerte) {
        Pokemon pokemon = pokemonRunRepository.findById(pokemonId)
                .orElseThrow(() -> new RuntimeException("Pokemon no encontrado: " + pokemonId));
        pokemon.setEstado(EstadoPokemon.MUERTO);
        pokemon.setNivel_muerte(nivelMuerte);
        pokemon.setCausa_muerte(causaMuerte);
        return pokemonRunRepository.save(pokemon);
    }

    @Transactional
    public Pokemon cambiarEstadoPokemon(Long pokemonId, EstadoPokemon nuevoEstado) {
        Pokemon pokemon = pokemonRunRepository.findById(pokemonId)
                .orElseThrow(() -> new RuntimeException("Pokemon no encontrado: " + pokemonId));
        pokemon.setEstado(nuevoEstado);
        return pokemonRunRepository.save(pokemon);
    }

    @Transactional
    public void eliminarPokemon(Long pokemonId) {
        pokemonRunRepository.deleteById(pokemonId);
    }

    public List<Gimnasios> getGimnasiosDeRun(Long juegoId) {
        return gimnasioRepository.findByJuegoIdOrderByOrdenAsc(juegoId);
    }

    public List<GimnasioCompletado> getGimnasiosCompletadosDeRun(Long runId) {
        return gimnasioCompletoRepository.findByRunId(runId);
    }

    public List<Long> getIdsGimnasiosCompletados(Long runId) {
        return gimnasioCompletoRepository.findByRunId(runId).stream()
                .map(gc -> gc.getGimnasio().getId())
                .toList();
    }

    @Transactional
    public GimnasioCompletado completarGimnasio(Long runId, Long gimnasioId) {
        if (gimnasioCompletoRepository.existsByRunIdAndGimnasioId(runId, gimnasioId)) {
            throw new RuntimeException("Gimnasio ya completado en esta run: " + gimnasioId);
        }
        Run run = runRepository.findById(runId)
                .orElseThrow(() -> new RuntimeException("Run no encontrado: " + runId));
        Gimnasios gimnasio = gimnasioRepository.findById(gimnasioId)
                .orElseThrow(() -> new RuntimeException("Gimnasio no encontrado: " + gimnasioId));

        GimnasioCompletado gc = new GimnasioCompletado();
        gc.setRun(run);
        gc.setGimnasio(gimnasio);
        return gimnasioCompletoRepository.save(gc);
    }

    @Transactional
    public void descompletarGimnasio(Long runId, Long gimnasioId) {
        gimnasioCompletoRepository.findByRunId(runId).stream()
                .filter(gc -> gc.getGimnasio().getId().equals(gimnasioId))
                .findFirst()
                .ifPresent(gc -> gimnasioCompletoRepository.deleteById(gc.getId()));
    }
}
