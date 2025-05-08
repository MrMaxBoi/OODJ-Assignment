/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raven.main;

import com.raven.component.Header;
import com.raven.form.Form_SM_ItemEntry;
import com.raven.event.EventMenuSelected;
import com.raven.form.Form_SM_SupplierManagement;
import com.raven.form.Form_SM_DailySalesEntry;
import com.raven.form.Form_SM_Dashboard;
import com.raven.form.Form_SM_PurchaseOrderList;
import com.raven.form.Form_SM_PurchaseRequisition;
import java.awt.Color;
import java.awt.Component;
import static java.awt.SystemColor.menu;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 *
 * @author RAVEN
 */
public class FM_Page extends javax.swing.JFrame {

    /**
     * Creates new form Main
     */
    
    private Form_SM_Dashboard home;
    private Form_SM_ItemEntry form1;
    private Form_SM_SupplierManagement form2;
    private Form_SM_DailySalesEntry form3;
    private Form_SM_PurchaseRequisition form4;
    private Form_SM_PurchaseOrderList form5;
    private Form_SM_PurchaseOrderList form6;
    private String currentUserId;
    
    public FM_Page(String userId) {
        this.currentUserId = userId;
        initComponents();
        setBackground(new Color(0, 0, 0, 0));
        home = new Form_SM_Dashboard();
        form1 = new Form_SM_ItemEntry();
        form2 = new Form_SM_SupplierManagement();
        form3 = new Form_SM_DailySalesEntry();
        form4 = new Form_SM_PurchaseRequisition(currentUserId);
        form5 = new Form_SM_PurchaseOrderList();
        menu.initMoving(FM_Page.this);
        menu.addEventMenuSelected(new EventMenuSelected() {
            @Override
            public void selected(int index) {
                if (index == 0) {
                    setForm(home);
                } else if (index == 1) {
                    setForm(form1);
                } else if (index == 2) {
                    setForm(form2);
                } else if (index == 3) {
                    setForm(form3);
                } else if (index == 4) {
                    setForm(form4);
                } else if (index == 5) {
                    setForm(form5);
                } else if (index == 15) {
                    logout();
                }
            }
        });
        
        setForm(new Form_SM_Dashboard());
    }
    
    public FM_Page() {
        this("default_user"); // Provide a default value or handle differently
    }
    
private void logout() {
    // Check for unsaved changes in both forms
    boolean hasUnsavedItemChanges = form1 != null && form1.hasUnsavedChanges;
    boolean hasUnsavedSupplierChanges = form2 != null && form2.hasUnsavedChanges;
    
    if (hasUnsavedItemChanges || hasUnsavedSupplierChanges) {
        StringBuilder message = new StringBuilder("You have unsaved changes in:");
        if (hasUnsavedItemChanges) message.append("\n- Item Management");
        if (hasUnsavedSupplierChanges) message.append("\n- Supplier Management");
        message.append("\n\nDo you want to save before logging out?");
        
        int saveOption = JOptionPane.showConfirmDialog(
            this,
            message.toString(),
            "Unsaved Changes",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (saveOption == JOptionPane.YES_OPTION) {
            // Save both forms silently during logout
            boolean itemSaved = form1 != null && form1.hasUnsavedChanges ? form1.saveItemsToFile(true) : true;
            boolean supplierSaved = form2 != null && form2.hasUnsavedChanges ? form2.saveSuppliersToFile(true) : true;
            
            if (!itemSaved || !supplierSaved) {
                return; // Don't proceed with logout if any save failed
            }
        } else if (saveOption == JOptionPane.CANCEL_OPTION) {
            return; // Cancel logout
        }
        // If NO, continue with logout without saving
    }

    // Only show logout confirmation if we didn't just handle unsaved changes
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to logout?",
        "Confirm Logout",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
        this.dispose();
        java.awt.EventQueue.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}

private void setForm(JComponent com) {
    // Check for unsaved changes in the current form before switching
    if (mainPanel.getComponentCount() > 0) {
        Component current = mainPanel.getComponent(0);
        
        if (current instanceof Form_SM_ItemEntry) {
            Form_SM_ItemEntry currentForm = (Form_SM_ItemEntry)current;
            if (!currentForm.checkUnsavedChanges()) {
                return; // Abort the form switch if user cancels
            }
        } else if (current instanceof Form_SM_SupplierManagement) {
            Form_SM_SupplierManagement currentForm = (Form_SM_SupplierManagement)current;
            if (!currentForm.checkUnsavedChanges()) {
                return; // Abort the form switch if user cancels
            }
        }
    }
    
    mainPanel.removeAll();
    mainPanel.add(com);
    mainPanel.repaint();
    mainPanel.revalidate();
   
}
    
    

    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelBorder1 = new com.raven.swing.PanelBorder();
        header2 = new com.raven.component.Header();
        mainPanel = new javax.swing.JPanel();
        menu = new com.raven.component.FM_Menu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        header2.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N

        mainPanel.setOpaque(false);
        mainPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(header2, javax.swing.GroupLayout.DEFAULT_SIZE, 933, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addComponent(header2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FM_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FM_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FM_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FM_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // This is just for testing - in real usage, it will be called from Login
                new FM_Page("test_user").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.component.Header header2;
    private javax.swing.JPanel mainPanel;
    private com.raven.component.FM_Menu menu;
    private com.raven.swing.PanelBorder panelBorder1;
    // End of variables declaration//GEN-END:variables

    public Header getHeader() {
        return header2; // This is the variable name in your Dashboard class
    }
}
