package com.raven.form;

import com.raven.cell.TableActionCellEditor;
import com.raven.cell.TableActionCellRender;
import com.raven.cell.TableActionEvent;
import com.raven.data.Supplier;
import com.raven.data.SupplierRepository;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Form_SM_SupplierManagement extends javax.swing.JPanel {
    private List<Supplier> suppliers = new ArrayList<>();
    private static final String FILE_NAME = "suppliers.txt";
    public boolean hasUnsavedChanges = false;
    
    public Form_SM_SupplierManagement() {
        initComponents();
        loadSuppliersFromFile();
        initTable();
        refreshTable();
    }    
    
    private void initTable(){
        addButton.addActionListener(e -> addNewSupplier());
        saveButton.addActionListener(e -> saveSuppliersToFile());
        
        // Set up table model
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{}, 
            new String[]{"Supplier Code", "Supplier Name", "Contact", "Items Supplied", "Action"}
        );
        table.setModel(model);
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
                        deleteSupplier(row);
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
        searchTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { searchSuppliers(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { searchSuppliers(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { searchSuppliers(); }
        });
    }
    
    private void loadSuppliersFromFile() {
        suppliers.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4); // Split into max 4 parts
                if (parts.length >= 4) {
                    // Join any extra parts beyond index 3 back together as items supplied
                    String itemsSupplied = parts.length > 4 ? 
                        String.join(",", Arrays.copyOfRange(parts, 3, parts.length)) : 
                        parts[3];
                    suppliers.add(new Supplier(parts[0], parts[1], parts[2], itemsSupplied));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading suppliers: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean saveSuppliersToFile(boolean silent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Supplier supplier : suppliers) {
                writer.write(supplier.toFileString());
                writer.newLine();
            }
            hasUnsavedChanges = false;
            if (!silent) {
                JOptionPane.showMessageDialog(this, "Suppliers saved successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving suppliers: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void saveSuppliersToFile() {
        saveSuppliersToFile(false);  // Default to showing messages
    }
    
    public boolean checkUnsavedChanges() {
        if (!hasUnsavedChanges) {
            return true; 
        }
        return true; // Always return true since we removed the cancel option
    }
    
    public void reloadUnsavedData() {
        // Refresh the table with current unsaved data
        refreshTable();
    }
    
    private void refreshTable() {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    model.setRowCount(0);
    
    try {
        List<Supplier> suppliers = SupplierRepository.loadSuppliers();
        for (Supplier supplier : suppliers) {
            model.addRow(new Object[]{
                supplier.getCode(),
                supplier.getName(),
                supplier.getContact(),
                formatItemsSupplied(supplier.getItemsSupplied()),
                "" // Action column
            });
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, 
            "Error loading supplier data: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void searchSuppliers() {
        String searchText = searchTextField.getText().toLowerCase().trim();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        for (Supplier supplier : suppliers) {
            if (searchText.isEmpty() ||
                supplier.getCode().toLowerCase().contains(searchText) ||
                supplier.getName().toLowerCase().contains(searchText) ||
                supplier.getContact().toLowerCase().contains(searchText) ||
                supplier.getItemsSupplied().toLowerCase().contains(searchText)) {
                
                model.addRow(new Object[]{
                    supplier.getCode(),
                    supplier.getName(),
                    supplier.getContact(),
                    supplier.getItemsSupplied(),
                    ""
                });
            }
        }
    }
    
    private String generateNextSupplierCode() {
        if (suppliers.isEmpty()) {
            return "SUP001";
        }
        
        String lastCode = suppliers.get(suppliers.size() - 1).getCode();
        try {
            int number = Integer.parseInt(lastCode.substring(3));
            return String.format("SUP%03d", number + 1);
        } catch (NumberFormatException e) {
            return "SUP001";
        }
    }
    
    private void showEditDialog(int row) {
    // Get the supplier code from the table (this works for both filtered and unfiltered views)
    String supplierCode = (String) table.getModel().getValueAt(row, 0);
    
    // Find supplier by code instead of by row index
    Supplier supplier = null;
    for (Supplier s : suppliers) {
        if (s.getCode().equals(supplierCode)) {
            supplier = s;
            break;
        }
    }
    
    if (supplier == null) {
        JOptionPane.showMessageDialog(this, "Supplier not found!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Create final reference for use in lambda
    final Supplier finalSupplier = supplier;
    
    JDialog editDialog = new JDialog();
    editDialog.setTitle("Edit Supplier");
    editDialog.setModal(true);
    editDialog.setSize(400, 300);
    editDialog.setLayout(new GridLayout(5, 2, 10, 10));
    
    JLabel codeLabel = new JLabel("Supplier Code:");
    JTextField codeField = new JTextField(supplier.getCode());
    codeField.setEditable(false);
    
    JLabel nameLabel = new JLabel("Supplier Name:");
    JTextField nameField = new JTextField(supplier.getName());
    
    JLabel contactLabel = new JLabel("Contact:");
    JTextField contactField = new JTextField(supplier.getContact());
    
    JLabel itemsLabel = new JLabel("Items Supplied:");
    JTextField itemsField = new JTextField(formatItemsSupplied(supplier.getItemsSupplied()));
    itemsField.setEditable(false); // Make this field non-editable
    itemsField.setToolTipText("Items are managed through the Item Entry form");
    
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    
    editDialog.add(codeLabel);
    editDialog.add(codeField);
    editDialog.add(nameLabel);
    editDialog.add(nameField);
    editDialog.add(contactLabel);
    editDialog.add(contactField);
    editDialog.add(itemsLabel);
    editDialog.add(itemsField);
    editDialog.add(saveButton);
    editDialog.add(cancelButton);
    
    saveButton.addActionListener(e -> {
        if (nameField.getText().trim().isEmpty() || 
            contactField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(editDialog, "Name and Contact are required!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Use finalSupplier instead of supplier
        finalSupplier.setName(nameField.getText().trim());
        finalSupplier.setContact(contactField.getText().trim());
        // Note: We don't update itemsSupplied here
        
        hasUnsavedChanges = true;
        refreshTable();
        editDialog.dispose();
    });
    
    cancelButton.addActionListener(e -> editDialog.dispose());
    editDialog.setLocationRelativeTo(this);
    editDialog.setVisible(true);
}

    private String formatItemsSupplied(String itemsSupplied) {
    if (itemsSupplied == null || itemsSupplied.isEmpty()) {
        return "No items assigned";
    }
    return itemsSupplied.replace(",", ", ");
}
    
    private void addNewSupplier() {
        loadSuppliersFromFile();
        JDialog addDialog = new JDialog();
        addDialog.setTitle("Add New Supplier");
        addDialog.setModal(true);
        addDialog.setSize(400, 300);
        addDialog.setLayout(new GridLayout(5, 2, 10, 10));
        
        JLabel codeLabel = new JLabel("Supplier Code:");
        JTextField codeField = new JTextField(generateNextSupplierCode());
        codeField.setEditable(false);
        
        JLabel nameLabel = new JLabel("Supplier Name:");
        JTextField nameField = new JTextField();
        
        JLabel contactLabel = new JLabel("Contact:");
        JTextField contactField = new JTextField();
        
        JLabel itemsLabel = new JLabel("Items Supplied:");
        JTextField itemsField = new JTextField();
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        addDialog.add(codeLabel);
        addDialog.add(codeField);
        addDialog.add(nameLabel);
        addDialog.add(nameField);
        addDialog.add(contactLabel);
        addDialog.add(contactField);
        addDialog.add(saveButton);
        addDialog.add(cancelButton);
        
        saveButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty() || 
                contactField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(addDialog, "Name and Contact are required!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Supplier newSupplier = new Supplier(
                codeField.getText().trim(),
                nameField.getText().trim(),
                contactField.getText().trim(),
                itemsField.getText().trim()
            );
            
            suppliers.add(newSupplier);
            hasUnsavedChanges = true;
            refreshTable();
            addDialog.dispose();
        });
        
        refreshTable();
        cancelButton.addActionListener(e -> addDialog.dispose());
        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }
    
    private void deleteSupplier(int row) {
        String supplierCode = (String) table.getModel().getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this supplier?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Find and remove by code instead of by row index
            suppliers.removeIf(s -> s.getCode().equals(supplierCode));
            hasUnsavedChanges = true;
            refreshTable();
        }
    }

    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        searchTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Supplier Code", "Supplier Name", "Contact", "Items Supplied", "Action"
            }
        ));
        table.setRowHeight(35);
        jScrollPane1.setViewportView(table);

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 153, 153));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/search.png"))); // NOI18N
        jLabel3.setText("Search");

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("Supplier Management");

        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/AddItem.png"))); // NOI18N

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/Save.png"))); // NOI18N

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/refresh.png"))); // NOI18N
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 127, Short.MAX_VALUE)
                        .addComponent(refreshButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 128, Short.MAX_VALUE)
                        .addComponent(saveButton))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 446, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(refreshButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        refreshTable();
    }//GEN-LAST:event_refreshButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
