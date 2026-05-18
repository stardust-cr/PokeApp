package com.pokeapp.pokeapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pokeapp.pokeapp.model.EstadoPokemon;
import com.pokeapp.pokeapp.model.EstadoRun;
import com.pokeapp.pokeapp.model.Run;
import com.pokeapp.pokeapp.repository.PokedexRepository;
import com.pokeapp.pokeapp.service.JuegoService;
import com.pokeapp.pokeapp.service.NuzlockeService;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/web/nuzlocke")
public class NuzlockeController {

    private final NuzlockeService nuzlockeService;
    private final JuegoService juegoService;
    private final PokedexRepository pokedexRepository;

    public NuzlockeController(NuzlockeService nuzlockeService, JuegoService juegoService,
                              PokedexRepository pokedexRepository) {
        this.nuzlockeService = nuzlockeService;
        this.juegoService = juegoService;
        this.pokedexRepository = pokedexRepository;
    }

    private String getUsername(HttpSession session) {
        return (String) session.getAttribute("username");
    }

    // ── Listado de runs ──────────────────────────────────────
    @GetMapping
    public String listarRuns(HttpSession session, Model model) {
        String username = getUsername(session);
        if (username == null) return "redirect:/web/login";
        model.addAttribute("runs", nuzlockeService.getRunDeUsuario(username));
        model.addAttribute("juegos", juegoService.getTodosLosJuegos());
        return "nuzlocke";
    }

    // ── Crear run ────────────────────────────────────────────
    @PostMapping("/crear")
    public String crearRun(@RequestParam Long juegoId, HttpSession session) {
        String username = getUsername(session);
        if (username == null) return "redirect:/web/login";
        Run run = nuzlockeService.crearRun(username, juegoId);
        return "redirect:/web/nuzlocke/" + run.getId();
    }

    // ── Detalle de una run ───────────────────────────────────
    @GetMapping("/{runId}")
    public String detalleRun(@PathVariable Long runId, HttpSession session, Model model) {
        String username = getUsername(session);
        if (username == null) return "redirect:/web/login";

        Run run = nuzlockeService.getRun(runId);
        Long juegoId = run.getJuego().getId();

        model.addAttribute("run", run);
        model.addAttribute("pokemonVivos",   nuzlockeService.getPokemonesVivosDeRun(runId));
        model.addAttribute("pokemonMuertos", nuzlockeService.getPokemonesMuertosDeRun(runId));
        model.addAttribute("pokemonCaja",    nuzlockeService.getPokemonesDeRun(runId)
                .stream().filter(p -> p.getEstado() == EstadoPokemon.CAJA).toList());
        model.addAttribute("gimnasios",      nuzlockeService.getGimnasiosDeRun(juegoId));
        model.addAttribute("completados",    nuzlockeService.getIdsGimnasiosCompletados(runId));
        return "nuzlocke-detalle";
    }

    // ── API: buscar en Pokédex ───────────────────────────────
    @GetMapping("/api/pokedex/buscar")
    @ResponseBody
    public ResponseEntity<?> buscarEnPokedex(@RequestParam String nombre) {
        var resultado = pokedexRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(p -> Map.of("id", p.getId(), "numero", p.getNumero(), "nombre", p.getNombre()))
                .toList();
        return ResponseEntity.ok(resultado);
    }

    // ── Finalizar / eliminar run ─────────────────────────────
    @PostMapping("/{runId}/finalizar")
    public String finalizarRun(@PathVariable Long runId,
                               @RequestParam EstadoRun estado,
                               HttpSession session) {
        if (getUsername(session) == null) return "redirect:/web/login";
        nuzlockeService.finalizarRun(runId, estado);
        return "redirect:/web/nuzlocke";
    }

    @PostMapping("/{runId}/eliminar")
    public String eliminarRun(@PathVariable Long runId, HttpSession session) {
        if (getUsername(session) == null) return "redirect:/web/login";
        nuzlockeService.eliminarRun(runId);
        return "redirect:/web/nuzlocke";
    }

    // ── Pokémon ──────────────────────────────────────────────
    @PostMapping("/{runId}/pokemon/capturar")
    public String capturarPokemon(@PathVariable Long runId,
                                  @RequestParam Long especieId,
                                  @RequestParam(required = false) String apodo,
                                  @RequestParam(required = false) Integer nivel,
                                  @RequestParam(required = false) String ruta,
                                  HttpSession session) {
        if (getUsername(session) == null) return "redirect:/web/login";
        nuzlockeService.capturarPokemon(runId, especieId, apodo, nivel, ruta);
        return "redirect:/web/nuzlocke/" + runId;
    }

    @PostMapping("/{runId}/pokemon/{pokemonId}/matar")
    public String matarPokemon(@PathVariable Long runId,
                               @PathVariable Long pokemonId,
                               @RequestParam(required = false) Integer nivelMuerte,
                               @RequestParam(required = false) String causaMuerte,
                               HttpSession session) {
        if (getUsername(session) == null) return "redirect:/web/login";
        nuzlockeService.marcarPokemonComoMuerto(pokemonId, nivelMuerte, causaMuerte);
        return "redirect:/web/nuzlocke/" + runId;
    }

    @PostMapping("/{runId}/pokemon/{pokemonId}/estado")
    public String actualizarEstadoPokemon(@PathVariable Long runId,
                                          @PathVariable Long pokemonId,
                                          @RequestParam EstadoPokemon estado,
                                          HttpSession session) {
        if (getUsername(session) == null) return "redirect:/web/login";
        nuzlockeService.cambiarEstadoPokemon(pokemonId, estado);
        return "redirect:/web/nuzlocke/" + runId;
    }

    @PostMapping("/{runId}/pokemon/{pokemonId}/eliminar")
    public String eliminarPokemon(@PathVariable Long runId,
                                  @PathVariable Long pokemonId,
                                  HttpSession session) {
        if (getUsername(session) == null) return "redirect:/web/login";
        nuzlockeService.eliminarPokemon(pokemonId);
        return "redirect:/web/nuzlocke/" + runId;
    }

    // ── Gimnasios ────────────────────────────────────────────
    @PostMapping("/{runId}/gimnasio/{gimnasioId}/completar")
    public String completarGimnasio(@PathVariable Long runId,
                                    @PathVariable Long gimnasioId,
                                    HttpSession session) {
        if (getUsername(session) == null) return "redirect:/web/login";
        nuzlockeService.completarGimnasio(runId, gimnasioId);
        return "redirect:/web/nuzlocke/" + runId;
    }

    @PostMapping("/{runId}/gimnasio/{gimnasioId}/descompletar")
    public String descompletarGimnasio(@PathVariable Long runId,
                                       @PathVariable Long gimnasioId,
                                       HttpSession session) {
        if (getUsername(session) == null) return "redirect:/web/login";
        nuzlockeService.descompletarGimnasio(runId, gimnasioId);
        return "redirect:/web/nuzlocke/" + runId;
    }
}