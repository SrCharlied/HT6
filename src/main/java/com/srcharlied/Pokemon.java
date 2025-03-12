package com.srcharlied;

import java.io.*;
import java.nio.file.*;
import java.util.*;

// Clase Pokemon
class Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private int pokedexNumber;
    private String type1;
    private String type2;
    private String classification;
    private double height;
    private double weight;
    private String abilities;
    private int generation;
    private boolean legendary;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getPokedexNumber() { return pokedexNumber; }
    public void setPokedexNumber(int pokedexNumber) { this.pokedexNumber = pokedexNumber; }
    
    public String getType1() { return type1; }
    public void setType1(String type1) { this.type1 = type1; }
    
    public String getType2() { return type2; }
    public void setType2(String type2) { this.type2 = type2; }
    
    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }
    
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    
    public String getAbilities() { return abilities; }
    public void setAbilities(String abilities) { this.abilities = abilities; }
    
    public int getGeneration() { return generation; }
    public void setGeneration(int generation) { this.generation = generation; }
    
    public boolean isLegendary() { return legendary; }
    public void setLegendary(boolean legendary) { this.legendary = legendary; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre: ").append(name).append("\n");
        sb.append("Número de Pokédex: ").append(pokedexNumber).append("\n");
        sb.append("Tipo Primario: ").append(type1).append("\n");
        sb.append("Tipo Secundario: ").append(type2).append("\n");
        sb.append("Clasificación: ").append(classification).append("\n");
        sb.append("Altura (m): ").append(height).append("\n");
        sb.append("Peso (kg): ").append(weight).append("\n");
        sb.append("Habilidades: ").append(abilities).append("\n");
        sb.append("Generación: ").append(generation).append("\n");
        sb.append("Estado Legendario: ").append(legendary ? "Sí" : "No").append("\n");
        return sb.toString();
    }
}