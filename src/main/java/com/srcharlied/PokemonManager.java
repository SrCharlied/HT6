package com.srcharlied;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PokemonManager {
    private Map<String, Pokemon> pokemonMap;
    private Set<String> uColection;
    private static final String colection = "colection.ser";

    public static void main(String[] args) {
        PokemonManager manager = new PokemonManager();
        manager.initialize();
        manager.startMenu();
    }

    public void initialize() {
        // Aquí va map
        Scanner scanner = new Scanner(System.in);
        System.out.println("Seleccione la implementación de Map que desea utilizar:");
        System.out.println("1 - HashMap");
        System.out.println("2 - TreeMap");
        System.out.println("3 - LinkedHashMap");
        
        int option = 0;
        while (option < 1 || option > 3) {
            try {
                System.out.print("Ingrese su opción (1 a 3): ");
                option = Integer.parseInt(scanner.nextLine());
                if (option < 1 || option > 3) {
                    System.out.println("Intente otra vez");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número del 1 al 3.");
            }
        }
        
        pokemonMap = MapFactory.createMap(option);
        
        loaduColection();
    
        loadPokemonData();
    }

    private void loadPokemonData() {
        String filePath = "C:\\Users\\carlo\\Documents\\Algoritmos y Estructura de Datos\\HT6\\src\\main\\java\\com\\srcharlied\\pokemon_data_pokeapi.csv";
        
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Error: El archivo '" + filePath + "' no existe en el directorio de trabajo.");
            System.out.println("Por favor, asegúrese de que el archivo esté en la misma carpeta que el programa.");
            System.exit(1);
            return;
        }
        
        try {
            // Intentar leer el archivo con diferentes codificaciones
            List<String> lines;
            try {
                lines = Files.readAllLines(Paths.get(filePath), java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception e) {
                System.out.println("Intentando con otra codificación...");
                lines = Files.readAllLines(Paths.get(filePath), java.nio.charset.StandardCharsets.ISO_8859_1);
            }
            
            if (lines.isEmpty()) {
                System.out.println("Error: El archivo está vacío.");
                System.exit(1);
                return;
            }
            
            // Verificar y mostrar cabeceras
            String headerLine = lines.get(0);
            String[] headers = headerLine.split(",");
            System.out.println("Cabeceras encontradas: " + Arrays.toString(headers));
            
            // Verificar si las cabeceras necesarias están presentes
            boolean hasNameHeader = false;
            for (String header : headers) {
                if (header.trim().equals("Name")) hasNameHeader = true;
            }
            
            if (!hasNameHeader) {
                System.out.println("Error: El archivo no tiene la cabecera 'Name' que es necesaria.");
                System.exit(1);
                return;
            }
            
            // Procesar las líneas de datos
            int validLines = 0;
            
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                
                // Saltamos líneas vacías
                if (line.isEmpty()) {
                    continue;
                }
                
                try {
                    // Manejo especial para valores con comas dentro de comillas
                    List<String> values = parseCsvLine(line);
                    
                    // Verificamos si hay suficientes valores
                    if (values.size() < headers.length) {
                        System.out.println("Advertencia: La línea " + i + " no tiene suficientes campos.");
                        continue;
                    }
                    
                    // Crear un objeto Pokemon con los valores
                    Pokemon pokemon = new Pokemon();
                    boolean validPokemon = true;
                    
                    for (int j = 0; j < headers.length && j < values.size(); j++) {
                        String headerName = headers[j].trim();
                        String value = values.get(j).trim();
                        
                        try {
                            switch (headerName) {
                                case "Name":
                                    if (value.isEmpty()) {
                                        validPokemon = false;
                                        System.out.println("Error: Pokémon sin nombre en línea " + i);
                                    } else {
                                        pokemon.setName(value);
                                    }
                                    break;
                                case "Pokedex Number":
                                    if (!value.isEmpty()) {
                                        pokemon.setPokedexNumber(Integer.parseInt(value));
                                    }
                                    break;
                                case "Type1":
                                    if (!value.isEmpty()) {
                                        pokemon.setType1(value);
                                    } else {
                                        pokemon.setType1("Normal"); // Valor por defecto
                                    }
                                    break;
                                case "Type2":
                                    pokemon.setType2(value);
                                    break;
                                case "Classification":
                                    pokemon.setClassification(value);
                                    break;
                                case "Height (m)":
                                    if (!value.isEmpty()) {
                                        try {
                                            pokemon.setHeight(Double.parseDouble(value));
                                        } catch (NumberFormatException e) {
                                            System.out.println("Advertencia: Valor de altura inválido en línea " + i + ": " + value);
                                            pokemon.setHeight(0.0); // Valor por defecto
                                        }
                                    }
                                    break;
                                case "Weight (kg)":
                                    if (!value.isEmpty()) {
                                        try {
                                            pokemon.setWeight(Double.parseDouble(value));
                                        } catch (NumberFormatException e) {
                                            System.out.println("Advertencia: Valor de peso inválido en línea " + i + ": " + value);
                                            pokemon.setWeight(0.0); // Valor por defecto
                                        }
                                    }
                                    break;
                                case "Abilities":
                                    pokemon.setAbilities(value);
                                    break;
                                case "Generation":
                                    if (!value.isEmpty()) {
                                        try {
                                            pokemon.setGeneration(Integer.parseInt(value));
                                        } catch (NumberFormatException e) {
                                            System.out.println("Advertencia: Valor de generación inválido en línea " + i + ": " + value);
                                            pokemon.setGeneration(1); // Valor por defecto
                                        }
                                    }
                                    break;
                                case "Legendary Status":
                                    pokemon.setLegendary(value.equalsIgnoreCase("true") || 
                                                    value.equalsIgnoreCase("yes") || 
                                                    value.equalsIgnoreCase("1") ||
                                                    value.equalsIgnoreCase("legendary"));
                                    break;
                            }
                        } catch (Exception e) {
                            System.out.println("Error procesando el campo '" + headerName + "' en la línea " + i + ": " + e.getMessage());
                        }
                    }
                    
                    // Solo agregamos el Pokemon si tiene un nombre válido
                    if (validPokemon && pokemon.getName() != null && !pokemon.getName().isEmpty()) {
                        pokemonMap.put(pokemon.getName(), pokemon);
                        validLines++;
                    }
                } catch (Exception e) {
                    System.out.println("Error procesando la línea " + i + ": " + e.getMessage());
                }
            }
            
            System.out.println("Se han cargado " + validLines + " Pokémon válidos desde el archivo.");
            
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de Pokémon: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    // Método auxiliar para manejar valores con comas dentro de comillas en CSV
    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentValue = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentValue.toString());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        
        // No olvidar añadir el último valor
        result.add(currentValue.toString());
        
        return result;
    }

    private void loaduColection() {
        uColection = new HashSet<>();
        
        File file = new File(colection);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                @SuppressWarnings("unchecked")
                Set<String> loadedCollection = (Set<String>) ois.readObject();
                uColection = loadedCollection;
                System.out.println("Colección del usuario cargada con éxito.");
            } catch (Exception e) {
                System.out.println("Error al cargar la colección del usuario: " + e.getMessage());
                uColection = new HashSet<>();
            }
        }
    }

    private void saveuColection() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(colection))) {
            oos.writeObject(uColection);
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
                        showuColectionByType();
                        break;
                    case 4:
                        showAllPokemonByType();
                        break;
                    case 5:
                        findPokemonByAbility(scanner);
                        break;
                    case 6:
                        System.out.println("Guardando datos y saliendo...");
                        saveuColection();
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
            if (uColection.contains(name)) {
                System.out.println("Este Pokémon ya está en su colección.");
            } else {
                uColection.add(name);
                System.out.println(name + " ha sido agregado a su colección.");
                saveuColection();
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

    private void showuColectionByType() {
        if (uColection.isEmpty()) {
            System.out.println("Su colección está vacía.");
            return;
        }
        
        List<Pokemon> userPokemons = new ArrayList<>();
        for (String name : uColection) {
            if (pokemonMap.containsKey(name)) {
                userPokemons.add(pokemonMap.get(name));
            }
        }
        
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