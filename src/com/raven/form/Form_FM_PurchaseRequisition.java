package com.raven.form;

import com.raven.cell.TableActionCellEditor;
import com.raven.cell.TableActionCellRender;
import com.raven.cell.TableActionEvent;
import com.raven.data.PurchaseRequisition;
import com.raven.data.PurchaseRequisitionRepository;
import java.awt.BorderLayout;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

public class Form_FM_PurchaseRequisition extends javax.swing.JPanel {

    private List<PurchaseRequisition> prs = new ArrayList<>();
    private boolean hasUnsavedChanges = false;

    public Form_FM_PurchaseRequisition() {
        initComponents();
        loadPRs();
        initTable();
    }

    private void loadPRs() {
        try {
            prs = PurchaseRequisitionRepository.loadPRs();
            hasUnsavedChanges = false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading PRs: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initTable() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"PR ID", "Date Required", "Items Count", "Raised By", "Status", "Action"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only action column is editable
            }
        };
        table.setModel(model);
        refreshTable();

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

        for (PurchaseRequisition pr : prs) {
            model.addRow(new Object[]{
                pr.getPrId(),
                dateFormat.format(pr.getDateRequired()),
                pr.getItems().size(),
                pr.getRaisedBy(),
                pr.getStatus(),
                ""
            });
        }
    }
    
    private void showViewDialog(int row) {
        PurchaseRequisition pr = prs.get(row);

        JDialog dialog = new JDialog();
        dialog.setTitle("View Purchase Requisition - " + pr.getPrId());
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());

        PM_PREditorPanel viewPanel = new PM_PREditorPanel(pr);
        viewPanel.setEditable(false); // Ensure it’s read-only
        dialog.add(viewPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
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

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("Purchase Requisition");

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
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(refreshButton)
                        .addGap(16, 16, 16)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 449, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(refreshButton)
                .addGap(23, 23, 23))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        if (hasUnsavedChanges) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "You have unsaved changes. Are you sure you want to refresh?",
                    "Confirm Refresh", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                loadPRs();
                refreshTable();
            }
        } else {
            loadPRs();
            refreshTable();
        }
    }//GEN-LAST:event_refreshButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton refreshButton;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}