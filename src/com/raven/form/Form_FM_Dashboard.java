package com.raven.form;

import com.raven.component.FM_Card_Profit;
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
import javax.swing.table.DefaultTableModel;

public class Form_FM_Dashboard extends javax.swing.JPanel {

    private Timer profitRefreshTimer;
    private Timer POTableRefreshTimer;
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
        cardProfit = FM_Card_Profit;
        loadPOTable();
        POTableAutoRefresh();
        loadItemUpdateLog();
        LogAutoRefresh();
        FM_CardProfitAutoRefresh();
    }
    
    private void updateProfitCard() {
        double revenue     = getTodayRevenue();
        double expenditure = getTodayExpenditure();
        double profit      = revenue - expenditure;

        cardProfit.setTitle("Today Profit");

        cardProfit.setValue(String.format("%.2f", profit));

        cardProfit.setRevenueText(String.format("%.2f", revenue));

        cardProfit.setExpenditureText(String.format("%.2f", expenditure));
    }
 
    private double getTodayRevenue() {
        double totalRevenue = 0.0;
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try (BufferedReader reader = new BufferedReader(new FileReader("daily_sales.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3 && parts[0].equals(today)) {
                    try {
                        totalRevenue = Double.parseDouble(parts[2]);
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalRevenue;
    }

    private double getTodayExpenditure() {
        double totalExpenditure = 0.0;
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try (BufferedReader reader = new BufferedReader(new FileReader("processed_po.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    long timestamp;
                    try {
                        timestamp = Long.parseLong(parts[1]);
                    } catch (NumberFormatException nfe) {
                        continue;
                    }
                    String recordDate = new SimpleDateFormat("yyyy-MM-dd")
                                            .format(new Date(timestamp));
                    if (today.equals(recordDate)) {
                        try {
                            totalExpenditure += Double.parseDouble(parts[5]);
                        } catch (NumberFormatException nfe) {
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalExpenditure;
    }
    
    private void FM_CardProfitAutoRefresh() {
        if (profitRefreshTimer != null && profitRefreshTimer.isRunning()) {
            profitRefreshTimer.stop();
        }
        profitRefreshTimer = new Timer(1000, e -> updateProfitCard());
        profitRefreshTimer.setInitialDelay(0);
        profitRefreshTimer.start();
    }
    
    private void loadPOTable() {
        DefaultTableModel model = (DefaultTableModel) PO_Table_List.getModel();
        model.setRowCount(0);

        int pendingCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("purchase_orders.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    String poid = parts[0];
                    long timestamp = Long.parseLong(parts[1]);
                    String status = parts[3];

                    if ("Pending".equalsIgnoreCase(status.trim())) {
                        String dateRequired = new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp));
                        model.addRow(new Object[]{poid, dateRequired, status});
                        pendingCount++;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        Remaining_PO_lb.setText(pendingCount + " Purchase Order Remaining");
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
        Item_Update_log.setCaretPosition(
            Item_Update_log.getDocument().getLength()
        );
    }

    
    private void LogAutoRefresh() {
        if (logRefreshTimer != null && logRefreshTimer.isRunning()) {
            logRefreshTimer.stop();
        }
        logRefreshTimer = new Timer(30_000, e -> loadItemUpdateLog());
        logRefreshTimer.setInitialDelay(0);
        logRefreshTimer.start();
    }
    
    private void POTableAutoRefresh() {
        if (POTableRefreshTimer != null && POTableRefreshTimer.isRunning()) {
            POTableRefreshTimer.stop();
        }
        POTableRefreshTimer = new Timer(15_000, e -> loadPOTable());
        POTableRefreshTimer.setInitialDelay(0);
        POTableRefreshTimer.start();
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
        FM_Card_Profit = new com.raven.component.FM_Card_Profit();
        jScrollPane1 = new javax.swing.JScrollPane();
        Item_Update_log = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        PO_Table_List = new javax.swing.JTable();
        Remaining_PO_lb = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        panel.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        Item_Update_log.setColumns(20);
        Item_Update_log.setRows(5);
        jScrollPane1.setViewportView(Item_Update_log);

        PO_Table_List.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "POID", "Date Required", "Status"
            }
        ));
        jScrollPane2.setViewportView(PO_Table_List);

        Remaining_PO_lb.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panel)
                        .addGap(20, 20, 20))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(FM_Card_Profit, javax.swing.GroupLayout.PREFERRED_SIZE, 875, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                                    .addComponent(Remaining_PO_lb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(14, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FM_Card_Profit, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Remaining_PO_lb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(23, 23, 23))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.component.FM_Card_Profit FM_Card_Profit;
    private javax.swing.JTextArea Item_Update_log;
    private javax.swing.JTable PO_Table_List;
    private javax.swing.JLabel Remaining_PO_lb;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private java.awt.MenuBar menuBar1;
    private javax.swing.JLayeredPane panel;
    // End of variables declaration//GEN-END:variables
}
