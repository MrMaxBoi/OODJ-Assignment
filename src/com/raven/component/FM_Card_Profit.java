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

    public FM_Card_Profit() {
        initComponents();
        setOpaque(false);
        setPreferredSize(new Dimension(250,120));
    }
    
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

    public void setRevenueText(String text) {
        Revenure_TextField.setText(text);
    }

    public String getRevenueText() {
        return Revenure_TextField.getText();
    }

    public void setExpenditureText(String text) {
        Expenditure_TextField.setText(text);
    }

    public String getExpenditureText() {
        return Expenditure_TextField.getText();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbTitle = new javax.swing.JLabel();
        lbValues = new javax.swing.JLabel();
        lbRevenue = new javax.swing.JLabel();
        lbExpediture = new javax.swing.JLabel();
        Revenure_TextField = new javax.swing.JTextField();
        Expenditure_TextField = new javax.swing.JTextField();

        lbTitle.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(255, 255, 255));
        lbTitle.setText("Title");

        lbValues.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        lbValues.setForeground(new java.awt.Color(255, 255, 255));
        lbValues.setText("Values");

        lbRevenue.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        lbRevenue.setForeground(new java.awt.Color(255, 255, 255));
        lbRevenue.setText("Revenue");

        lbExpediture.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        lbExpediture.setForeground(new java.awt.Color(255, 255, 255));
        lbExpediture.setText("Expenditure");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbTitle)
                    .addComponent(lbValues)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbRevenue)
                            .addComponent(lbExpediture))
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Revenure_TextField)
                            .addComponent(Expenditure_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(288, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(lbTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbValues)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbRevenue)
                    .addComponent(Revenure_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbExpediture)
                    .addComponent(Expenditure_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JTextField Expenditure_TextField;
    private javax.swing.JTextField Revenure_TextField;
    private javax.swing.JLabel lbExpediture;
    private javax.swing.JLabel lbRevenue;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JLabel lbValues;
    // End of variables declaration//GEN-END:variables
}
