/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.form;

import com.raven.data.ItemManager;
import com.raven.data.POItem;
import com.raven.data.PurchaseOrder;
import com.raven.data.PurchaseRequisition;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

public class PM_POEditorPanel extends JPanel {
    private final PurchaseOrder po;
    private final List<PurchaseRequisition> approvedPRs;
    private DefaultTableModel itemsModel;
    private JSpinner dateSpinner;
    private JComboBox<PurchaseRequisition> prComboBox;
    private boolean editable = true;
    
    public PM_POEditorPanel(PurchaseOrder po, List<PurchaseRequisition> approvedPRs) {
        this.po = po;
        this.approvedPRs = approvedPRs;
        initComponents();
        loadItems();
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
        dateSpinner.setEnabled(editable);
        prComboBox.setEnabled(editable);
        itemsModel.fireTableDataChanged(); // This will trigger cell editors to update
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // PO info panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JLabel idLabel = new JLabel("PO ID:");
        JTextField idField = new JTextField(po.getPoId());
        idField.setEditable(false);
        
        JLabel dateLabel = new JLabel("Date Required:");
        dateSpinner = new JSpinner(new SpinnerDateModel(po.getDateRequired(), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        
        // In PM_POEditorPanel.java, modify the initComponents() method:
        JLabel prLabel = new JLabel("Based on PR:");
        prComboBox = new JComboBox<>();
        for (PurchaseRequisition pr : approvedPRs) {
            prComboBox.addItem(pr);
            // Set the display to show PR ID
            prComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof PurchaseRequisition) {
                        PurchaseRequisition pr = (PurchaseRequisition) value;
                        setText(pr.getPrId()); // Show only the PR ID
                    }
                    return this;
                }
            });
        }
        
        prComboBox.addActionListener(e -> {
            if (prComboBox.getSelectedItem() != null) {
                loadItemsFromPR((PurchaseRequisition) prComboBox.getSelectedItem());
            }
        });
        
        JLabel statusLabel = new JLabel("Status:");
        JTextField statusField = new JTextField(po.getStatus());
        statusField.setEditable(false);
        
        infoPanel.add(idLabel);
        infoPanel.add(idField);
        infoPanel.add(dateLabel);
        infoPanel.add(dateSpinner);
        infoPanel.add(prLabel);
        infoPanel.add(prComboBox);
        infoPanel.add(statusLabel);
        infoPanel.add(statusField);
        
        // Items table
        itemsModel = new DefaultTableModel(
            new Object[]{"Item Code", "Item Name", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only quantity is editable if panel is editable
                return editable && column == 2;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Integer.class;
                return String.class;
            }
        };
        
        JTable itemsTable = new JTable(itemsModel);
        itemsTable.setRowHeight(30);
        

        
        add(infoPanel, BorderLayout.NORTH);
        add(new JScrollPane(itemsTable), BorderLayout.CENTER);
    }
    
    private void loadItems() {
    itemsModel.setRowCount(0);
    for (POItem item : po.getItems()) {
        String itemName = ItemManager.getItemName(item.getItemCode());
        itemsModel.addRow(new Object[]{
            item.getItemCode(),
            itemName,
            item.getQuantity()
        });
    }
}

    private void loadItemsFromPR(PurchaseRequisition pr) {
    po.getItems().clear();
    itemsModel.setRowCount(0);
    
    for (Map.Entry<String, Integer> entry : pr.getItems().entrySet()) {
        po.getItems().add(new POItem(entry.getKey(), entry.getValue()));
        String itemName = ItemManager.getItemName(entry.getKey());
        itemsModel.addRow(new Object[]{
            entry.getKey(),
            itemName,
            entry.getValue()
        });
    }
}
    
    public void updatePO() {        
        // Update quantities from the table
        for (int i = 0; i < itemsModel.getRowCount(); i++) {
            String itemCode = (String) itemsModel.getValueAt(i, 0);
            int quantity = (Integer) itemsModel.getValueAt(i, 2);
            
            // Find and update the corresponding POItem
            for (POItem item : po.getItems()) {
                if (item.getItemCode().equals(itemCode)) {
                    item.setQuantity(quantity);
                    break;
                }
            }
        }
    }
} 