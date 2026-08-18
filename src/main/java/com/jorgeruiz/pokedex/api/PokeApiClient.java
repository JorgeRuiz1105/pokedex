package com.jorgeruiz.pokedex.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PokeApiClient {

    private final HttpClient client;
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    public PokeApiClient() {
        this.client = HttpClient.newHttpClient();
    }

    private String fetchFromUrl(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != 200){
            throw new IOException("HTTP Error: " + response.statusCode());
        }

        return response.body();
    }

    public String fetchPokemon(int id) throws IOException, InterruptedException {
        return fetchFromUrl(BASE_URL + "pokemon/" + id);
    }

    public String fetchPokemonSpecies(int id) throws IOException, InterruptedException {
        return fetchFromUrl(BASE_URL + "pokemon-species/" + id);
    }

    public String fetchEvolutionChain(String url) throws IOException, InterruptedException {
        return fetchFromUrl(url);
    }
}
