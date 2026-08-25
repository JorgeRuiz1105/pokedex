import com.jorgeruiz.pokedex.model.Pokemon;
import com.jorgeruiz.pokedex.repository.DatabaseConnection;
import com.jorgeruiz.pokedex.service.PokemonService;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        PokemonService service = new PokemonService();

        try {
            service.getAndSavePokemon(150);
            service.getAndSavePokemon(151);
            service.getAndSavePokemon(9);
            service.getAndSavePokemon(493);

            System.out.println("Filter by water type: ");
            service.getPokemonsByType("water").forEach(p -> System.out.println(p.getName()));

            System.out.println("Weakest to strongest pokemons: ");
            service.getAllStoredPokemonsSorted().forEach(p -> System.out.println(p.getName()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
