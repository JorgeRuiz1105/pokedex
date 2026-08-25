package com.jorgeruiz.pokedex.service;

import com.jorgeruiz.pokedex.api.PokeApiClient;
import com.jorgeruiz.pokedex.api.PokeMapper;
import com.jorgeruiz.pokedex.model.Pokemon;
import com.jorgeruiz.pokedex.repository.PokemonRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PokemonService {

    private final PokemonRepository repository;
    private final PokeMapper mapper;
    private final PokeApiClient client;

    public PokemonService() {
        client = new PokeApiClient();
        repository = new PokemonRepository();
        mapper = new PokeMapper();
    }

    public Pokemon getAndSavePokemon(int id) throws SQLException, IOException, InterruptedException {
        Pokemon localPokemon = repository.findById(id);
        if(localPokemon != null){
            System.out.println("Loading Pokemon from local database...");
            return localPokemon;
        }

        System.out.println("Pokemon wasn't found in local databae. Fetching the API...");
        String pokemonJson = client.fetchPokemon(id);
        String speciesJson = client.fetchPokemonSpecies(id);

        Pokemon apiPokemon = mapper.mapToPokemon(pokemonJson, speciesJson);
        repository.save(apiPokemon);
        return apiPokemon;
    }

    public List<Pokemon> getAllStoredPokemonsSorted() throws SQLException {
        List<Pokemon> pokemons = repository.findAll();
        pokemons.sort(null);
        return pokemons;
    }

    public List<Pokemon> getPokemonsByType(String type) throws SQLException {
        return repository.findAll().stream()
                .filter(p -> p.getTypes().stream().anyMatch(t -> t.equalsIgnoreCase(type)))
                .collect(Collectors.toList());
    }

    public List<Pokemon> getPokemonsByRarity(String rarity) throws SQLException {
        return repository.findAll().stream()
                .filter(p -> p.getRarity().equalsIgnoreCase(rarity))
                .collect(Collectors.toList());
    }

    public Pokemon getStrongestPokemonByBST() throws SQLException {
        return repository.findAll().stream()
                .max(Comparator.comparingInt(Pokemon::calculateBST))
                .orElse(null);
    }
}
