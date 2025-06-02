/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.cell;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author ljsljs
 */
public class TableActionCellRender extends DefaultTableCellRenderer {
    private String[] buttonNames;
    private String[] icons;
    
    public TableActionCellRender(String[] buttonNames, String[] icons) {
        this.buttonNames = buttonNames;
        this.icons = icons;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean isSelected, boolean hasFocus, int row, int column) {
        PanelAction action = new PanelAction();
        action.initButtons(buttonNames, icons, null, row);

        if (isSelected) {
            action.setBackground(jtable.getSelectionBackground());
        } else {
            action.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
        }

        return action;
    }
}
