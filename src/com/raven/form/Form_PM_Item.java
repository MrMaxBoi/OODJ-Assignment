package com.raven.form;

import javax.swing.table.DefaultTableModel;
import com.raven.data.SupplierRepository;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;



public class Form_PM_Item extends javax.swing.JPanel {
    private List<Item> items = new ArrayList<>();
    private static final String FILE_NAME = "items.txt";
    public boolean hasUnsavedChanges = false;
    private Map<String, String> supplierMap; // Maps display text to supplier code
    
    private class Item {
        String code;
        String name;
        String supplier;
        String unitPrice;
        
        public Item(String code, String name, String supplier, String unitPrice) {
            this.code = code;
            this.name = name;
            this.supplier = supplier;
            this.unitPrice = unitPrice;
        }
        
        public String toFileString() {
            return String.join(",", code, name, supplier, unitPrice);
        }
    }
    
    public Form_PM_Item() {
        initComponents();
        loadSuppliers();
        loadItemsFromFile();
        initTable();
    }

    private void loadSuppliers() {
        try {
            supplierMap = SupplierRepository.getSupplierNameCodeMap();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading suppliers: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            supplierMap = new HashMap<>();
        }
    }
    
    private void initTable() {
        // Set up table model
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{}, 
            new String[]{"Item Code", "Item Name", "Supplier", "Unit Price"}
        );
        table.setModel(model);
        refreshTable();

        // Set up search
        jTextField1.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { searchItems(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { searchItems(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { searchItems(); }
        });
    }

    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear the table

        for (Item item : items) {
            model.addRow(new Object[]{
                item.code,
                item.name,
                item.supplier,
                item.unitPrice,
                "" // Action column
            });
        }
    }
    
    public boolean checkUnsavedChanges() {
        if (!hasUnsavedChanges) {
            return true; // No changes, can proceed
        }
        return true; // Always return true since we removed the cancel option
    }

    public void reloadUnsavedData() {
        refreshTable();
    }
    
    private void loadItemsFromFile() {
        items.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Items file not found at: " + file.getAbsolutePath(), 
                "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                // Debug: Log the line being processed
                System.out.println("Line " + lineNumber + ": " + line);

                // Handle lines with at least 4 fields
                if (parts.length >= 4) {
                    try {
                        // Validate fields
                        String code = parts[0].trim();
                        String name = parts[1].trim();
                        String supplier = parts[2].trim();
                        String unitPrice = parts[3].trim();

                        // Check if supplier exists in supplierMap
                        if (supplierMap.containsValue(supplier)) {
                            items.add(new Item(code, name, supplier, unitPrice));
                        } else {
                            System.out.println("Skipping item at line " + lineNumber + ": Invalid supplier code " + supplier);
                        }
                    } catch (Exception e) {
                        System.out.println("Error parsing line " + lineNumber + ": " + e.getMessage());
                    }
                } else {
                    System.out.println("Skipping line " + lineNumber + ": Expected at least 4 fields, found " + parts.length);
                }
            }

            // Sort items by code
            items.sort((a, b) -> {
                try {
                    int numA = Integer.parseInt(a.code.substring(2));
                    int numB = Integer.parseInt(b.code.substring(2));
                    return Integer.compare(numA, numB);
                } catch (NumberFormatException e) {
                    return 0;
                }
            });

            // Debug: Log number of items loaded
            System.out.println("Loaded " + items.size() + " items from " + FILE_NAME);

            // Force table refresh
            refreshTable();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchItems() {
        String searchText = jTextField1.getText().toLowerCase().trim();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear the table

        for (Item item : items) {
            if (searchText.isEmpty() ||
                item.code.toLowerCase().contains(searchText) ||
                item.name.toLowerCase().contains(searchText) ||
                item.supplier.toLowerCase().contains(searchText) ||
                item.unitPrice.toLowerCase().contains(searchText)) {

                model.addRow(new Object[]{
                    item.code,
                    item.name,
                    item.supplier,
                    item.unitPrice,
                    "" // Action column
                });
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Item Code", "Item Name", "Supplier", "Unit Price", "Action"
            }
        ));
        table.setRowHeight(35);
        jScrollPane1.setViewportView(table);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("Item Entry");

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 153, 153));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/search.png"))); // NOI18N
        jLabel3.setText("Search");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 742, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 446, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1KeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
