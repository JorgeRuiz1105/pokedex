package com.jorgeruiz.pokedex.model;

import java.util.List;

public class LegendaryPokemon extends Pokemon{
    public LegendaryPokemon(int id, String name, List<String> types, String img, int baseHp, int baseAtk,
                            int baseDef, int baseSpAtk, int baseSpDef, int baseSpd) {
        super(id, name, types, img, baseHp, baseAtk, baseDef, baseSpAtk, baseSpDef, baseSpd);
    }

    @Override
    public String getRarity() {
        return "LEGENDARY";
    }
}
