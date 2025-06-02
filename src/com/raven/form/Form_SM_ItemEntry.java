package com.raven.form;

import com.raven.cell.TableActionCellRender;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.raven.cell.TableActionCellEditor;
import com.raven.cell.TableActionEvent;
import com.raven.data.Supplier;
import com.raven.data.SupplierRepository;
import com.raven.model.StatusType;
import com.raven.swing.ScrollBar;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;



public class Form_SM_ItemEntry extends javax.swing.JPanel {
    private List<Item> items = new ArrayList<>();
    private static final String FILE_NAME = "items.txt";
    public boolean hasUnsavedChanges = false;
    private Map<String, String> supplierMap; // Maps display text to supplier code
    
private class Item {
    String code;
    String name;
    String supplier;
    String unitPrice;
    String currentStock;  // Added as String to preserve original format
    String maxStock;
    String lowStockLevel;
    
    public Item(String code, String name, String supplier, String unitPrice, 
               String currentStock, String maxStock, String lowStockLevel) {
        this.code = code;
        this.name = name;
        this.supplier = supplier;
        this.unitPrice = unitPrice;
        this.currentStock = currentStock;
        this.maxStock = maxStock;
        this.lowStockLevel = lowStockLevel;
    }
    
    // Modified toFileString to include all fields
    public String toFileString() {
        return String.join(",", 
            code, 
            name, 
            supplier, 
            unitPrice,
            currentStock != null ? currentStock : "0",
            maxStock != null ? maxStock : "100",
            lowStockLevel != null ? lowStockLevel : "10"
        );
    }
}
    
    public Form_SM_ItemEntry() {
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
        jButton2.addActionListener(e -> addNewItem());
        jButton3.addActionListener(e -> saveItemsToFile());

        // Set up table model
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{}, 
            new String[]{"Item Code", "Item Name", "Supplier", "Unit Price", "Action"}
        );
        table.setModel(model);
        jTextField1.setText("");
        refreshTable();

        // Set up action buttons
        TableActionEvent event = new TableActionEvent() {
            @Override
            public void onAction(int row, String actionCommand) {
                switch (actionCommand) {
                    case "Edit":
                        showEditDialog(row);
                        break;
                    case "Delete":
                        deleteItem(row);
                        break;
                }
            }
        };

        String[] buttonNames = {"Edit", "Delete"};
        String[] icons = {
            "/com/raven/icon/edit.png",
            "/com/raven/icon/delete.png"
        };

        table.getColumnModel().getColumn(4).setCellRenderer(
            new TableActionCellRender(buttonNames, icons));
        table.getColumnModel().getColumn(4).setCellEditor(
            new TableActionCellEditor(event, buttonNames, icons));

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
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(",");
            System.out.println("Line " + lineNumber + ": " + line);

            if (parts.length >= 4) {
                try {
                    String code = parts[0].trim();
                    String name = parts[1].trim();
                    String supplier = parts[2].trim();
                    String unitPrice = parts[3].trim();
                    
                    // Handle different formats
                    String currentStock = parts.length > 4 ? parts[4].trim() : "0";
                    String maxStock = parts.length > 5 ? parts[5].trim() : "100";
                    String lowStockLevel = parts.length > 6 ? parts[6].trim() : "10";

                    if (supplierMap.containsValue(supplier)) {
                        items.add(new Item(code, name, supplier, unitPrice, 
                                         currentStock, maxStock, lowStockLevel));
                    } else {
                        System.out.println("Skipping item at line " + lineNumber + 
                                         ": Invalid supplier code " + supplier);
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing line " + lineNumber + ": " + e.getMessage());
                }
            } else {
                System.out.println("Skipping line " + lineNumber + 
                                 ": Expected at least 4 fields, found " + parts.length);
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

        System.out.println("Loaded " + items.size() + " items from " + FILE_NAME);
        refreshTable();

    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    public boolean saveItemsToFile(boolean silent) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
        for (Item item : items) {
            writer.write(item.toFileString());
            writer.newLine();
        }
        hasUnsavedChanges = false;
        if (!silent) {
            JOptionPane.showMessageDialog(this, "Items saved successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        return true;
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error saving items: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}
    
    private void saveItemsToFile() {
        saveItemsToFile(false);
    }

    private void showEditDialog(int row) {
    // Get the item code from the filtered table
    String itemCode = (String) table.getModel().getValueAt(row, 0);
    
    // Find the actual item in the original list
    Item item = null;
    for (Item i : items) {
        if (i.code.equals(itemCode)) {
            item = i;
            break;
        }
    }
    
    if (item == null) {
        JOptionPane.showMessageDialog(this, "Item not found!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Create final reference for lambda
    final Item finalItem = item;
    
    JDialog editDialog = new JDialog();
    editDialog.setTitle("Edit Item");
    editDialog.setModal(true);
    editDialog.setSize(400, 300);
    editDialog.setLayout(new GridLayout(5, 2, 10, 10));

    JLabel codeLabel = new JLabel("Item Code:");
    JTextField codeField = new JTextField(finalItem.code);
    codeField.setEditable(false);

    JLabel nameLabel = new JLabel("Item Name:");
    JTextField nameField = new JTextField(finalItem.name);
    
    JLabel supplierLabel = new JLabel("Supplier:");
    JComboBox<String> supplierCombo = new JComboBox<>();
    // Populate the combo box with supplier names and codes
    String currentlySelected = "";
    for (String displayText : supplierMap.keySet()) {
        supplierCombo.addItem(displayText);
        if (supplierMap.get(displayText).equals(finalItem.supplier)) {
            currentlySelected = displayText;
        }
    }
    supplierCombo.setSelectedItem(currentlySelected);
    
    JLabel priceLabel = new JLabel("Unit Price:");
    JTextField priceField = new JTextField(finalItem.unitPrice);

    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");

    editDialog.add(codeLabel);
    editDialog.add(codeField);
    editDialog.add(nameLabel);
    editDialog.add(nameField);
    editDialog.add(supplierLabel);
    editDialog.add(supplierCombo);
    editDialog.add(priceLabel);
    editDialog.add(priceField);
    editDialog.add(saveButton);
    editDialog.add(cancelButton);

    saveButton.addActionListener(e -> {
        if (codeField.getText().trim().isEmpty() || 
            nameField.getText().trim().isEmpty() ||
            supplierCombo.getSelectedItem() == null ||
            priceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(editDialog, "All fields are required!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the selected supplier code from the map
        String selectedDisplay = (String) supplierCombo.getSelectedItem();
        String newSupplierCode = supplierMap.get(selectedDisplay);
        String oldSupplierCode = finalItem.supplier;
        
        try {
            // If supplier changed, update both old and new suppliers
            if (!newSupplierCode.equals(oldSupplierCode)) {
                Supplier oldSupplier = SupplierRepository.findSupplierByCode(oldSupplierCode);
                if (oldSupplier != null) {
                    oldSupplier.removeItemSupplied(finalItem.code);
                    SupplierRepository.updateSupplier(oldSupplier);
                }
                
                Supplier newSupplier = SupplierRepository.findSupplierByCode(newSupplierCode);
                if (newSupplier != null) {
                    newSupplier.addItemSupplied(finalItem.code);
                    SupplierRepository.updateSupplier(newSupplier);
                }
            }

            // Update only the editable fields (first 4), preserve the stock fields
            finalItem.code = codeField.getText().trim();
            finalItem.name = nameField.getText().trim();
            finalItem.supplier = newSupplierCode;
            finalItem.unitPrice = priceField.getText().trim();

            hasUnsavedChanges = true;
            refreshTable();
            editDialog.dispose();
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(editDialog, 
                "Error updating supplier data: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    cancelButton.addActionListener(e -> editDialog.dispose());
    editDialog.setLocationRelativeTo(this);
    editDialog.setVisible(true);
}
    
    private String generateNextItemCode() {
        if (items.isEmpty()) {
            return "IC001"; // First item
        }

        // Get last item's code
        String lastCode = items.get(items.size() - 1).code;

        // Extract the numeric part
        try {
            int number = Integer.parseInt(lastCode.substring(2));
            return String.format("IC%03d", number + 1);
        } catch (NumberFormatException e) {
            // If format is wrong, start fresh
            return "IC001";
        }
    }

    private void deleteItem(int row) {
    Item itemToDelete = items.get(row);
    
    int confirm = JOptionPane.showConfirmDialog(this, 
        "Are you sure you want to delete this item?", 
        "Confirm Delete", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            // Remove item from supplier's list
            Supplier supplier = SupplierRepository.findSupplierByCode(itemToDelete.supplier);
            if (supplier != null) {
                supplier.removeItemSupplied(itemToDelete.code);
                SupplierRepository.updateSupplier(supplier);
            }
            
            items.remove(row);
            hasUnsavedChanges = true;
            refreshTable();
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error updating supplier data: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

    private void addNewItem() {
    JDialog addDialog = new JDialog();
    addDialog.setTitle("Add New Item");
    addDialog.setModal(true);
    addDialog.setSize(400, 300);
    addDialog.setLayout(new GridLayout(5, 2, 10, 10));

    JLabel codeLabel = new JLabel("Item Code:");
    JTextField codeField = new JTextField(generateNextItemCode());
    codeField.setEditable(false);

    JLabel nameLabel = new JLabel("Item Name:");
    JTextField nameField = new JTextField();

    JLabel supplierLabel = new JLabel("Supplier:");
    JComboBox<String> supplierCombo = new JComboBox<>();
    // Populate the combo box with supplier names and codes
    for (String displayText : supplierMap.keySet()) {
        supplierCombo.addItem(displayText);
    }

    JLabel priceLabel = new JLabel("Unit Price:");
    JTextField priceField = new JTextField();

    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");

    addDialog.add(codeLabel);
    addDialog.add(codeField);
    addDialog.add(nameLabel);
    addDialog.add(nameField);
    addDialog.add(supplierLabel);
    addDialog.add(supplierCombo);
    addDialog.add(priceLabel);
    addDialog.add(priceField);
    addDialog.add(saveButton);
    addDialog.add(cancelButton);

    saveButton.addActionListener(e -> {
        if (codeField.getText().trim().isEmpty() || 
            nameField.getText().trim().isEmpty() ||
            supplierCombo.getSelectedItem() == null ||
            priceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(addDialog, "All fields are required!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the selected supplier code from the map
        String selectedDisplay = (String) supplierCombo.getSelectedItem();
        String supplierCode = supplierMap.get(selectedDisplay);

        try {
            // Add new item with default stock values
            Item newItem = new Item(
                codeField.getText().trim(),
                nameField.getText().trim(),
                supplierCode,
                priceField.getText().trim(),
                "0",    // Default current stock
                "100",  // Default max stock
                "10"    // Default low stock level
            );

            // Update supplier's items list
            Supplier supplier = SupplierRepository.findSupplierByCode(supplierCode);
            if (supplier != null) {
                supplier.addItemSupplied(newItem.code);
                SupplierRepository.updateSupplier(supplier);
            }

            items.add(newItem);
            hasUnsavedChanges = true;
            refreshTable();
            addDialog.dispose();
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(addDialog, 
                "Error updating supplier data: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    cancelButton.addActionListener(e -> addDialog.dispose());
    addDialog.setLocationRelativeTo(this);
    addDialog.setVisible(true);
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
        jButton3 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

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
        jLabel1.setText("Item Entry Management");

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/Save.png"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 153, 153));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/search.png"))); // NOI18N
        jLabel3.setText("Search");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/AddItem.png"))); // NOI18N

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/refresh.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
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
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addContainerGap(14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        loadSuppliers();
        loadItemsFromFile();
    }//GEN-LAST:event_jButton4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
