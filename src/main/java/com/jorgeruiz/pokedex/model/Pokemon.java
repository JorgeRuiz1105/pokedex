package com.jorgeruiz.pokedex.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Pokemon implements StatEvaluator, Comparable<Pokemon>{
    private final int id;
    private final String name;
    private final List<String> types;
    private final String img;
    protected int baseHp;
    protected int baseAtk;
    protected int baseDef;
    protected int baseSpAtk;
    protected int baseSpDef;
    protected int baseSpd;

    protected Pokemon(int id, String name, List<String> types, String img, int baseHp, int baseAtk,
                      int baseDef, int baseSpAtk, int baseSpDef, int baseSpd) {
        this.id = id;
        this.name = name;
        this.types = new ArrayList<>(types);
        this.img = img;
        this.baseHp = baseHp;
        this.baseAtk = baseAtk;
        this.baseDef = baseDef;
        this.baseSpAtk = baseSpAtk;
        this.baseSpDef = baseSpDef;
        this.baseSpd = baseSpd;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getTypes() {
        return new ArrayList<>(types);
    }

    public String getImg() {
        return img;
    }

    public int getBaseHp() {
        return baseHp;
    }

    public void setBaseHp(int baseHp) {
        this.baseHp = baseHp;
    }

    public int getBaseAtk() {
        return baseAtk;
    }

    public void setBaseAtk(int baseAtk) {
        this.baseAtk = baseAtk;
    }

    public int getBaseDef() {
        return baseDef;
    }

    public void setBaseDef(int baseDef) {
        this.baseDef = baseDef;
    }

    public int getBaseSpAtk() {
        return baseSpAtk;
    }

    public void setBaseSpAtk(int baseSpAtk) {
        this.baseSpAtk = baseSpAtk;
    }

    public int getBaseSpDef() {
        return baseSpDef;
    }

    public void setBaseSpDef(int baseSpDef) {
        this.baseSpDef = baseSpDef;
    }

    public int getBaseSpd() {
        return baseSpd;
    }

    public void setBaseSpd(int baseSpd) {
        this.baseSpd = baseSpd;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", types=" + types +
                ", img='" + img + '\'' +
                ", baseHp=" + baseHp +
                ", baseAtk=" + baseAtk +
                ", baseDef=" + baseDef +
                ", baseSpAtk=" + baseSpAtk +
                ", baseSpDef=" + baseSpDef +
                ", baseSpd=" + baseSpd +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pokemon pokemon = (Pokemon) o;
        return id == pokemon.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int calculateBST() {
        return this.baseHp + this.baseAtk + this.baseDef + this.baseSpd + this.baseSpAtk +  this.baseSpDef;
    }

    @Override
    public int compareTo(Pokemon o) {
        return Integer.compare(this.calculateBST(), o.calculateBST());
    }

    public abstract String getRarity();
}
