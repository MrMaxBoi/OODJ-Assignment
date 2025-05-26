package com.raven.form;

import com.raven.cell.TableActionCellEditor;
import com.raven.cell.TableActionCellRender;
import com.raven.cell.TableActionEvent;
import com.raven.data.POItem;
import com.raven.data.PurchaseOrder;
import com.raven.data.PurchaseRequisition;
import com.raven.data.PurchaseRequisitionRepository;
import java.awt.BorderLayout;
import java.awt.Component;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Form_FM_Payments extends javax.swing.JPanel {
    private List<PurchaseOrder> pos = new ArrayList<>();
    private boolean hasUnsavedChanges = false;
    private String currentUserId;
    
    public Form_FM_Payments(String userId) {
        this.currentUserId = userId;
        initComponents();
        loadPOs();
        initTable();
    }
    
    private void loadPOs() {
        try {
            pos = loadPurchaseOrdersFromFile();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading POs: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private List<PurchaseOrder> loadPurchaseOrdersFromFile() throws IOException {
        List<PurchaseOrder> orders = new ArrayList<>();
        File file = new File("purchase_orders.txt");
        
        if (!file.exists()) {
            return orders;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String poId = parts[0];
                    Date dateRequired = new Date(Long.parseLong(parts[1]));
                    String raisedBy = parts[2];
                    String status = parts[3];
                    
                    // Parse items (format: IC004:6,IC006:6)
                    List<POItem> items = new ArrayList<>();
                    if (!parts[4].isEmpty()) {
                        for (String itemPair : parts[4].split(",")) {
                            String[] itemParts = itemPair.split(":");
                            if (itemParts.length == 2) {
                                items.add(new POItem(itemParts[0], Integer.parseInt(itemParts[1])));
                            }
                        }
                    }
                    
                    orders.add(new PurchaseOrder(poId, dateRequired, raisedBy, status, items));
                }
            }
        }
        return orders;
    }
    
    private void savePOs() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("purchase_orders.txt"))) {
            for (PurchaseOrder po : pos) {
                // Format: PO001|1620000000000|PM001|Pending|IC001:50,IC002:30
                String itemsString = po.getItems().stream()
                    .map(item -> item.getItemCode() + ":" + item.getQuantity())
                    .collect(Collectors.joining(","));
                
                String line = String.join("|",
                    po.getPoId(),
                    String.valueOf(po.getDateRequired().getTime()),
                    po.getRaisedBy(),
                    po.getStatus(),
                    itemsString
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }
    
    private void initTable() {
        // Set up table model
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{}, 
            new String[]{"PO ID", "Date Required", "Items Count", "Raised By", "Status", "Action"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only action column is editable
            }
        };
        
        table.setModel(model);
        refreshTable();

        // Set up action buttons
        TableActionEvent event = new TableActionEvent() {
            @Override
            public void onAction(int row, String actionCommand) {
                switch (actionCommand) {
                    case "View":
                        showViewDialog(row);
                        break;
                    case "Confirm Payments":
                        updatePaymentStatus(row);
                        break;
                }
            }
        };

        String[] buttonNames = {"View","Confirm Payments"};
        String[] icons = {
            "/com/raven/icon/view.png",
            "/com/raven/icon/FM_Payments.png",
        };

        table.getColumnModel().getColumn(5).setCellRenderer(
            new TableActionCellRender(buttonNames, icons));
        table.getColumnModel().getColumn(5).setCellEditor(
            new TableActionCellEditor(event, buttonNames, icons));
    }
    
    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        for (PurchaseOrder po : pos) {
            if ("Approved".equals(po.getStatus())) {
                model.addRow(new Object[]{
                    po.getPoId(),
                    dateFormat.format(po.getDateRequired()),
                    po.getItems().size(),
                    po.getRaisedBy(),
                    po.getStatus(),
                    "" // Action column
                });
            }
        }
    }

    private void showViewDialog(int row) {
        PurchaseOrder po = pos.get(row);
        
        JDialog dialog = new JDialog();
        dialog.setTitle("View Purchase Order - " + po.getPoId());
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        
        // Create view panel (read-only)
        PM_POEditorPanel viewPanel = new PM_POEditorPanel(po, new ArrayList<>());
        viewPanel.setEditable(false);
        dialog.add(viewPanel, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updatePaymentStatus(int row) {
        String targetPoId = (String) table.getValueAt(row,0);
        PurchaseOrder po = pos.stream()
                .filter(p -> p.getPoId().equals(targetPoId))
                .findFirst()
                .orElse(null);
        
        if (po == null) return;
        
        if ("Processed".equals(po.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "Payment already processed!",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirm payment fo " + po.getPoId() + "?",
            "Confirm Payment",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            po.setStatus("Processed");
            hasUnsavedChanges = true;
            refreshTable();
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
        refreshButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "PO ID", "Date Required", "Items Count", "Raised By", "Status", "Action"
            }
        ));
        table.setRowHeight(35);
        jScrollPane1.setViewportView(table);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("Payments");

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/refresh.png"))); // NOI18N
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/Save.png"))); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(refreshButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(saveButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(refreshButton)
                    .addComponent(saveButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        loadPOs();
        refreshTable();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
    try {
            savePOs();
            hasUnsavedChanges = false;
            JOptionPane.showMessageDialog(this, "POs saved successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving POs: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_saveButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
