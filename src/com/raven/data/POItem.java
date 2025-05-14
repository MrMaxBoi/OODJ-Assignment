/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.data;

public class POItem {
    private final String itemCode;  // Made final since it shouldn't change after creation
    private int quantity;

    public POItem(String itemCode, int quantity) {
        if (itemCode == null || itemCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Item code cannot be null or empty");
        }
        this.itemCode = itemCode;
        setQuantity(quantity);  // Use setter for validation
    }
    
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    // Getters
    public String getItemCode() { return itemCode; }
    public int getQuantity() { return quantity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        POItem poItem = (POItem) o;
        return itemCode.equals(poItem.itemCode);
    }

    @Override
    public int hashCode() {
        return itemCode.hashCode();
    }

    @Override
    public String toString() {
        return "POItem{" +
               "itemCode='" + itemCode + '\'' +
               ", quantity=" + quantity +
               '}';
    }
}
