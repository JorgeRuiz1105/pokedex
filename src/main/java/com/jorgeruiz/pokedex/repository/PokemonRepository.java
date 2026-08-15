package com.jorgeruiz.pokedex.repository;

import com.jorgeruiz.pokedex.model.Pokemon;
import com.jorgeruiz.pokedex.model.RegularPokemon;

import java.lang.reflect.Type;
import java.sql.*;

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
}
