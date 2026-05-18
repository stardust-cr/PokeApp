package com.pokeapp.pokeapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokeapp.pokeapp.model.Pokedex;
import com.pokeapp.pokeapp.repository.PokedexRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class PokedexInitializer implements ApplicationRunner {

    private final PokedexRepository pokedexRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // Cambia este número si quieres más o menos Pokémon
    private static final int TOTAL_POKEMON = 1025;

    public PokedexInitializer(PokedexRepository pokedexRepository) {
        this.pokedexRepository = pokedexRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (pokedexRepository.count() >= TOTAL_POKEMON) {
            System.out.println("[Pokédex] Ya está poblada, saltando inicialización.");
            return;
        }

        System.out.println("[Pokédex] Poblando desde PokéAPI...");

        for (int i = 1; i <= TOTAL_POKEMON; i++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://pokeapi.co/api/v2/pokemon/" + i))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                JsonNode pokemon = objectMapper.readTree(response.body());

                // Nombre
                String nombre = capitalize(pokemon.get("name").asText());

                // Tipos
                JsonNode tipos = pokemon.get("types");
                String tipo1 = capitalize(tipos.get(0).get("type").get("name").asText());
                String tipo2 = tipos.size() > 1
                        ? capitalize(tipos.get(1).get("type").get("name").asText())
                        : null;

                // Imagen (sprite oficial)
                String imagenUrl = pokemon
                        .get("sprites")
                        .get("other")
                        .get("official-artwork")
                        .get("front_default").asText();

                // Guardar
                Pokedex p = new Pokedex();
                p.setNumero(i);
                p.setNombre(nombre);
                p.setTipo1(traducirTipo(tipo1));
                p.setTipo2(tipo2 != null ? traducirTipo(tipo2) : null);
                p.setImagenUrl(imagenUrl);

                pokedexRepository.save(p);
                System.out.println("[Pokédex] " + i + "/1025 - " + nombre);

            } catch (Exception e) {
                System.err.println("[Pokédex] Error en Pokémon #" + i + ": " + e.getMessage());
            }
        }

        System.out.println("[Pokédex] ¡Inicialización completada!");
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private String traducirTipo(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "fire"     -> "Fuego";
            case "water"    -> "Agua";
            case "grass"    -> "Planta";
            case "electric" -> "Eléctrico";
            case "ice"      -> "Hielo";
            case "fighting" -> "Lucha";
            case "poison"   -> "Veneno";
            case "ground"   -> "Tierra";
            case "flying"   -> "Volador";
            case "psychic"  -> "Psíquico";
            case "bug"      -> "Bicho";
            case "rock"     -> "Roca";
            case "ghost"    -> "Fantasma";
            case "dragon"   -> "Dragón";
            case "dark"     -> "Siniestro";
            case "steel"    -> "Acero";
            case "fairy"    -> "Hada";
            default         -> capitalize(tipo);
        };
    }
}
