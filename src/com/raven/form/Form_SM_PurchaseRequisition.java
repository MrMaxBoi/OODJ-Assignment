package com.raven.form;

import com.raven.cell.TableActionCellEditor;
import com.raven.cell.TableActionCellRender;
import com.raven.cell.TableActionEvent;
import com.raven.data.PurchaseRequisition;
import com.raven.data.PurchaseRequisitionRepository;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Form_SM_PurchaseRequisition extends javax.swing.JPanel {
    private List<PurchaseRequisition> prs = new ArrayList<>();
    private boolean hasUnsavedChanges = false;
    private String currentUserId;
    
    public Form_SM_PurchaseRequisition(String userId) {
        this.currentUserId = userId;
        initComponents();
        loadPRs();
        initTable();
    }
    
    private void loadPRs() {
        try {
            prs = PurchaseRequisitionRepository.loadPRs();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading PRs: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initTable() {
        // Set up table model to show PR summary
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{}, 
            new String[]{"PR ID", "Date Required", "Items Count", "Raised By","Status", "Action"}
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
                    case "Edit":
                        showEditDialog(row);
                        break;
                    case "Delete":
                        deletePR(row);
                        break;
                }
            }
        };

        String[] buttonNames = {"Edit", "Delete"};
        String[] icons = {
            "/com/raven/icon/edit.png",
            "/com/raven/icon/delete.png"
        };

        table.getColumnModel().getColumn(5).setCellRenderer(
            new TableActionCellRender(buttonNames, icons));
        table.getColumnModel().getColumn(5).setCellEditor(
            new TableActionCellEditor(event, buttonNames, icons));
        
        // Connect buttons
        addButton.addActionListener(e -> showAddDialog());
        saveButton.addActionListener(e -> savePRs());
        
        // Set up search
        searchTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { searchPRs(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { searchPRs(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { searchPRs(); }
        });
    }
    
    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        for (PurchaseRequisition pr : prs) {
            model.addRow(new Object[]{
                pr.getPrId(),
                dateFormat.format(pr.getDateRequired()),
                pr.getItems().size(),
                pr.getRaisedBy(),
                pr.getStatus(),
                "" // Action column
            });
        }
    }
    
    private void searchPRs() {
        String searchText = searchTextField.getText().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        for (PurchaseRequisition pr : prs) {
            if (searchText.isEmpty() ||
                pr.getPrId().toLowerCase().contains(searchText) ||
                dateFormat.format(pr.getDateRequired()).toLowerCase().contains(searchText) ||
                pr.getStatus().toLowerCase().contains(searchText)) {
                
                model.addRow(new Object[]{
                    pr.getPrId(),
                    dateFormat.format(pr.getDateRequired()),
                    pr.getItems().size(),
                    pr.getStatus(),
                    ""
                });
            }
        }
    }
    
    private void showAddDialog() {
        try {
            JDialog dialog = new JDialog();
            dialog.setTitle("New Purchase Requisition");
            dialog.setModal(true);
            dialog.setLayout(new BorderLayout());
            
            // Create empty PR
            Map<String, Integer> items = new HashMap<>();
            PurchaseRequisition newPR = new PurchaseRequisition(
                PurchaseRequisitionRepository.generateNextPRId(),
                new Date(),
                items,
                currentUserId
            );
            
            // Create editor panel
            PREditorPanel editorPanel = new PREditorPanel(newPR);
            dialog.add(editorPanel, BorderLayout.CENTER);
            
            // Save button
            JPanel buttonPanel = new JPanel();
            JButton saveButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");
            
            saveButton.addActionListener(e -> {
                editorPanel.updatePR();
                prs.add(newPR);
                hasUnsavedChanges = true;
                refreshTable();
                dialog.dispose();
            });
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error creating PR: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showEditDialog(int row) {
        PurchaseRequisition pr = prs.get(row);
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Purchase Requisition - " + pr.getPrId());
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        
        // Create editor panel
        PREditorPanel editorPanel = new PREditorPanel(pr);
        dialog.add(editorPanel, BorderLayout.CENTER);
        
        // Save button
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            editorPanel.updatePR();
            hasUnsavedChanges = true;
            refreshTable();
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deletePR(int row) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this PR?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            prs.remove(row);
            hasUnsavedChanges = true;
            refreshTable();
        }
    }
    
    private void savePRs() {
        try {
            PurchaseRequisitionRepository.savePRs(prs);
            hasUnsavedChanges = false;
            JOptionPane.showMessageDialog(this, "PRs saved successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving PRs: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
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
        jLabel3 = new javax.swing.JLabel();
        searchTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "PRID", "Date Required", "Item Code", "Action"
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
        jLabel1.setText("Purchase Requisition");

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/Save.png"))); // NOI18N

        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/AddItem.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 257, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(saveButton)))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addButton)
                    .addComponent(saveButton))
                .addContainerGap(40, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
