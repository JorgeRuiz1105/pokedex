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
        String pokemon_sql = "CREATE TABLE IF NOT EXISTS pokemon ("
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

        String type_sql = "CREATE TABLE IF NOT EXISTS type ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL UNIQUE"
                + ");";

        String pokemon_type_sql = "CREATE TABLE IF NOT EXISTS pokemon_type ("
                + "pokemon_id INTEGER NOT NULL,"
                + "type_id INTEGER NOT NULL,"
                + "PRIMARY KEY (pokemon_id, type_id),"
                + "FOREIGN KEY (pokemon_id) REFERENCES pokemon(id) ON DELETE CASCADE,"
                + "FOREIGN KEY (type_id) REFERENCES type(id) ON DELETE CASCADE"
                + ");";

        String evolutions_sql = "CREATE TABLE IF NOT EXISTS evolutions ("
                + "pokemon_id INTEGER PRIMARY KEY,"
                + "evolves_id INTEGER NOT NULL,"
                + "stage INTEGER NOT NULL,"
                + "FOREIGN KEY (evolves_id) REFERENCES pokemon(id) ON DELETE CASCADE"
                + ");";

        try(Connection conn = connect();
            Statement stmt = conn.createStatement()){

            stmt.execute(pokemon_sql);
            stmt.execute(type_sql);
            stmt.execute(pokemon_type_sql);
            stmt.execute(evolutions_sql);

            System.out.println("Database correctly initialized!");
        }catch(SQLException e){
            System.err.println("Error while initializing database: " + e.getMessage());
        }
    }
}
