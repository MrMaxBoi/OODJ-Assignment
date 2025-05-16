/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ItemManager {
    private static final String ITEMS_FILE = "items.txt";
    private static Map<String, String> itemMap = new HashMap<>();

    static {
        loadItems();
    }

    private static void loadItems() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ITEMS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    itemMap.put(parts[0], parts[1]); // itemCode -> itemName
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getItemName(String itemCode) {
        return itemMap.getOrDefault(itemCode, "Item " + itemCode);
    }
}
