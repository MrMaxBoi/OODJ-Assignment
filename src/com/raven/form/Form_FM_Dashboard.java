package com.raven.form;

import com.raven.model.Model_Card;
import com.raven.model.StatusType;
import com.raven.swing.ScrollBar;
import com.raven.component.FM_Card_Profit;

import javax.swing.ImageIcon;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Form_FM_Dashboard extends javax.swing.JPanel {

    private Timer logRefreshTimer;
    private FM_Card_Profit cardProfit;
    
    public Form_FM_Dashboard() {
        initComponents();
        jScrollPane1.setViewportView(Item_Update_log);
        Item_Update_log.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        Item_Update_log.setMargin(new Insets(8, 8, 8, 8));
        Item_Update_log.setLineWrap(true);
        Item_Update_log.setWrapStyleWord(true);
        Item_Update_log.setBackground(new Color(250, 250, 250));
        Item_Update_log.setForeground(new Color(60, 60, 60));
        cardProfit = new FM_Card_Profit();
        updateCardData();
        loadItemUpdateLog();
        LogAutoRefresh();
    }
    
    private void updateCardData() {
        // Count items from items.txt
        int PurchaseOrderCount = countLines("purchase_orders.txt");
        
        // Count suppliers from suppliers.txt
        int TotalSalesCount = countLines("suppliers.txt");
        
        // Get today's sales data
        Map<String, Object> todaySales = getTodaySales();
        int itemsSoldToday = (int) todaySales.get("itemCount");
        double totalSalesToday = (double) todaySales.get("totalAmount");
        
        // Update cards with real data
        cardProfit.setTitle("Remaining Purchase Order waiting for Approved or Reject");
        cardProfit.setValue("There are total " + PurchaseOrderCount + " items.");
        cardProfit.setRevenue(""); 
        
        card2.setData(new Model_Card(
            new ImageIcon(getClass().getResource("/com/raven/icon/FM_Report.png")), 
            "Total Sales Monthly", 
            "There are total " + TotalSalesCount + " suppliers.", 
            ""
        ));
    }
 
    private void loadItemUpdateLog() {
        StringBuilder logContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("logs.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logContent.append(line).append("\n");
            }
        } catch (IOException e) {
            logContent.append("Error reading logs: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }

        Item_Update_log.setText(logContent.toString());
        // scroll to bottom
        Item_Update_log.setCaretPosition(
            Item_Update_log.getDocument().getLength()
        );
    }

    
    private void LogAutoRefresh() {
        if (logRefreshTimer != null && logRefreshTimer.isRunning()) {
            logRefreshTimer.stop();
        }
        logRefreshTimer = new Timer(3000, e -> loadItemUpdateLog());
        logRefreshTimer.setInitialDelay(0);
        logRefreshTimer.start();
    }
    
    private int countLines(String filename) {
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
    
    private Map<String, Object> getTodaySales() {
        Map<String, Object> result     = new HashMap<>();
        int                  itemCount  = 0;
        double               totalAmount = 0.0;
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try (BufferedReader reader = new BufferedReader(new FileReader("daily_sales.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3 && parts[0].equals(today)) {
                    for (String item : parts[1].split(";")) {
                        if (!item.isEmpty()) itemCount++;
                    }
                    try {
                        totalAmount = Double.parseDouble(parts[2]);
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        result.put("itemCount", itemCount);
        result.put("totalAmount", totalAmount);
        return result;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar1 = new java.awt.MenuBar();
        panel = new javax.swing.JLayeredPane();
        card2 = new com.raven.component.Card();
        fM_Card_Profit1 = new com.raven.component.FM_Card_Profit();
        jScrollPane1 = new javax.swing.JScrollPane();
        Item_Update_log = new javax.swing.JTextArea();

        setBackground(new java.awt.Color(255, 255, 255));

        panel.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        card2.setColor1(new java.awt.Color(186, 123, 247));
        card2.setColor2(new java.awt.Color(167, 94, 236));

        Item_Update_log.setColumns(20);
        Item_Update_log.setRows(5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE))
                        .addGap(20, 20, 20))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fM_Card_Profit1, javax.swing.GroupLayout.PREFERRED_SIZE, 875, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fM_Card_Profit1, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(card2, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addGap(23, 23, 23))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea Item_Update_log;
    private com.raven.component.Card card2;
    private com.raven.component.FM_Card_Profit fM_Card_Profit1;
    private javax.swing.JScrollPane jScrollPane1;
    private java.awt.MenuBar menuBar1;
    private javax.swing.JLayeredPane panel;
    // End of variables declaration//GEN-END:variables
}
