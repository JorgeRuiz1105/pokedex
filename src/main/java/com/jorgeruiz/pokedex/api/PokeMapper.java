package com.jorgeruiz.pokedex.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jorgeruiz.pokedex.model.LegendaryPokemon;
import com.jorgeruiz.pokedex.model.MythicalPokemon;
import com.jorgeruiz.pokedex.model.Pokemon;
import com.jorgeruiz.pokedex.model.RegularPokemon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PokeMapper {
    private final ObjectMapper objectMapper;
    private final PokeApiClient pokeApiClient;

    public PokeMapper() {
        this.objectMapper = new ObjectMapper();
        this.pokeApiClient = new PokeApiClient();
    }

    public Pokemon mapToPokemon(String pokemonData, String speciesData) throws IOException, InterruptedException {
        JsonNode pokemonNode = objectMapper.readTree(pokemonData);
        JsonNode speciesNode = objectMapper.readTree(speciesData);

        boolean isLegendary = speciesNode.get("is_legendary").asBoolean();
        boolean isMythical = speciesNode.get("is_mythical").asBoolean();

        int id = pokemonNode.get("id").asInt();
        String name = pokemonNode.get("name").asText();
        String img = pokemonNode.has("sprites") && pokemonNode.get("sprites").has("front_default")
                ? pokemonNode.get("sprites").get("front_default").asText() : "";

        List<String> types = new ArrayList<>();
        JsonNode typesArray = pokemonNode.get("types");
        if(typesArray != null && typesArray.isArray()){
            for(JsonNode node : typesArray){
                String typeName = node.get("type").get("name").asText();
                types.add(typeName);
            }
        }

        JsonNode statsArray = pokemonNode.get("stats");

        int hp = statsArray.get(0).get("base_stat").asInt();
        int atk = statsArray.get(1).get("base_stat").asInt();
        int def = statsArray.get(2).get("base_stat").asInt();
        int spAtk = statsArray.get(3).get("base_stat").asInt();
        int spDef = statsArray.get(4).get("base_stat").asInt();
        int spd = statsArray.get(5).get("base_stat").asInt();

        if(isLegendary){
            return new LegendaryPokemon(id,name,types,img,hp,atk,def,spAtk,spDef,spd);
        } else if(isMythical){
            return new MythicalPokemon(id,name,types,img,hp,atk,def,spAtk,spDef,spd);
        } else{
            String habitat = speciesNode.has("habitat") && !speciesNode.get("habitat").isNull()
                    ? speciesNode.get("habitat").get("name").asText() : "unknown";
            String evoChainUrl = speciesNode.get("evolution_chain").get("url").asText();

            String evoChainJson = pokeApiClient.fetchEvolutionChain(evoChainUrl);
            JsonNode evoNode = objectMapper.readTree(evoChainJson);

            boolean hasEvo = checkHasEvolutions(evoNode.get("chain"), name);

            return new RegularPokemon(id,name,types,img,hp,atk,def,spAtk,spDef,spd,habitat,hasEvo);
        }
    }

    private boolean checkHasEvolutions(JsonNode node, String name){
        if (node == null) return false;

        String currentName = node.get("species").get("name").asText();
        JsonNode evolvesTo = node.get("evolves_to");

        if (currentName.equalsIgnoreCase(name)) {
            return evolvesTo != null && evolvesTo.size() > 0;
        }

        if (evolvesTo != null) {
            for (JsonNode nextNode : evolvesTo) {
                if (checkHasEvolutions(nextNode, name)) {
                    return true;
                }
            }
        }

        return false;
    }


}
