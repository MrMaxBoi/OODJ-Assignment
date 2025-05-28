package com.raven.form;

import com.raven.model.Model_Card;
import com.raven.model.StatusType;
import com.raven.swing.ScrollBar;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Form_IM_Dashboard extends javax.swing.JPanel {

    public Form_IM_Dashboard() {
        initComponents();
        updateCardData();
    }

    private void updateCardData() {
        // Count items from items.txt and analyze stock levels
        Map<String, Integer> stockAnalysis = analyzeStockLevels("items.txt");
        int itemCount = stockAnalysis.get("totalItems");
        int lowStockItems = stockAnalysis.get("lowStock");
        int normalStockItems = stockAnalysis.get("normalStock");
        int highStockItems = stockAnalysis.get("highStock");

        // Count suppliers from suppliers.txt and average items per supplier
        int supplierCount = countLinesInFile("suppliers.txt");
        double avgItemsPerSupplier = supplierCount > 0 ? (double)itemCount/supplierCount : 0;

        // Get today's sales data
        Map<String, Object> todaySales = getTodaysSalesData();
        int itemsSoldToday = (int) todaySales.get("itemCount");
        double totalSalesToday = (double) todaySales.get("totalAmount");
        
        // Update cards with real data
        card1.setData(new Model_Card(
            new ImageIcon(getClass().getResource("/com/raven/icon/stock.png")), 
            "Item Stock Status", 
            String.format("%d items total - %d LOW, %d NORMAL, %d HIGH", 
                itemCount, lowStockItems, normalStockItems, highStockItems), 
            "Check stock report for details"
        ));
        
        card2.setData(new Model_Card(
            new ImageIcon(getClass().getResource("/com/raven/icon/flag.png")), 
            "Supplier Summary", 
            String.format("%d suppliers managing inventory", supplierCount), 
            String.format("Average %.1f items per supplier", avgItemsPerSupplier)
        ));
        
        card3.setData(new Model_Card(
            new ImageIcon(getClass().getResource("/com/raven/icon/profit.png")), 
            "Daily Sales Entry", 
            String.format("%d items sold today", itemsSoldToday), 
            String.format("Total Sales: RM%.2f", totalSalesToday)
        ));
    }
    
    // Helper method to count lines in a file
    private int countLinesInFile(String filename) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }
    
    
    // Helper method to get today's sales data
    private Map<String, Object> getTodaysSalesData() {
        Map<String, Object> result = new HashMap<>();
        int itemCount = 0;
        double totalAmount = 0.0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());

        try (BufferedReader reader = new BufferedReader(new FileReader("daily_sales.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3 && parts[0].equals(today)) {
                    // Count items sold today
                    String[] items = parts[1].split(";");
                    for (String item : items) {
                        if (!item.isEmpty()) {
                            itemCount++;
                        }
                    }

                    // Get total amount
                    try {
                        totalAmount = Double.parseDouble(parts[2]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    break; // Found today's data, no need to continue
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        result.put("itemCount", itemCount);
        result.put("totalAmount", totalAmount);
        return result;
    }
    
    private Map<String, Integer> analyzeStockLevels(String filename) {
        Map<String, Integer> result = new HashMap<>();
        int totalItems = 0;
        int lowStock = 0;
        int normalStock = 0;
        int highStock = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    totalItems++;
                    int currentStock = Integer.parseInt(parts[4]);
                    int maxStock = Integer.parseInt(parts[5]);
                    int lowStockLevel = Integer.parseInt(parts[6]);

                    if (currentStock <= lowStockLevel) {
                        lowStock++;
                    } else if (currentStock >= maxStock) {
                        highStock++;
                    } else {
                        normalStock++;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        result.put("totalItems", totalItems);
        result.put("lowStock", lowStock);
        result.put("normalStock", normalStock);
        result.put("highStock", highStock);
        return result;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JLayeredPane();
        card1 = new com.raven.component.Card();
        card2 = new com.raven.component.Card();
        card3 = new com.raven.component.Card();

        setBackground(new java.awt.Color(255, 255, 255));

        panel.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        card1.setColor1(new java.awt.Color(142, 142, 250));
        card1.setColor2(new java.awt.Color(123, 123, 245));
        panel.add(card1);

        card2.setColor1(new java.awt.Color(186, 123, 247));
        card2.setColor2(new java.awt.Color(167, 94, 236));
        panel.add(card2);

        card3.setColor1(new java.awt.Color(241, 208, 62));
        card3.setColor2(new java.awt.Color(211, 184, 61));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(card3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(card3, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(251, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.component.Card card1;
    private com.raven.component.Card card2;
    private com.raven.component.Card card3;
    private javax.swing.JLayeredPane panel;
    // End of variables declaration//GEN-END:variables
}
