package com.jorgeruiz.pokedex.repository;

import com.jorgeruiz.pokedex.model.LegendaryPokemon;
import com.jorgeruiz.pokedex.model.MythicalPokemon;
import com.jorgeruiz.pokedex.model.Pokemon;
import com.jorgeruiz.pokedex.model.RegularPokemon;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PokemonRepository {
    public void save(Pokemon pokemon) throws SQLException {
        String pokemonSQL = "INSERT OR REPLACE INTO pokemon (id, name, img, base_hp, base_atk, base_def, base_sp_atk, " +
                "base_sp_def, base_spd, rarity, habitat, has_evo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseConnection.connect()){

            conn.setAutoCommit(false);

            try{
                savePokemonBase(conn,pokemonSQL,pokemon);
                savePokemonTypes(conn,pokemon);

                conn.commit();
            }catch (SQLException e){
                conn.rollback();
                throw e;
            }
        }catch (SQLException e){
            System.out.println("An error ocurred while saving pokemon: " + e.getMessage());
        }
    }

    public List<Pokemon> findAll() throws SQLException{
        String pokemonSQL = "SELECT * FROM pokemon";
        List<Pokemon> pokemons = new ArrayList<>();

        try(Connection conn = DatabaseConnection.connect()){
            try(PreparedStatement stmt = conn.prepareStatement(pokemonSQL);
                ResultSet rs = stmt.executeQuery()){
                    while(rs.next()){
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String img = rs.getString("img");
                        int hp = rs.getInt("base_hp");
                        int def = rs.getInt("base_def");
                        int atk = rs.getInt("base_atk");
                        int spAtk = rs.getInt("base_sp_atk");
                        int spDef = rs.getInt("base_sp_def");
                        int spd = rs.getInt("base_spd");
                        List<String> types = findPokemonTypes(conn, id);
                        String rarity = rs.getString("rarity");

                        Pokemon pokemon;

                        if(rarity.equalsIgnoreCase("REGULAR")){
                            String habitat = rs.getString("habitat");
                            boolean hasEvolutions = rs.getBoolean("has_evo");
                            pokemon = new RegularPokemon(id,name,types,img,hp,def,atk,spAtk,spDef,spd,habitat,hasEvolutions);
                        } else if(rarity.equalsIgnoreCase("MYTHICAL")){
                            pokemon = new MythicalPokemon(id,name,types,img,hp,atk,def,spAtk,spDef,spd);
                        } else{
                            pokemon = new LegendaryPokemon(id,name,types,img,hp,atk,def,spAtk,spDef,spd);
                        }
                        pokemons.add(pokemon);
                    }
            }
        }
        return pokemons;
    }

    private void savePokemonBase(Connection conn, String query, Pokemon pokemon) throws SQLException{
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pokemon.getId());
            stmt.setString(2, pokemon.getName());
            stmt.setString(3, pokemon.getImg());
            stmt.setInt(4, pokemon.getBaseHp());
            stmt.setInt(5, pokemon.getBaseAtk());
            stmt.setInt(6, pokemon.getBaseDef());
            stmt.setInt(7, pokemon.getBaseSpAtk());
            stmt.setInt(8, pokemon.getBaseSpDef());
            stmt.setInt(9, pokemon.getBaseSpd());
            stmt.setString(10, pokemon.getRarity());

            if(pokemon instanceof RegularPokemon){
                stmt.setString(11, ((RegularPokemon) pokemon).getHabitat());
                stmt.setBoolean(12, ((RegularPokemon) pokemon).isHasEvolutions());
            }else{
                stmt.setNull(11, Types.VARCHAR);
                stmt.setNull(12, Types.BOOLEAN);
            }

            stmt.executeUpdate();
        }
    }

    private void savePokemonTypes(Connection conn, Pokemon pokemon) throws SQLException{
            String deleteSQL = "DELETE FROM pokemon_type WHERE pokemon_id = ?";
            String insertSQL = "INSERT INTO pokemon_type (pokemon_id, type_id) VALUES (?,?)";
            try(PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);
                PreparedStatement insertStmt = conn.prepareStatement(insertSQL)){

                deleteStmt.setInt(1, pokemon.getId());
                deleteStmt.executeUpdate();

                if(pokemon.getTypes() != null){
                    for(String typeName : pokemon.getTypes()){
                        int typeId = getOrCreateTypeId(conn,typeName);
                        insertStmt.setInt(1, pokemon.getId());
                        insertStmt.setInt(2, typeId);
                        insertStmt.addBatch();
                    }
                    insertStmt.executeBatch();
                }
            }
    }

    private int getOrCreateTypeId(Connection conn, String typeName) throws SQLException{
        String selectSQL = "SELECT id FROM type WHERE name = ?";

        try(PreparedStatement selectStmt = conn.prepareStatement(selectSQL)){
            selectStmt.setString(1,typeName);
            try(ResultSet rs = selectStmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt("id");
                }
            }
        }

        String insertSQL = "INSERT INTO type (name) VALUES (?)";
        try(PreparedStatement selectStmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)){
            selectStmt.setString(1,typeName);
            selectStmt.executeUpdate();
            try(ResultSet rs = selectStmt.getGeneratedKeys()){
                if(rs.next()){
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Error while attempting to get or create record for: " + typeName);
    }

    private List<String> findPokemonTypes(Connection conn, int id) throws SQLException{
        String findSQL = "SELECT t.name FROM type t "
                + "INNER JOIN pokemon_type pt ON t.id = pt.type_id "
                + "WHERE pt.pokemon_id = ?";
        List <String> types = new ArrayList<>();

        try(PreparedStatement findStmt = conn.prepareStatement(findSQL)){
            findStmt.setInt(1,id);
            try(ResultSet rs = findStmt.executeQuery()){
                while(rs.next()){
                    types.add(rs.getString("name"));
                }
            }
        }
        return types;
    }
}
