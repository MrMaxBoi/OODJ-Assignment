package com.raven.component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;

public class Header extends javax.swing.JPanel {
    
    private Timer timer;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

    public Header() {
        initComponents();
        setOpaque(false);
        setUserInfo("", "");
        startClock();
    }
    
    private void startClock() {
        timer = new Timer(1000, e -> {
            updateDateTime();
        });
        timer.start();
        updateDateTime(); // Initial update
    }
    
    private void updateDateTime() {
        Date now = new Date();
        labelDate.setText(dateFormat.format(now));
        labelTime.setText(timeFormat.format(now));
    }
    
    public void setUserInfo(String name, String role) {
        // Set name in the first label with bold font
        jLabel1.setText(name != null ? name : "Unknown");
        
        // Set role in the second label with smaller gray font
        jLabel3.setText(role != null ? role : "Unknown Role");
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelTime = new javax.swing.JLabel();
        labelDate = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(688, 55));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 102, 102));
        jLabel3.setText(null);

        labelTime.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        labelTime.setForeground(new java.awt.Color(102, 102, 102));
        labelTime.setText("jLabel2");

        labelDate.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        labelDate.setForeground(new java.awt.Color(102, 102, 102));
        labelDate.setText("jLabel4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDate)
                    .addComponent(labelTime, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 421, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelTime)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(labelDate))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, 0)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.fillRect(0, 0, 25, getHeight());
        g2.fillRect(getWidth() - 25, getHeight() - 25, getWidth(), getHeight());
        super.paintComponent(grphcs);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel labelDate;
    private javax.swing.JLabel labelTime;
    // End of variables declaration//GEN-END:variables
}
