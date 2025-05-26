/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// SupplierRepository.java
package com.raven.data;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SupplierRepository {
    private static final String FILE_PATH = "suppliers.txt";
    
    public static List<Supplier> loadSuppliers() throws IOException {
        List<Supplier> suppliers = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            file.createNewFile();
            return suppliers;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts.length >= 4) {
                    String itemsSupplied = parts.length > 4 ? 
                        String.join(",", Arrays.copyOfRange(parts, 3, parts.length)) : 
                        parts[3];
                    suppliers.add(new Supplier(parts[0], parts[1], parts[2], itemsSupplied));
                }
            }
        }
        return suppliers;
    }
    
    public static void saveSuppliers(List<Supplier> suppliers) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Supplier supplier : suppliers) {
                writer.write(supplier.toFileString());
                writer.newLine();
            }
        }
    }
    
    public static Supplier findSupplierByCode(String code) throws IOException {
        List<Supplier> suppliers = loadSuppliers();
        for (Supplier s : suppliers) {
            if (s.getCode().equals(code)) {
                return s;
            }
        }
        return null;
    }
    
    public static void updateSupplier(Supplier updatedSupplier) throws IOException {
        List<Supplier> suppliers = loadSuppliers();
        for (int i = 0; i < suppliers.size(); i++) {
            if (suppliers.get(i).getCode().equals(updatedSupplier.getCode())) {
                suppliers.set(i, updatedSupplier);
                break;
            }
        }
        saveSuppliers(suppliers);
    }
    
    // In SupplierRepository.java
    public static Map<String, String> getSupplierNameCodeMap() throws IOException {
        Map<String, String> map = new LinkedHashMap<>(); // Preserves insertion order
        List<Supplier> suppliers = loadSuppliers();
        for (Supplier s : suppliers) {
            map.put(s.getName() + " (" + s.getCode() + ")", s.getCode());
        }
        return map;
    }
}
