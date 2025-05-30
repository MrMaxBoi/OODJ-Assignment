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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;




public class Form_IM_ItemEntry extends javax.swing.JPanel {
    private List<Item> items = new ArrayList<>();
    private static final String FILE_NAME = "items.txt";
    private static final String LOG_FILE_NAME = "logs.txt";
    public boolean hasUnsavedChanges = false;
    private Map<String, String> supplierMap; // Maps display text to supplier code
    
    
    private class Item {
        String code;
        String name;
        String supplier;
        String unitPrice;
        int currentStock;
        int maxStock;
        int lowStockLevel;
        
        public Item(String code, String name, String supplier, String unitPrice, int currentStock, int maxStock, int lowStockLevel) {
            this.code = code;
            this.name = name;
            this.supplier = supplier;
            this.unitPrice = unitPrice;
            this.currentStock = currentStock;
            this.maxStock = maxStock;
            this.lowStockLevel = lowStockLevel; 
        }
        
        public String toFileString() {
            return String.join(",", code, name, supplier, unitPrice, 
                              String.valueOf(currentStock),
                              String.valueOf(maxStock), 
                              String.valueOf(lowStockLevel));
        }
    }
    
    public Form_IM_ItemEntry() {
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
        jButton3.addActionListener(e -> saveItemsToFile());
        // New button for stock report
      

        // Set up table model with stock columns
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{}, 
            new String[]{"Item Code", "Item Name", "Supplier", "Unit Price", "Current Stock", "Max Stock", "Low Stock Level", "Action"}
        );
        table.setModel(model);
        refreshTable();

        // Set up action buttons (plus, minus, edit)
        TableActionEvent event = new TableActionEvent() {
            @Override
            public void onAction(int row, String actionCommand) {
                switch (actionCommand) {
                    case "plus":
                        incrementStock(row);
                        break;
                    case "minus":
                        decrementStock(row);
                        break;
                    case "edit":
                        showEditDialog(row);
                        break;
                }
            }
        };

        String[] buttonNames = {"plus", "minus", "edit"};
        String[] icons = {
            "/com/raven/icon/plus.png",
            "/com/raven/icon/minus.png",
            "/com/raven/icon/edit.png"
        };

        table.getColumnModel().getColumn(7).setCellRenderer(
            new TableActionCellRender(buttonNames, icons));
        table.getColumnModel().getColumn(7).setCellEditor(
            new TableActionCellEditor(event, buttonNames, icons));

        // Add search functionality
        jTextField1.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { searchItems(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { searchItems(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { searchItems(); }
        });
    }
    
   
   
    
    // Add this method to log changes
    private void logChange(String action, String itemName, String details) {
        String timestamp = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
        String logEntry = String.format("%s - %s: %s (%s)", timestamp, action, itemName, details);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_NAME, true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to log file: " + e.getMessage(),
                "Log Error", JOptionPane.ERROR_MESSAGE);
        }
    }
   
    // Add this method to show the log viewer
        private void showUpdateLog() {
            JDialog logDialog = new JDialog();
            logDialog.setTitle("Updated Log");
            logDialog.setModal(true);
            logDialog.setSize(800, 500);
            logDialog.setLayout(new BorderLayout());

            // Create text area for logs
            JTextArea logTextArea = new JTextArea();
            logTextArea.setEditable(false);
            logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(logTextArea);

    // Load log content
        try {
            File logFile = new File(LOG_FILE_NAME);
            if (logFile.exists()) {
                List<String> logLines = java.nio.file.Files.readAllLines(logFile.toPath());
                Collections.reverse(logLines); // Show newest first
                logTextArea.setText(String.join("\n", logLines));
            } else {
                logTextArea.setText("No update logs found.");
            }
        } catch (IOException e) {
            logTextArea.setText("Error loading logs: " + e.getMessage());
        }

        // Add refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            try {
                File logFile = new File(LOG_FILE_NAME);
                if (logFile.exists()) {
                    List<String> logLines = java.nio.file.Files.readAllLines(logFile.toPath());
                    Collections.reverse(logLines);
                    logTextArea.setText(String.join("\n", logLines));
                }
            } catch (IOException ex) {
                logTextArea.setText("Error refreshing logs: " + ex.getMessage());
            }
        });

        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> logDialog.dispose());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        // Add components to dialog
        logDialog.add(scrollPane, BorderLayout.CENTER);
        logDialog.add(buttonPanel, BorderLayout.SOUTH);

        logDialog.setLocationRelativeTo(this);
        logDialog.setVisible(true);
    }
    
    
    
    private void generateStockReport() {
    // Create the dialog
    JDialog reportDialog = new JDialog();
    reportDialog.setTitle("Stock Level Report");
    reportDialog.setModal(true);
    reportDialog.setSize(900, 600);
    reportDialog.setLayout(new BorderLayout());
    
    // Create table model for report
    DefaultTableModel reportModel = new DefaultTableModel(
         
        new String[]{
            "Item Code", 
            "Item Name", 
            "Current Stock", 
            "Max Stock", 
            "Low Level", 
            "Stock Status",
            "Fill Rate (%)"
        },
        0
    );
    
    
    // Populate the report data
    for (Item item : items) {
        // Calculate fill rate percentage
        double fillRate = (item.currentStock * 100.0) / item.maxStock;
        
        // Determine stock status
        String status;
        Color statusColor;
        if (item.currentStock <= item.lowStockLevel) {
            status = "LOW";
            statusColor = Color.RED;
        } else if (item.currentStock >= item.maxStock) {
            status = "HIGH";
            statusColor = Color.GREEN;
        } else {
            status = "NORMAL";
            statusColor = Color.BLUE;
        }
        
        reportModel.addRow(new Object[]{
            item.code,
            item.name,
            item.currentStock,
            item.maxStock,
            item.lowStockLevel,
            status,
            String.format("%.1f", fillRate)
        });
    }
    
    // Create the table with custom renderer for status
    JTable reportTable = new JTable(reportModel);
    reportTable.setRowHeight(30);
    reportTable.setAutoCreateRowSorter(true);
    
    // Custom renderer for status column
    reportTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = (String) value;
            if ("LOW".equals(status)) {
                c.setForeground(Color.RED);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            } else if ("HIGH".equals(status)) {
                c.setForeground(Color.GREEN);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            } else {
                c.setForeground(Color.BLUE);
            }
            setHorizontalAlignment(JLabel.CENTER);
            return c;
        }
    });
    
    // Add table to scroll pane
    JScrollPane scrollPane = new JScrollPane(reportTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    // Summary panel
    JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 10));
    summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    // Calculate summary statistics
    long lowStockCount = items.stream().filter(i -> i.currentStock <= i.lowStockLevel).count();
    long normalStockCount = items.stream().filter(i -> 
        i.currentStock > i.lowStockLevel && i.currentStock < i.maxStock).count();
    long highStockCount = items.stream().filter(i -> i.currentStock >= i.maxStock).count();
    
    summaryPanel.add(createSummaryPanel("Low Stock Items", String.valueOf(lowStockCount), Color.RED));
    summaryPanel.add(createSummaryPanel("Normal Stock Items", String.valueOf(normalStockCount), Color.BLUE));
    summaryPanel.add(createSummaryPanel("High Stock Items", String.valueOf(highStockCount), Color.GREEN));
    
    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton printButton = new JButton("Print Report");
    JButton closeButton = new JButton("Close");
    
    printButton.addActionListener(e -> {
        try {
            reportTable.print();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(reportDialog, 
                "Error printing report: " + ex.getMessage(),
                "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    });
    
    closeButton.addActionListener(e -> reportDialog.dispose());
    
    buttonPanel.add(printButton);
    buttonPanel.add(closeButton);
    
    // Add components to dialog
    reportDialog.add(summaryPanel, BorderLayout.NORTH);
    reportDialog.add(scrollPane, BorderLayout.CENTER);
    reportDialog.add(buttonPanel, BorderLayout.SOUTH);
    
    reportDialog.setLocationRelativeTo(this);
    reportDialog.setVisible(true);
}

    private JPanel createSummaryPanel(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(color);

        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }
    
    
    // New methods for stock management
    private void incrementStock(int row) {
        if (row >= 0 && row < items.size()){
        Item item = items.get(row);
        int oldValue = item.currentStock;
        item.currentStock++;
        hasUnsavedChanges = true;
        refreshTable();
        logChange("Stock Increased", item.name, 
            String.format("Quantity changed from %d to %d", oldValue, item.currentStock));
        }
    }
    
    private void decrementStock(int row) {
        if (row >= 0 && row < items.size()) {
        Item item = items.get(row);
            if (item.currentStock > 0) {
                int oldValue = item.currentStock;
                item.currentStock--;
                hasUnsavedChanges = true;
                refreshTable();
                logChange("Stock Decreased", item.name, 
                String.format("Quantity changed from %d to %d", oldValue, item.currentStock));
            } else {
                JOptionPane.showMessageDialog(this, "Stock cannot be negative", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Updated refreshTable to show stock info
    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Item item : items) {
            model.addRow(new Object[]{
                item.code,
                item.name,
                item.supplier,
                item.unitPrice,
                item.currentStock,
                item.maxStock,
                item.lowStockLevel,
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
    
    // Modified file handling methods to use comma format
    private void loadItemsFromFile() {
        items.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    // New format with stock info
                    items.add(new Item(parts[0], parts[1], parts[2], parts[3],
                        Integer.parseInt(parts[4]), 
                        Integer.parseInt(parts[5]), 
                        Integer.parseInt(parts[6])));
                } else if (parts.length == 4) {
                    // Old format - initialize with default stock values
                    items.add(new Item(parts[0], parts[1], parts[2], parts[3],
                        0, 100, 10)); // Default values
                }
            }

            // Sort items by code
            items.sort((a, b) -> a.code.compareTo(b.code));
        } catch (IOException | NumberFormatException e) {
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
    
    // Updated showEditDialog to match your requirements
    private void showEditDialog(int row) {
        Item item = items.get(row);

        JDialog editDialog = new JDialog();
        editDialog.setTitle("Edit Item Stock Details");
        editDialog.setModal(true);
        editDialog.setSize(800, 400);
        editDialog.setResizable(false);
        editDialog.setLayout(new GridLayout(9, 2, 5, 5));

        // Item Code (read-only)
        JLabel codeLabel = new JLabel("Item Code:");
        codeLabel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 0));
        JTextField codeField = new JTextField(item.code);
        codeField.setEditable(false);

        // Item Name (read-only)
        JLabel nameLabel = new JLabel("Item Name:");
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 0));
        JTextField nameField = new JTextField(item.name);
        nameField.setEditable(false);

        // Supplier (read-only)
        JLabel supplierLabel = new JLabel("Supplier:");
        supplierLabel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 0));
        JTextField supplierField = new JTextField(item.supplier);
        supplierField.setEditable(false);

        // Unit Price (read-only)
        JLabel priceLabel = new JLabel("Unit Price:");
        priceLabel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 0));
        JTextField priceField = new JTextField(item.unitPrice);
        priceField.setEditable(false);

        // Stock Quantity
        JLabel stockLabel = new JLabel("Stock Quantity:");
        stockLabel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 0));
        JTextField stockField = new JTextField(String.valueOf(item.currentStock));

        // Max Stock
        JLabel maxStockLabel = new JLabel("Max Stock Quantity:");
        maxStockLabel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 0));
        JTextField maxStockField = new JTextField(String.valueOf(item.maxStock));

        // Low Stock Level
        JLabel lowStockLabel = new JLabel("Low Stock Level:");
        lowStockLabel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 0));
        JTextField lowStockField = new JTextField(String.valueOf(item.lowStockLevel));

        // Stock Level Alert Label
        JLabel alertLabel = new JLabel("Stock Level Alert:");
        alertLabel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 0));
        JLabel alertDesc = new JLabel("When quantity is ≤ " + item.lowStockLevel + 
                                    ", stock is LOW. When ≥ " + item.maxStock + 
                                    ", stock is HIGH.");

        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        // Add components to dialog
        editDialog.add(codeLabel);
        editDialog.add(codeField);
        editDialog.add(nameLabel);
        editDialog.add(nameField);
        editDialog.add(supplierLabel);
        editDialog.add(supplierField);
        editDialog.add(priceLabel);
        editDialog.add(priceField);
        editDialog.add(stockLabel);
        editDialog.add(stockField);
        editDialog.add(maxStockLabel);
        editDialog.add(maxStockField);
        editDialog.add(lowStockLabel);
        editDialog.add(lowStockField);
        editDialog.add(alertLabel);
        editDialog.add(alertDesc);
        editDialog.add(saveButton);
        editDialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            try {
                item.currentStock = Integer.parseInt(stockField.getText());
                item.maxStock = Integer.parseInt(maxStockField.getText());
                item.lowStockLevel = Integer.parseInt(lowStockField.getText());
                int oldStock = item.currentStock;
                int oldMaxStock = item.maxStock;
                int oldLowLevel = item.lowStockLevel;
                
                
                if (oldStock != item.currentStock) {
                    logChange("Stock Updated", item.name, 
                        String.format("Quantity changed from %d to %d", oldStock, item.currentStock));
                }
                if (oldMaxStock != item.maxStock) {
                    logChange("Max Stock Updated", item.name, 
                        String.format("Changed from %d to %d", oldMaxStock, item.maxStock));
                }
                if (oldLowLevel != item.lowStockLevel) {
                    logChange("Low Level Updated", item.name, 
                        String.format("Changed from %d to %d", oldLowLevel, item.lowStockLevel));
                }
                
                hasUnsavedChanges = true;
                refreshTable();
                editDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editDialog, 
                    "Please enter valid numbers for stock fields", 
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
            logChange("Item Deleted", itemToDelete.name, 
                    String.format("Code: %s was removed from inventory", itemToDelete.code));
            
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
        addDialog.setSize(400, 400); // Increased size for new fields
        addDialog.setLayout(new GridLayout(8, 2, 10, 10)); // Updated for new fields

        JLabel codeLabel = new JLabel("Item Code:");
        JTextField codeField = new JTextField(generateNextItemCode());
        codeField.setEditable(false);

        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField();

        JLabel supplierLabel = new JLabel("Supplier:");
        JComboBox<String> supplierCombo = new JComboBox<>();
        for (String displayText : supplierMap.keySet()) {
            supplierCombo.addItem(displayText);
        }

        JLabel priceLabel = new JLabel("Unit Price:");
        JTextField priceField = new JTextField();

        // New stock-related fields
        JLabel stockLabel = new JLabel("Initial Stock:");
        JTextField stockField = new JTextField("0");

        JLabel maxStockLabel = new JLabel("Max Stock:");
        JTextField maxStockField = new JTextField("100");

        JLabel lowStockLabel = new JLabel("Low Stock Level:");
        JTextField lowStockField = new JTextField("10");

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
        addDialog.add(stockLabel);
        addDialog.add(stockField);
        addDialog.add(maxStockLabel);
        addDialog.add(maxStockField);
        addDialog.add(lowStockLabel);
        addDialog.add(lowStockField);
        addDialog.add(saveButton);
        addDialog.add(cancelButton);

        saveButton.addActionListener(e -> {
            if (codeField.getText().trim().isEmpty() || 
                nameField.getText().trim().isEmpty() ||
                supplierCombo.getSelectedItem() == null ||
                priceField.getText().trim().isEmpty() ||
                stockField.getText().trim().isEmpty() ||
                maxStockField.getText().trim().isEmpty() ||
                lowStockField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(addDialog, "All fields are required!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the selected supplier code from the map
            String selectedDisplay = (String) supplierCombo.getSelectedItem();
            String supplierCode = supplierMap.get(selectedDisplay);

            try {
                // Add new item with all 7 parameters
                Item newItem = new Item(
                    codeField.getText().trim(),
                    nameField.getText().trim(),
                    supplierCode,
                    priceField.getText().trim(),
                    Integer.parseInt(stockField.getText().trim()),
                    Integer.parseInt(maxStockField.getText().trim()),
                    Integer.parseInt(lowStockField.getText().trim())
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
                logChange("New Item Added", newItem.name, 
                    String.format("Code: %s, Initial Stock: %d", newItem.code, newItem.currentStock));
                addDialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addDialog, 
                    "Please enter valid numbers for stock fields", 
                    "Error", JOptionPane.ERROR_MESSAGE);
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
        jLabel1.setText("Item Stock Management");

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

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton2.setText("Generate Stock Report");
        jButton2.setToolTipText("");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton4.setText("View Updated Log");
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
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        generateStockReport();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        showUpdateLog();
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
