import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/*
Author: Adam Shergill
Student ID: A01316226
Set#: 3G - Client Server
*/

/**
 * This class simulates hashing with three different hash functions.
 * It runs the simulation on a set of keys and calculates the number
 * of collisions and probes for each hash function on a hash table of a given size.
 */
public class HashSimulator {

    /**
     * Runs the hash simulation for three different hash functions.
     * @param keys Array of strings to hash.
     * @param tableSize The size of the hash table.
     * @return An array containing the number of collisions and probes for each hash function.
     */
    public int[] runHashSimulation(String[] keys, int tableSize) {
        int[] results = new int[6];

        // Simulate with H1
        results[0] = 0; // collisions for H1
        results[1] = 0; // probes for H1
        simulateHashing(keys, tableSize, this::H1, results, 0);

        // Simulate with H2
        results[2] = 0; // collisions for H2
        results[3] = 0; // probes for H2
        simulateHashing(keys, tableSize, this::H2, results, 2);

        // Simulate with H3
        results[4] = 0; // collisions for H3
        results[5] = 0; // probes for H3
        simulateHashing(keys, tableSize, this::H3, results, 4);

        return results;
    }

    /**
     * Simulates hashing for a single hash function.
     * @param keys Array of strings to hash.
     * @param tableSize The size of the hash table.
     * @param func The hash function interface implementation.
     * @param results Array to store results of collisions and probes.
     * @param indexOffset Offset to store results in the results array.
     */
    private void simulateHashing(String[] keys, int tableSize, HashFunction func, int[] results, int indexOffset) {
        String[] table = new String[tableSize];
        for (String key : keys) {
            int hash = func.hash(key, tableSize);
            int probes = 0; // Initialize probes to 0

            // Check the first slot. If it's a collision, we will handle it in the loop.
            if (table[hash] != null && !table[hash].equals(key)) {
                results[indexOffset]++; // Collision for the first slot
            }

            // Continue probing while the slot is not empty and does not contain the current key
            while (table[hash] != null && !table[hash].equals(key)) {
                hash = (hash + 1) % tableSize; // Linear probing
                probes++; // Increment probes for each subsequent slot access
            }

            // Insert the key into the table
            table[hash] = key;
            // Add the probes for this key to the total
            results[indexOffset + 1] += probes;
        }
    }


    public int H1(String key, int tableSize) {
        int hash = 0;
        for (char c : key.toCharArray()) {
            hash += (c - 'A' + 1);
        }
        return hash % tableSize;
    }

    public int H2(String key, int tableSize) {
        long hash = 0;
        long multiplier = 1;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash + (key.charAt(i) - 'A' + 1) * multiplier) % tableSize;
            multiplier = (multiplier * 26) % tableSize;
        }
        return (int)hash;
    }


    /*
    It starts with the length of the string to ensure that an empty string will have a hash of zero.
    It then iterates over each character of the string.
    For each character, it multiplies the current hash value by 31 (a prime number) and then adds the integer value of the current character.
    Finally, it ensures the hash code fits within the table by taking the modulus with the table size.

    Got it from Java's own String.hashCode() function.
     */
    public int H3(String key, int tableSize) {
        long hash = key.length(); // Use long to avoid overflow
        for (int i = 0; i < key.length(); i++) {
            hash = (31 * hash + key.charAt(i)) % tableSize; // Modulo each step to avoid overflow
        }
        return (int)hash; // Cast back to int as the result is now guaranteed to be within tableSize
    }


    @FunctionalInterface
    interface HashFunction {
        int hash(String key, int tableSize);
    }

    public static String[] readStrings(String fileName) throws IOException {
        // read the strings as an ArrayList
        List<String> listOfStrings = Files.readAllLines(Paths.get(fileName));
        // convert the ArrayList to a plain Array of Strings
        return listOfStrings.toArray(new String[0]);
    }
    // main method for your own testing
    public static void main(String[] args) {
        // Define the size of the hash table for the simulation


        try {
            // Read the data file into an array of strings (keys)
            String[] keys = readStrings("37names.txt");

            // Create an instance of the HashSimulator class
            HashSimulator simulator = new HashSimulator();

            // Run the hash simulation with the loaded keys and the specified table size
            int[] results = simulator.runHashSimulation(keys, 37);

            // Output the results for each hash function
            System.out.println("Collisions with H1: " + results[0] + ", Probes with H1: " + results[1]);
            System.out.println("Collisions with H2: " + results[2] + ", Probes with H2: " + results[3]);
            System.out.println("Collisions with H3: " + results[4] + ", Probes with H3: " + results[5]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
