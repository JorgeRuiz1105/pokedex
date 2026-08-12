package com.jorgeruiz.pokedex.model;

import java.util.List;

public class RegularPokemon extends Pokemon{
    private final String habitat;
    private final boolean hasEvolutions;

    public RegularPokemon(int id, String name, List<String> types, String img, int baseHp, int baseAtk,
                          int baseDef, int baseSpAtk, int baseSpDef, int baseSpd, String habitat,
                          boolean hasEvolutions) {
        super(id, name, types, img, baseHp, baseAtk, baseDef, baseSpAtk, baseSpDef, baseSpd);
        this.habitat = habitat;
        this.hasEvolutions = hasEvolutions;
    }

    public String getHabitat() {
        return habitat;
    }

    public boolean isHasEvolutions() {
        return hasEvolutions;
    }

    @Override
    public String getRarity() {
        return "REGULAR";
    }
}
