package com.raven.component;

import com.raven.model.Model_Card;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class FM_Card_Profit extends javax.swing.JPanel {

    private Color color1 = new Color(58,96,115);
    private Color color2 = new Color(22,34,42);

    public void setColor1(Color color1) {
        this.color1 = color1;
        repaint();
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
        repaint();
    }
    
    public void setTitle(String title){
        lbTitle.setText(title);
    }

    public void setValue(String value){
        lbValues.setText(value);
    }
    
    public FM_Card_Profit() {
        initComponents();
        setOpaque(false);
        setPreferredSize(new Dimension(250,120));
    }

    public void setData(Model_Card data) {
        lbTitle.setText(data.getTitle());
        lbValues.setText(data.getValues());
        lbRevenue.setText(data.getDescription());
    }

    public void setRevenue(String revenueText) {
    lbRevenue.setText(revenueText);
}

    public void setExpenditure(String expenditureText) {
    lbExpediture.setText(expenditureText);
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbTitle = new javax.swing.JLabel();
        lbValues = new javax.swing.JLabel();
        lbRevenue = new javax.swing.JLabel();
        Daily_Button = new javax.swing.JButton();
        Monthly_Button = new javax.swing.JButton();
        Yearly_Button = new javax.swing.JButton();
        lbExpediture = new javax.swing.JLabel();

        lbTitle.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(255, 255, 255));
        lbTitle.setText("Title");

        lbValues.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        lbValues.setForeground(new java.awt.Color(255, 255, 255));
        lbValues.setText("Values");

        lbRevenue.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        lbRevenue.setForeground(new java.awt.Color(255, 255, 255));
        lbRevenue.setText("Revenue");

        Daily_Button.setText("Daily");

        Monthly_Button.setText("Monthly");
        Monthly_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Monthly_ButtonActionPerformed(evt);
            }
        });

        Yearly_Button.setText("Yearly");
        Yearly_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Yearly_ButtonActionPerformed(evt);
            }
        });

        lbExpediture.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        lbExpediture.setForeground(new java.awt.Color(255, 255, 255));
        lbExpediture.setText("Expenditure");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbValues)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(lbRevenue)
                        .addGap(18, 18, 18)
                        .addComponent(lbExpediture))
                    .addComponent(lbTitle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Daily_Button, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(Monthly_Button, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                        .addComponent(Yearly_Button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbTitle)
                    .addComponent(Daily_Button))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbValues)
                    .addComponent(Monthly_Button))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbExpediture)
                    .addComponent(lbRevenue)
                    .addComponent(Yearly_Button))
                .addContainerGap(49, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void Monthly_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Monthly_ButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Monthly_ButtonActionPerformed

    private void Yearly_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Yearly_ButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Yearly_ButtonActionPerformed

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.setColor(new Color(255, 255, 255, 50));
        g2.fillOval(getWidth() - (getHeight() / 2), 10, getHeight(), getHeight());
        g2.fillOval(getWidth() - (getHeight() / 2) - 20, getHeight() / 2 + 20, getHeight(), getHeight());
        super.paintComponent(g);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Daily_Button;
    private javax.swing.JButton Monthly_Button;
    private javax.swing.JButton Yearly_Button;
    private javax.swing.JLabel lbExpediture;
    private javax.swing.JLabel lbRevenue;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JLabel lbValues;
    // End of variables declaration//GEN-END:variables
}
