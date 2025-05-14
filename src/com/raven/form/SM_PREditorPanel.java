/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.form;

import com.raven.cell.TableActionCellEditor;
import com.raven.cell.TableActionCellRender;
import com.raven.cell.TableActionEvent;
import com.raven.data.PurchaseRequisition;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Map;
import javax.swing.SpinnerDateModel;

public class SM_PREditorPanel extends JPanel {
    private final PurchaseRequisition pr;
    private DefaultTableModel itemsModel;
    private JSpinner dateSpinner; // Using JSpinner as alternative to JXDatePicker
    // OR if using JXDatePicker:
    // private JXDatePicker datePicker;
    
    public SM_PREditorPanel(PurchaseRequisition pr) {
        this.pr = pr;
        initComponents();
        loadItems();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // PR info panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JLabel idLabel = new JLabel("PR ID:");
        JTextField idField = new JTextField(pr.getPrId());
        idField.setEditable(false);
        
        JLabel dateLabel = new JLabel("Date Required:");
        
        // Option 1: Using JSpinner (no external library needed)
        dateSpinner = new JSpinner(new SpinnerDateModel(pr.getDateRequired(), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        
        // Option 2: Using JXDatePicker (requires SwingX library)
        // datePicker = new JXDatePicker(pr.getDateRequired());
        
        JLabel statusLabel = new JLabel("Status:");
        JTextField statusField = new JTextField(pr.getStatus());
        statusField.setEditable(false);
        
        infoPanel.add(idLabel);
        infoPanel.add(idField);
        infoPanel.add(dateLabel);
        infoPanel.add(dateSpinner); // or datePicker if using JXDatePicker
        infoPanel.add(statusLabel);
        infoPanel.add(statusField);
        
        // Items table
        itemsModel = new DefaultTableModel(
            new Object[]{"Item Code", "Item Name", "Quantity", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 3;
            }
        };
        
        JTable itemsTable = new JTable(itemsModel);
        itemsTable.setRowHeight(30);
        
        // Set up action buttons for items
        TableActionEvent event = new TableActionEvent() {
            @Override
            public void onAction(int row, String actionCommand) {
                if ("Remove".equals(actionCommand)) {
                    String itemCode = (String) itemsModel.getValueAt(row, 0);
                    pr.removeItem(itemCode);
                    itemsModel.removeRow(row);
                }
            }
        };
        
        itemsTable.getColumnModel().getColumn(3).setCellRenderer(
            new TableActionCellRender(new String[]{"Remove"}, new String[]{"/com/raven/icon/delete.png"}));
        itemsTable.getColumnModel().getColumn(3).setCellEditor(
            new TableActionCellEditor(event, new String[]{"Remove"}, new String[]{"/com/raven/icon/delete.png"}));
        
        // Add item button
        JPanel buttonPanel = new JPanel();
        JButton addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddItemDialog();
            }
        });
        buttonPanel.add(addItemButton);
        
        add(infoPanel, BorderLayout.NORTH);
        add(new JScrollPane(itemsTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadItems() {
        itemsModel.setRowCount(0);
        for (Map.Entry<String, Integer> entry : pr.getItems().entrySet()) {
            // In a real implementation, you would look up the item name from your items database
            String itemName = "Item " + entry.getKey(); // Placeholder - replace with actual lookup
            itemsModel.addRow(new Object[]{
                entry.getKey(),
                itemName,
                entry.getValue(),
                "Remove"
            });
        }
    }
    
    public void updatePR() {
    // Update the date
    pr.setDateRequired(getSelectedDate());
    
    // The items are already updated directly in the PR object
    // through the table's remove/add item actions
    }   
    
    public Date getSelectedDate() {
        // If using JSpinner:
        return (Date) dateSpinner.getValue();
        
        // If using JXDatePicker:
        // return datePicker.getDate();
    }
    
    private void showAddItemDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Add Item to PR");
        dialog.setModal(true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        
        JLabel itemLabel = new JLabel("Item Code:");
        JTextField itemField = new JTextField();
        
        JLabel qtyLabel = new JLabel("Quantity:");
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");
        
        dialog.add(itemLabel);
        dialog.add(itemField);
        dialog.add(qtyLabel);
        dialog.add(qtySpinner);
        dialog.add(addButton);
        dialog.add(cancelButton);
        
        addButton.addActionListener(e -> {
            String itemCode = itemField.getText().trim();
            if (itemCode.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Item code cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int quantity = (Integer) qtySpinner.getValue();
            
            // Check if item already exists
            for (int i = 0; i < itemsModel.getRowCount(); i++) {
                if (itemsModel.getValueAt(i, 0).equals(itemCode)) {
                    // Update quantity if item exists
                    int currentQty = (Integer) itemsModel.getValueAt(i, 2);
                    itemsModel.setValueAt(currentQty + quantity, i, 2);
                    pr.addItem(itemCode, currentQty + quantity);
                    dialog.dispose();
                    return;
                }
            }
            
            // Add new item
            pr.addItem(itemCode, quantity);
            itemsModel.addRow(new Object[]{
                itemCode,
                "Item " + itemCode, // Replace with actual item name lookup
                quantity,
                "Remove"
            });
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
