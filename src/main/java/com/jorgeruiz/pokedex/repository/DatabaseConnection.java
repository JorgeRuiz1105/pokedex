package com.jorgeruiz.pokedex.repository;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:data/pokedex.db";

    public DatabaseConnection() {
    }

    public static Connection connect() throws SQLException{
        //Make sure '/data' directory exists before trying to connect
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return DriverManager.getConnection(DB_URL);
    }

    public static void initDatabase(){
        String pokemonSQL = "CREATE TABLE IF NOT EXISTS pokemon ("
                + "id INTEGER PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "img TEXT,"
                + "base_hp INTEGER NOT NULL,"
                + "base_atk INTEGER NOT NULL,"
                + "base_def INTEGER NOT NULL,"
                + "base_sp_atk INTEGER NOT NULL,"
                + "base_sp_def INTEGER NOT NULL,"
                + "base_spd INTEGER NOT NULL,"
                + "rarity TEXT NOT NULL,"
                + "habitat TEXT,"
                + "has_evo BOOLEAN"
                + ");";

        String typeSQL = "CREATE TABLE IF NOT EXISTS type ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL UNIQUE"
                + ");";

        String pokemonTypeSQL = "CREATE TABLE IF NOT EXISTS pokemon_type ("
                + "pokemon_id INTEGER NOT NULL,"
                + "type_id INTEGER NOT NULL,"
                + "PRIMARY KEY (pokemon_id, type_id),"
                + "FOREIGN KEY (pokemon_id) REFERENCES pokemon(id) ON DELETE CASCADE,"
                + "FOREIGN KEY (type_id) REFERENCES type(id) ON DELETE CASCADE"
                + ");";

        String evolutionsSQL = "CREATE TABLE IF NOT EXISTS evolutions ("
                + "pokemon_id INTEGER PRIMARY KEY,"
                + "evolves_id INTEGER NOT NULL,"
                + "stage INTEGER NOT NULL,"
                + "FOREIGN KEY (evolves_id) REFERENCES pokemon(id) ON DELETE CASCADE"
                + ");";

        try(Connection conn = connect();
            Statement stmt = conn.createStatement()){

            stmt.execute(pokemonSQL);
            stmt.execute(typeSQL);
            stmt.execute(pokemonTypeSQL);
            stmt.execute(evolutionsSQL);

            System.out.println("Database correctly initialized!");
        }catch(SQLException e){
            System.err.println("Error while initializing database: " + e.getMessage());
        }
    }
}
