package com.raven.form;

import com.raven.cell.TableActionCellEditor;
import com.raven.cell.TableActionCellRender;
import com.raven.cell.TableActionEvent;
import com.raven.data.POItem;
import com.raven.data.PurchaseOrder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Form_IM_PurchaseOrderList extends javax.swing.JPanel {
    private List<PurchaseOrder> pos = new ArrayList<>();
    
    public Form_IM_PurchaseOrderList() {
        initComponents();
        loadPOs();
        initTable();
    }
    
    private void loadPOs() {
        try {
            pos = loadPurchaseOrdersFromFile();
        } catch (IOException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Error loading POs: " + e.getMessage(), 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
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

        // Set up action buttons (just View for now)
        TableActionEvent event = new TableActionEvent() {
            @Override
            public void onAction(int row, String actionCommand) {
                if ("View".equals(actionCommand)) {
                    showViewDialog(row);
                }
            }
        };

        String[] buttonNames = {"View"};
        String[] icons = {"/com/raven/icon/view.png"};

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
    
    private void showViewDialog(int row) {
    PurchaseOrder po = pos.get(row);
    
    JDialog dialog = new JDialog();
    dialog.setTitle("View Purchase Order - " + po.getPoId());
    dialog.setModal(true);
    dialog.setLayout(new BorderLayout());
    
    // Create view panel (read-only)
    // We'll use an empty list for approved PRs since we're just viewing
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
        jLabel1.setText("Purchase Order List");

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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 838, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(refreshButton)
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refreshButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        loadPOs();
        refreshTable();
    }//GEN-LAST:event_refreshButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton refreshButton;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
