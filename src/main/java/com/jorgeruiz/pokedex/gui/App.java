package com.jorgeruiz.pokedex.gui;

import com.jorgeruiz.pokedex.model.Pokemon;
import com.jorgeruiz.pokedex.model.RegularPokemon;
import com.jorgeruiz.pokedex.service.PokemonService;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class App extends Application {

    private final PokemonService service = new PokemonService();

    private final ListView<Pokemon> listView = new ListView<>();
    private final Label statusLabel = new Label("Listo.");
    private final ImageView spriteView = new ImageView();

    // Detail labels
    private final Label nameLabel = new Label("-");
    private final Label idLabel = new Label("-");
    private final Label typesLabel = new Label("-");
    private final Label rarityLabel = new Label("-");
    private final Label statsLabel = new Label("-");
    private final Label bstLabel = new Label("-");
    private final Label extraLabel = new Label("-"); // habitat / evolutions, only for RegularPokemon

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        root.setTop(buildTopBar());
        root.setLeft(buildListPanel());
        root.setCenter(buildDetailPanel());
        root.setBottom(statusLabel);
        BorderPane.setMargin(statusLabel, new Insets(8, 0, 0, 0));

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> showDetails(newVal));

        stage.setTitle("Pokedex - GUI de pruebas");
        stage.setScene(new Scene(root, 780, 480));
        stage.show();
    }

    private VBox buildTopBar() {
        TextField idField = new TextField();
        idField.setPromptText("ID de Pokemon (ej: 25)");
        idField.setPrefWidth(120);

        Button fetchBtn = new Button("Buscar / Guardar");
        fetchBtn.setOnAction(e -> {
            String text = idField.getText().trim();
            if (text.isEmpty()) {
                setStatus("Escribe un ID primero.");
                return;
            }
            int id;
            try {
                id = Integer.parseInt(text);
            } catch (NumberFormatException ex) {
                setStatus("El ID debe ser un numero entero.");
                return;
            }
            runTask("Buscando Pokemon #" + id + "...", () -> service.getAndSavePokemon(id),
                    pokemon -> {
                        if (pokemon != null) {
                            listView.getItems().removeIf(p -> p.getId() == pokemon.getId());
                            listView.getItems().add(pokemon);
                            listView.getSelectionModel().select(pokemon);
                            setStatus("Cargado: " + pokemon.getName());
                        } else {
                            setStatus("No se encontro el Pokemon.");
                        }
                    });
        });

        TextField typeField = new TextField();
        typeField.setPromptText("Tipo (ej: fire)");
        typeField.setPrefWidth(100);

        Button filterTypeBtn = new Button("Filtrar por tipo");
        filterTypeBtn.setOnAction(e -> {
            String type = typeField.getText().trim();
            if (type.isEmpty()) {
                setStatus("Escribe un tipo primero.");
                return;
            }
            runTask("Filtrando por tipo " + type + "...", () -> service.getPokemonsByType(type),
                    this::replaceListItems);
        });

        ComboBox<String> rarityBox = new ComboBox<>();
        rarityBox.getItems().addAll("REGULAR", "LEGENDARY", "MYTHICAL");
        rarityBox.setPromptText("Rareza");

        Button filterRarityBtn = new Button("Filtrar por rareza");
        filterRarityBtn.setOnAction(e -> {
            String rarity = rarityBox.getValue();
            if (rarity == null) {
                setStatus("Selecciona una rareza primero.");
                return;
            }
            runTask("Filtrando por rareza " + rarity + "...", () -> service.getPokemonsByRarity(rarity),
                    this::replaceListItems);
        });

        Button showAllBtn = new Button("Mostrar todos");
        showAllBtn.setOnAction(e -> runTask("Cargando todos los Pokemon guardados...",
                service::getAllStoredPokemonsSorted, this::replaceListItems));

        Button strongestBtn = new Button("Mas fuerte (BST)");
        strongestBtn.setOnAction(e -> runTask("Calculando el mas fuerte...", service::getStrongestPokemonByBST,
                pokemon -> {
                    if (pokemon != null) {
                        listView.getSelectionModel().select(pokemon);
                        if (!listView.getItems().contains(pokemon)) {
                            listView.getItems().add(pokemon);
                            listView.getSelectionModel().select(pokemon);
                        }
                        setStatus("Mas fuerte: " + pokemon.getName() + " (BST " + pokemon.calculateBST() + ")");
                    } else {
                        setStatus("Aun no hay Pokemon guardados.");
                    }
                }));

        HBox searchRow = new HBox(8, idField, fetchBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        HBox filterRow = new HBox(8, typeField, filterTypeBtn, rarityBox, filterRarityBtn, showAllBtn, strongestBtn);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        VBox top = new VBox(8, searchRow, filterRow);
        top.setPadding(new Insets(0, 0, 10, 0));
        return top;
    }

    private VBox buildListPanel() {
        listView.setPrefWidth(260);
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pokemon pokemon, boolean empty) {
                super.updateItem(pokemon, empty);
                setText(empty || pokemon == null
                        ? null
                        : "#" + pokemon.getId() + " " + pokemon.getName() + " (" + pokemon.getRarity() + ")");
            }
        });
        VBox box = new VBox(new Label("Resultados"), listView);
        box.setSpacing(4);
        BorderPane.setMargin(box, new Insets(0, 10, 0, 0));
        return box;
    }

    private VBox buildDetailPanel() {
        spriteView.setFitWidth(96);
        spriteView.setFitHeight(96);
        spriteView.setPreserveRatio(true);

        VBox details = new VBox(6,
                nameLabel, idLabel, typesLabel, rarityLabel, statsLabel, bstLabel, extraLabel);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox content = new HBox(16, spriteView, details);
        content.setAlignment(Pos.TOP_LEFT);

        VBox box = new VBox(new Label("Detalle"), content);
        box.setSpacing(4);
        return box;
    }

    private void showDetails(Pokemon pokemon) {
        if (pokemon == null) {
            nameLabel.setText("-");
            idLabel.setText("-");
            typesLabel.setText("-");
            rarityLabel.setText("-");
            statsLabel.setText("-");
            bstLabel.setText("-");
            extraLabel.setText("-");
            spriteView.setImage(null);
            return;
        }
        nameLabel.setText(pokemon.getName());
        idLabel.setText("ID: " + pokemon.getId());
        typesLabel.setText("Tipos: " + String.join(", ", pokemon.getTypes()));
        rarityLabel.setText("Rareza: " + pokemon.getRarity());
        statsLabel.setText(String.format("HP %d | ATK %d | DEF %d | SpA %d | SpD %d | SPD %d",
                pokemon.getBaseHp(), pokemon.getBaseAtk(), pokemon.getBaseDef(),
                pokemon.getBaseSpAtk(), pokemon.getBaseSpDef(), pokemon.getBaseSpd()));
        bstLabel.setText("BST: " + pokemon.calculateBST());

        if (pokemon instanceof RegularPokemon regular) {
            extraLabel.setText("Habitat: " + regular.getHabitat()
                    + " | Evoluciones: " + (regular.isHasEvolutions() ? "si" : "no"));
        } else {
            extraLabel.setText("");
        }

        String img = pokemon.getImg();
        spriteView.setImage(img == null || img.isEmpty() ? null : new Image(img, true));
    }

    private void replaceListItems(List<Pokemon> pokemons) {
        listView.getItems().setAll(pokemons);
        setStatus(pokemons.isEmpty() ? "Sin resultados." : pokemons.size() + " resultado(s).");
    }

    private void setStatus(String text) {
        statusLabel.setText(text);
    }

    // --- Background task helper -------------------------------------------------

    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    private <T> void runTask(String loadingMessage, ThrowingSupplier<T> work, java.util.function.Consumer<T> onSuccess) {
        setStatus(loadingMessage);
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return work.get();
            }
        };
        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            setStatus("Error: " + (ex != null ? ex.getMessage() : "desconocido"));
            ex.printStackTrace();
        });
        Thread thread = new Thread(task, "pokedex-task");
        thread.setDaemon(true);
        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}