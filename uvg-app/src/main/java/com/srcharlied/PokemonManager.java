package com.srcharlied;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PokemonManager {
    private Map<String, Pokemon> pokemonMap;
    private Set<String> userCollection;
    private static final String USER_COLLECTION_FILE = "user_collection.ser";

    public static void main(String[] args) {
        PokemonManager manager = new PokemonManager();
        manager.initialize();
        manager.startMenu();
    }

    public void initialize() {
        // Aquí va map
        Scanner scanner = new Scanner(System.in);
        System.out.println("Seleccione la implementación de Map que desea utilizar:");
        System.out.println("1) HashMap");
        System.out.println("2) TreeMap");
        System.out.println("3) LinkedHashMap");
        
        int option = 0;
        while (option < 1 || option > 3) {
            try {
                System.out.print("Ingrese su opción (1-3): ");
                option = Integer.parseInt(scanner.nextLine());
                if (option < 1 || option > 3) {
                    System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Ingrese un número del 1 al 3.");
            }
        }
        
        // Usar patrón Factory para crear el Map
        pokemonMap = MapFactory.createMap(option);
        
        // Cargar la colección del usuario desde el archivo
        loadUserCollection();
        
        // Leer el archivo CSV de Pokémon
        loadPokemonData();
    }

    private void loadPokemonData() {
        String filePath = "pokemon.csv"; // Asumimos que el archivo está en la carpeta del proyecto
        
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            String[] headers = lines.get(0).split(",");
            
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] values = line.split(",");
                
                // Crear un objeto Pokemon con los valores
                Pokemon pokemon = new Pokemon();
                for (int j = 0; j < headers.length && j < values.length; j++) {
                    switch (headers[j].trim()) {
                        case "Name":
                            pokemon.setName(values[j].trim());
                            break;
                        case "Pokedex Number":
                            pokemon.setPokedexNumber(Integer.parseInt(values[j].trim()));
                            break;
                        case "Type1":
                            pokemon.setType1(values[j].trim());
                            break;
                        case "Type2":
                            pokemon.setType2(values[j].trim());
                            break;
                        case "Classification":
                            pokemon.setClassification(values[j].trim());
                            break;
                        case "Height (m)":
                            pokemon.setHeight(Double.parseDouble(values[j].trim()));
                            break;
                        case "Weight (kg)":
                            pokemon.setWeight(Double.parseDouble(values[j].trim()));
                            break;
                        case "Abilities":
                            pokemon.setAbilities(values[j].trim());
                            break;
                        case "Generation":
                            pokemon.setGeneration(Integer.parseInt(values[j].trim()));
                            break;
                        case "Legendary Status":
                            pokemon.setLegendary(values[j].trim().equalsIgnoreCase("true") || 
                                               values[j].trim().equalsIgnoreCase("yes") || 
                                               values[j].trim().equalsIgnoreCase("1"));
                            break;
                    }
                }
                
                // Agregar el Pokemon al mapa
                pokemonMap.put(pokemon.getName(), pokemon);
            }
            
            System.out.println("Se han cargado " + pokemonMap.size() + " Pokémon desde el archivo.");
            
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de Pokémon: " + e.getMessage());
            System.exit(1);
        } catch (NumberFormatException e) {
            System.out.println("Error al convertir datos numéricos: " + e.getMessage());
            System.exit(1);
        }
    }

    private void loadUserCollection() {
        userCollection = new HashSet<>();
        
        File file = new File(USER_COLLECTION_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                @SuppressWarnings("unchecked")
                Set<String> loadedCollection = (Set<String>) ois.readObject();
                userCollection = loadedCollection;
                System.out.println("Colección del usuario cargada con éxito.");
            } catch (Exception e) {
                System.out.println("Error al cargar la colección del usuario: " + e.getMessage());
                userCollection = new HashSet<>();
            }
        }
    }

    private void saveUserCollection() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_COLLECTION_FILE))) {
            oos.writeObject(userCollection);
            System.out.println("Colección del usuario guardada con éxito.");
        } catch (IOException e) {
            System.out.println("Error al guardar la colección del usuario: " + e.getMessage());
        }
    }

    public void startMenu() {
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        
        while (option != 6) {
            System.out.println("\n===== MENÚ PRINCIPAL =====");
            System.out.println("1. Agregar un Pokémon a mi colección");
            System.out.println("2. Mostrar datos de un Pokémon");
            System.out.println("3. Mostrar mi colección ordenada por tipo primario");
            System.out.println("4. Mostrar todos los Pokémon ordenados por tipo primario");
            System.out.println("5. Buscar Pokémon por habilidad");
            System.out.println("6. Salir");
            
            try {
                System.out.print("Ingrese su opción (1-6): ");
                option = Integer.parseInt(scanner.nextLine());
                
                switch (option) {
                    case 1:
                        addPokemonToCollection(scanner);
                        break;
                    case 2:
                        showPokemonDetails(scanner);
                        break;
                    case 3:
                        showUserCollectionByType();
                        break;
                    case 4:
                        showAllPokemonByType();
                        break;
                    case 5:
                        findPokemonByAbility(scanner);
                        break;
                    case 6:
                        System.out.println("Guardando datos y saliendo...");
                        saveUserCollection();
                        break;
                    default:
                        System.out.println("Opción inválida. Intente nuevamente.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Ingrese un número del 1 al 6.");
            }
        }
        
        scanner.close();
    }

    private void addPokemonToCollection(Scanner scanner) {
        System.out.print("Ingrese el nombre del Pokémon que desea agregar: ");
        String name = scanner.nextLine().trim();
        
        if (pokemonMap.containsKey(name)) {
            if (userCollection.contains(name)) {
                System.out.println("Este Pokémon ya está en su colección.");
            } else {
                userCollection.add(name);
                System.out.println(name + " ha sido agregado a su colección.");
                saveUserCollection();
            }
        } else {
            System.out.println("Error: No se encontró un Pokémon con el nombre '" + name + "'.");
        }
    }

    private void showPokemonDetails(Scanner scanner) {
        System.out.print("Ingrese el nombre del Pokémon: ");
        String name = scanner.nextLine().trim();
        
        if (pokemonMap.containsKey(name)) {
            Pokemon pokemon = pokemonMap.get(name);
            System.out.println("\n===== DETALLES DEL POKÉMON =====");
            System.out.println(pokemon);
        } else {
            System.out.println("Error: No se encontró un Pokémon con el nombre '" + name + "'.");
        }
    }

    private void showUserCollectionByType() {
        if (userCollection.isEmpty()) {
            System.out.println("Su colección está vacía.");
            return;
        }
        
        // Obtener todos los Pokemon de la colección del usuario
        List<Pokemon> userPokemons = new ArrayList<>();
        for (String name : userCollection) {
            if (pokemonMap.containsKey(name)) {
                userPokemons.add(pokemonMap.get(name));
            }
        }
        
        // Ordenar por tipo primario
        userPokemons.sort(Comparator.comparing(Pokemon::getType1));
        
        System.out.println("\n===== SU COLECCIÓN ORDENADA POR TIPO PRIMARIO =====");
        System.out.printf("%-20s %-15s\n", "Nombre", "Tipo Primario");
        System.out.println("----------------------------------------");
        
        for (Pokemon pokemon : userPokemons) {
            System.out.printf("%-20s %-15s\n", pokemon.getName(), pokemon.getType1());
        }
    }

    private void showAllPokemonByType() {
        List<Pokemon> allPokemons = new ArrayList<>(pokemonMap.values());
        
        // Ordenar por tipo primario
        allPokemons.sort(Comparator.comparing(Pokemon::getType1));
        
        System.out.println("\n===== TODOS LOS POKÉMON ORDENADOS POR TIPO PRIMARIO =====");
        System.out.printf("%-20s %-15s\n", "Nombre", "Tipo Primario");
        System.out.println("----------------------------------------");
        
        for (Pokemon pokemon : allPokemons) {
            System.out.printf("%-20s %-15s\n", pokemon.getName(), pokemon.getType1());
        }
    }

    private void findPokemonByAbility(Scanner scanner) {
        System.out.print("Ingrese la habilidad que desea buscar: ");
        String ability = scanner.nextLine().trim().toLowerCase();
        
        System.out.println("\n===== POKÉMON CON LA HABILIDAD '" + ability + "' =====");
        
        boolean found = false;
        for (Pokemon pokemon : pokemonMap.values()) {
            if (pokemon.getAbilities().toLowerCase().contains(ability)) {
                System.out.println(pokemon.getName());
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("No se encontraron Pokémon con la habilidad '" + ability + "'.");
        }
    }
}

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
    
    // Getters y setters
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

// Clase Factory para crear diferentes tipos de Map
class MapFactory {
    public static Map<String, Pokemon> createMap(int option) {
        switch (option) {
            case 1:
                System.out.println("Usando HashMap como implementación.");
                return new HashMap<>();
            case 2:
                System.out.println("Usando TreeMap como implementación.");
                return new TreeMap<>();
            case 3:
                System.out.println("Usando LinkedHashMap como implementación.");
                return new LinkedHashMap<>();
            default:
                System.out.println("Opción inválida. Usando HashMap por defecto.");
                return new HashMap<>();
        }
    }
}