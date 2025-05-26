/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// Supplier.java
package com.raven.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Supplier {
    private String code;
    private String name;
    private String contact;
    private String itemsSupplied; // Comma-separated list of item codes
    
    public Supplier(String code, String name, String contact, String itemsSupplied) {
        this.code = code;
        this.name = name;
        this.contact = contact;
        this.itemsSupplied = itemsSupplied;
    }
    
    public String toFileString() {
        return String.join(",", code, name, contact, itemsSupplied);
    }
    
    // Getters and setters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getItemsSupplied() { return itemsSupplied; }
    
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setContact(String contact) { this.contact = contact; }
    
    // New methods for item management
    public void setItemsSupplied(String itemsSupplied) { 
        this.itemsSupplied = itemsSupplied; 
    }
    
    public boolean suppliesItem(String itemCode) {
        if (itemsSupplied == null || itemsSupplied.isEmpty()) {
            return false;
        }
        return Arrays.asList(itemsSupplied.split(",")).contains(itemCode);
    }
    
    public void addItemSupplied(String itemCode) {
        Set<String> items = new HashSet<>();
        if (itemsSupplied != null && !itemsSupplied.isEmpty()) {
            items.addAll(Arrays.asList(itemsSupplied.split(",")));
        }
        items.add(itemCode);
        itemsSupplied = String.join(",", items);
    }
    
    public void removeItemSupplied(String itemCode) {
        if (itemsSupplied == null || itemsSupplied.isEmpty()) {
            return;
        }
        Set<String> items = new HashSet<>(Arrays.asList(itemsSupplied.split(",")));
        items.remove(itemCode);
        itemsSupplied = String.join(",", items);
    }
}
