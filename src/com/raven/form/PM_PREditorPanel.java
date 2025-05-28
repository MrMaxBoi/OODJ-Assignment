/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.form;

import com.raven.cell.TableActionCellEditor;
import com.raven.cell.TableActionCellRender;
import com.raven.cell.TableActionEvent;
import com.raven.data.ItemManager;
import com.raven.data.PurchaseRequisition;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Map;
import javax.swing.SpinnerDateModel;

public class PM_PREditorPanel extends JPanel {
    private final PurchaseRequisition pr;
    private DefaultTableModel itemsModel;
    private JSpinner dateSpinner; // Using JSpinner as alternative to JXDatePicker
    private JTable itemsTable;
    // OR if using JXDatePicker:
    // private JXDatePicker datePicker;
    
    public PM_PREditorPanel(PurchaseRequisition pr) {
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
            new Object[]{"Item Code", "Item Name", "Quantity"}, 0) {
        };
        
        itemsTable = new JTable(itemsModel);
        itemsTable.setRowHeight(30);
        
        add(infoPanel, BorderLayout.NORTH);
        add(new JScrollPane(itemsTable), BorderLayout.CENTER);
    }
    
    private void loadItems() {
    itemsModel.setRowCount(0);
    for (Map.Entry<String, Integer> entry : pr.getItems().entrySet()) {
        String itemName = ItemManager.getItemName(entry.getKey());
        itemsModel.addRow(new Object[]{
            entry.getKey(),
            itemName,
            entry.getValue()
        });
    }
}
    public void setEditable(boolean editable) {
    dateSpinner.setEnabled(editable); // or datePicker.setEnabled(editable) if used
    
    // Make the table cells editable or not
    itemsTable.setEnabled(editable);
    
    // Optional: visually gray it out when not editable
    itemsTable.setBackground(editable ? Color.WHITE : UIManager.getColor("Panel.background"));
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
}
