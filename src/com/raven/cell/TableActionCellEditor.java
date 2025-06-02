/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.cell;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

/**
 *
 * @author RAVEN
 */
public class TableActionCellEditor extends DefaultCellEditor {
    private TableActionEvent event;
    private String[] buttonNames;
    private String[] icons;
    
    public TableActionCellEditor(TableActionEvent event, String[] buttonNames, String[] icons) {
        super(new JCheckBox());
        this.event = event;
        this.buttonNames = buttonNames;
        this.icons = icons;
    }

    @Override
    public Component getTableCellEditorComponent(JTable jtable, Object o, boolean bln, int row, int column) {
        PanelAction action = new PanelAction();
        action.initButtons(buttonNames, icons, event, row);
        action.setBackground(jtable.getSelectionBackground());
        return action;
    }
}