package com.raven.form;

import com.raven.cell.TableActionCellEditor;
import com.raven.cell.TableActionCellRender;
import com.raven.cell.TableActionEvent;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class Form_SM_DailySalesEntry extends javax.swing.JPanel {
    public boolean hasUnsavedChanges = false;
    public List<Item> items = new ArrayList<>();
    private static final String ITEMS_FILE = "items.txt";
    private static final String SALES_FILE = "daily_sales.txt";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    
    
    private class Item {
        String code;
        String name;
        String supplier;
        double unitPrice;
        
        public Item(String code, String name, String supplier, String unitPrice) {
            this.code = code;
            this.name = name;
            this.supplier = supplier;
            try {
                this.unitPrice = Double.parseDouble(unitPrice);
            } catch (NumberFormatException e) {
                this.unitPrice = 0.0;
            }
        }
        
        @Override
        public String toString() {
            return code + " - " + name;
        }
    }
    
    private class DailySale {
        String date;
        List<SaleItem> items;
        double total;
        
        public DailySale(String date) {
            this.date = date;
            this.items = new ArrayList<>();
            this.total = 0.0;
        }
        
        public void addItem(Item item, int quantity) {
            items.add(new SaleItem(item, quantity));
            total += item.unitPrice * quantity;
        }
        
        public String toFileString() {
            StringBuilder sb = new StringBuilder();
            sb.append(date).append("|");
            for (SaleItem si : items) {
                sb.append(si.item.code).append(",")
                  .append(si.quantity).append(";");
            }
            sb.append("|").append(total);
            return sb.toString();
        }
    }
    
    private class SaleItem {
        Item item;
        int quantity;
        
        public SaleItem(Item item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }
    }
    
    public Form_SM_DailySalesEntry() {
        initComponents();
        loadItemsFromFile();
        initTable();
        loadTodaysSales(); // Load today's existing entries
    }
    
    private void loadItemsFromFile() {
    items.clear();
    File file = new File(ITEMS_FILE);
    if (!file.exists()) {
        return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 4) {
                items.add(new Item(parts[0], parts[1], parts[2], parts[3]));
            }
        }
        
        // Sort items by code
        items.sort((a, b) -> a.code.compareTo(b.code));
        
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void initTable() {
    jButton2.addActionListener(e -> showAddDialog());
    jButton3.addActionListener(e -> saveDailySales());

    // Set up table model
    DefaultTableModel model = new DefaultTableModel(
        new Object[][]{}, 
        new String[]{"Item Code", "Item Name", "Unit Price", "Quantity", "Subtotal", "Action"}
    );
    table.setModel(model);
    
    // Set up action buttons
    TableActionEvent event = new TableActionEvent() {
        @Override
        public void onAction(int row, String actionCommand) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int quantity = Integer.parseInt(model.getValueAt(row, 3).toString());
            double price = Double.parseDouble(model.getValueAt(row, 2).toString());
            
            switch (actionCommand) {
                case "Minus":
                    if (quantity > 0) {
                        model.setValueAt(quantity - 1, row, 3);
                        model.setValueAt((quantity - 1) * price, row, 4);
                    }
                    break;
                case "Plus":
                    model.setValueAt(quantity + 1, row, 3);
                    model.setValueAt((quantity + 1) * price, row, 4);
                    break;
                case "Delete":
                    if (table.isEditing()) {
                        table.getCellEditor().stopCellEditing();
                    }
                    model.removeRow(row);
                    break;
            }
        }
    };
    
    String[] buttonNames = {"Minus", "Plus", "Delete"};
    String[] icons = {
        "/com/raven/icon/minus.png",
        "/com/raven/icon/plus.png",
        "/com/raven/icon/delete.png"
    };
    
    table.getColumnModel().getColumn(5).setCellRenderer(
        new TableActionCellRender(buttonNames, icons));
    table.getColumnModel().getColumn(5).setCellEditor(
        new TableActionCellEditor(event, buttonNames, icons));
}

    private void loadTodaysSales() {
    String today = dateFormat.format(new Date());
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    model.setRowCount(0); // Clear existing entries
    
    File file = new File(SALES_FILE);
    if (!file.exists()) {
        return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length == 3 && parts[0].equals(today)) {
                // This is today's sales record
                String[] itemsData = parts[1].split(";");
                for (String itemData : itemsData) {
                    if (!itemData.isEmpty()) {
                        String[] itemParts = itemData.split(",");
                        if (itemParts.length == 2) {
                            String code = itemParts[0];
                            int quantity = Integer.parseInt(itemParts[1]);
                            
                            // Find the item in our list
                            for (Item item : items) {
                                if (item.code.equals(code)) {
                                    addItemToTable(item, quantity);
                                    break;
                                }
                            }
                        }
                    }
                }
                break; // Found today's record, no need to continue
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error loading sales data: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
        }
    } 

    
    private void showAddDialog() {
        
        loadItemsFromFile();
        JDialog addDialog = new JDialog();
        addDialog.setTitle("Add Daily Sales Entry");
        addDialog.setModal(true);
        addDialog.setSize(400, 300);
        addDialog.setLayout(new GridLayout(5, 2, 10, 10));

        // Create form components
        JLabel itemLabel = new JLabel("Select Item:");
        JComboBox<Item> itemCombo = new JComboBox<>();
        for (Item item : items) {
            itemCombo.addItem(item);
        }

        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField();
        nameField.setEditable(false);

        JLabel priceLabel = new JLabel("Unit Price:");
        JTextField priceField = new JTextField();
        priceField.setEditable(false);

        JLabel quantityLabel = new JLabel("Quantity Sold:");
        JTextField quantityField = new JTextField("1");

        JButton saveButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        // Add action listener to combo box
        itemCombo.addActionListener(e -> {
            Item selected = (Item) itemCombo.getSelectedItem();
            if (selected != null) {
                nameField.setText(selected.name);
                priceField.setText(String.format("%.2f", selected.unitPrice));
            }
        });

        // Initialize fields with first item
        if (itemCombo.getItemCount() > 0) {
            Item firstItem = (Item) itemCombo.getSelectedItem();
            nameField.setText(firstItem.name);
            priceField.setText(String.format("%.2f", firstItem.unitPrice));
        }

        // Add components to dialog
        addDialog.add(itemLabel);
        addDialog.add(itemCombo);
        addDialog.add(nameLabel);
        addDialog.add(nameField);
        addDialog.add(priceLabel);
        addDialog.add(priceField);
        addDialog.add(quantityLabel);
        addDialog.add(quantityField);
        addDialog.add(saveButton);
        addDialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                Item selectedItem = (Item) itemCombo.getSelectedItem();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(addDialog, "Quantity must be positive!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Add to table
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                double subtotal = selectedItem.unitPrice * quantity;
                model.addRow(new Object[]{
                    selectedItem.code,
                    selectedItem.name,
                    String.format("%.2f", selectedItem.unitPrice),
                    quantity,
                    String.format("%.2f", subtotal),
                    "" // Action column
                });

                addDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addDialog, "Please enter a valid quantity!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        

        cancelButton.addActionListener(e -> addDialog.dispose());

        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }
    
    private void saveDailySales() {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "No sales entries to save!", 
            "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    String today = dateFormat.format(new Date());
    DailySale dailySale = new DailySale(today);
    
    // Add all items from table to daily sale
    for (int i = 0; i < model.getRowCount(); i++) {
        String code = model.getValueAt(i, 0).toString();
        int quantity = Integer.parseInt(model.getValueAt(i, 3).toString());
        
        for (Item item : items) {
            if (item.code.equals(code)) {
                dailySale.addItem(item, quantity);
                break;
            }
        }
    }
    
    // First remove any existing record for today
    removeTodaysRecord(today);
    
    // Then save the updated record
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALES_FILE, true))) {
        writer.write(dailySale.toFileString());
        writer.newLine();
        JOptionPane.showMessageDialog(this, "Daily sales updated successfully!", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error saving daily sales: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void removeTodaysRecord(String today) {
    File inputFile = new File(SALES_FILE);
    File tempFile = new File("temp_sales.txt");
    
    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
        
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length > 0 && !parts[0].equals(today)) {
                writer.write(line);
                writer.newLine();
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error updating sales records: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Delete the original file and rename the temp file
    if (inputFile.delete()) {
        tempFile.renameTo(inputFile);
    }
}
    
    private void addItemToTable(Item item, int quantity) {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    
    // Check if item already exists in table
    for (int i = 0; i < model.getRowCount(); i++) {
        String existingCode = model.getValueAt(i, 0).toString();
        if (existingCode.equals(item.code)) {
            // Update existing entry
            int currentQty = Integer.parseInt(model.getValueAt(i, 3).toString());
            model.setValueAt(currentQty + quantity, i, 3);
            model.setValueAt(item.unitPrice * (currentQty + quantity), i, 4);
            return;
        }
    }
    
    // Add new entry
    double subtotal = item.unitPrice * quantity;
    model.addRow(new Object[]{
        item.code,
        item.name,
        String.format("%.2f", item.unitPrice),
        quantity,
        String.format("%.2f", subtotal),
        "" // Action column
    });
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
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, "", null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Item Code", "Item Name", "Stock Available", "Quantity Sold", "Action"
            }
        ));
        table.setRowHeight(35);
        jScrollPane1.setViewportView(table);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("Daily Sales Entry");

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/AddNewEntry.png"))); // NOI18N

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/Save.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 446, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
