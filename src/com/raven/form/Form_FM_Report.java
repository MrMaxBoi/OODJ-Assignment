package com.raven.form;

import com.raven.cell.TableActionCellEditor;
import com.raven.cell.TableActionCellRender;
import com.raven.cell.TableActionEvent;
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class Form_FM_Report extends javax.swing.JPanel {
   
    private final List<String[]> financialReports = new ArrayList<>();
    
    public static class DailySaleEntry {
        public Date   date;
        public String itemCode;
        public int    quantity;
        public double amount;
        public DailySaleEntry(Date d, String code, int q, double a) {
            date     = d;
            itemCode = code;
            quantity = q;
            amount   = a;
        }
    }

    public static class POEntry {
        public String poId, prId;
        public Date   date;
        public String itemCode;
        public int    quantity;
        public double amount;
        public POEntry(String p, String pr, Date d, String code, int q, double a) {
            poId     = p;
            prId     = pr;
            date     = d;
            itemCode = code;
            quantity = q;
            amount   = a;
        }
    }

    public void reload() {
        loadFinancialReports();
        refreshTable();
    }
    
    public Form_FM_Report() {
        initComponents();
        Genearate_Report_Button.setText("Generate Report");
        for (ActionListener al : Genearate_Report_Button.getActionListeners()) {
            Genearate_Report_Button.removeActionListener(al);
        }
        Genearate_Report_Button.addActionListener(evt -> generateReport());
        initTable();
        loadFinancialReports();
        refreshTable();
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadFinancialReports();
                refreshTable();
            }
        });
    }
    
    private void loadFinancialReports() {
        financialReports.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("financial_report_list.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    financialReports.add(new String[]{
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim()
                    });
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading financial report file:\n" + ex.getMessage(),
                "Load Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void initTable() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{},
            new String[]{ "FRID", "Date Generated", "Generated by", "Action" }
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 3; // only “Action” is editable (i.e. clickable)
            }
        };
        table.setModel(model);
        table.setRowHeight(35);
        refreshTable();

        // when you click the View button:
        TableActionEvent event = new TableActionEvent() {
            @Override
            public void onAction(int row, String actionCommand) {
                if ("View".equals(actionCommand)) {
                    showReportDialog(row);
                }
            }
        };

        String[] btns  = { "View", "Delete" };
        String[] icons = { "/com/raven/icon/view.png", "/com/raven/icon/delete.png" };

        table.getColumnModel().getColumn(3)
            .setCellRenderer(new TableActionCellRender(btns, icons));
        table.getColumnModel().getColumn(3)
            .setCellEditor(new TableActionCellEditor(new TableActionEvent() {
            @Override
            public void onAction(int row, String cmd) {
                String frid = (String) table.getValueAt(row, 0);
                switch (cmd) {
                    case "View":
                        showReportDialog(row);
                        break;
                    case "Delete":
                        if (JOptionPane.showConfirmDialog(
                                Form_FM_Report.this,
                                "Delete report " + frid + "?",
                                "Confirm Delete",
                                JOptionPane.YES_NO_OPTION
                            ) == JOptionPane.YES_OPTION) {
                        if (table.isEditing()) {
                            table.getCellEditor().cancelCellEditing();
                            }
                            deleteReport(frid);
                        }
                        break;
                }
            }
        }, btns, icons));
    }    
        
    private void deleteReport(String frid) {
        try {
            new File("financial_report_" + frid + ".txt").delete();

            File  listFile = new File("financial_report_list.txt");
            List<String> lines = Files.readAllLines(listFile.toPath());

            try (PrintWriter pw = new PrintWriter(new FileWriter(listFile))) {
                for (String l : lines) {
                    if (!l.startsWith(frid + "|")) {
                        pw.println(l);
                    }
                }
            }

            // Rrefresh table
            loadFinancialReports();
            refreshTable();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error deleting report " + frid + ":\n" + ex.getMessage(),
                "I/O Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (String[] rec : financialReports) {
            model.addRow(new Object[]{ rec[0], rec[1], rec[2], "" });
        }
    }
    
    private void generateReport() {
        int next = 1;
        try (BufferedReader br = new BufferedReader(new FileReader("financial_report_list.txt"))) {
            String l;
            while ((l = br.readLine()) != null) {
                String[] p = l.split("\\|");
                if (p.length>0 && p[0].startsWith("FR")) {
                    try {
                        int val = Integer.parseInt(p[0].substring(2));
                        next = Math.max(next, val+1);
                    } catch (NumberFormatException ign) {}
                }
            }
        } catch (IOException ign) { /* first run: file might not exist */ }

        String frid = String.format("FR%03d", next);    
        // Validate and Parse Dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate, endDate;
        try {
            startDate = sdf.parse(DateStarted_TB.getText().trim());
            endDate   = sdf.parse(DateEnded_TB.getText().trim());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
            "Please enter valid dates in format yyyy-MM-dd.",
            "Invalid Date",
            JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (startDate.after(endDate)) {
            JOptionPane.showMessageDialog(this,
            "Start date must be before or equal to end date.",
            "Invalid Range",
            JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Load and Filter Sales ---
        List<DailySaleEntry> sales = new ArrayList<>();
        double totalRevenue = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("daily_sales.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length < 3) {
                    continue;
                }
                Date d;
                try {
                    d = sdf.parse(parts[0]);
                } catch (ParseException pe) {
                    continue;
                }
                // Filter Data by Date
                if (d.before(startDate) || d.after(endDate)) {
                    continue;
                }
            
                double recordAmount;
                try {
                    recordAmount = Double.parseDouble(parts[2]);
                } catch (NumberFormatException nfe) {
                    continue;
                }
                totalRevenue += recordAmount;
            
                String[] itemPairs = parts[1].split(";");
                for (String pair : itemPairs) {
                    if (pair.isBlank()) continue;
                    String[] kv = pair.split(",");
                    if (kv.length != 2) continue;
                    try {
                        int qty = Integer.parseInt(kv[1].trim());
                        sales.add(new DailySaleEntry(d, kv[0].trim(), qty, recordAmount));
                    } catch (NumberFormatException nfe) {
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
            "Error reading daily_sales.txt:\n" + ex.getMessage(),
            "I/O Error",
            JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Load and Filter POs
        List<POEntry> pos = new ArrayList<>();
        double totalExpenditure = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("processed_po.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                String poId = p[0], prId = p[2];
                Date d = new Date(Long.parseLong(p[1]));
                if (d.before(startDate) || d.after(endDate)) continue;

                double recordAmt = Double.parseDouble(p[5]);
                totalExpenditure += recordAmt;

                String[] itemPairs = p[4].split(",");
                for (String ip : itemPairs) {
                    String[] kv = ip.split(":");
                    pos.add(new POEntry(poId, prId, d, kv[0], Integer.parseInt(kv[1]), recordAmt));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
            "Error reading processed_po.txt:\n" + ex.getMessage(),
            "I/O Error",
            JOptionPane.ERROR_MESSAGE);
            return;
        }

        double profit = totalRevenue - totalExpenditure;
    
        String detailFile = "financial_report_" + frid + ".txt";
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

        try (PrintWriter pw = new PrintWriter(new FileWriter(detailFile))) {
            pw.println("FRID: " + frid);
            pw.println("Date Range: " 
                + sdf2.format(startDate) + " to " + sdf2.format(endDate));
            pw.println();
            pw.println("Revenue:");
            for (DailySaleEntry e : sales) {
                pw.printf("%s|%s|%d|%.2f%n",
                    sdf2.format(e.date),
                    e.itemCode,
                    e.quantity,
                    e.amount
                );
            }
            pw.printf("Total Revenue: %.2f%n%n", totalRevenue);
            pw.println("Expenditure:");
            for (POEntry p : pos) {
                pw.printf("%s|%s|%s|%d|%.2f%n",
                    p.poId,
                    sdf2.format(p.date),
                    p.prId,
                    p.quantity,
                    p.amount
                );
            }
            pw.printf("Total Expenditure: %.2f%n%n", totalExpenditure);
            pw.printf("Profit: %.2f%n", profit);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
            "Error writing " + detailFile + ":\n" + ex.getMessage(),
            "I/O Error",
            JOptionPane.ERROR_MESSAGE);
        }
    
        // --- 4) record this report in your list file ---
        String generatedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String user = System.getProperty("user.name");  // or your session manager
        try(PrintWriter pw = new PrintWriter(new FileWriter("financial_report_list.txt", true))) {
            pw.println(frid + "|" + generatedAt + "|" + user);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
            "Error writing financial_report_list.txt:\n" + ex.getMessage(),
            "I/O Error",
            JOptionPane.ERROR_MESSAGE);
        }

        // Refresh Report Table 
        loadFinancialReports();
        refreshTable();

        // --- 6) pop up the detail panel ---
        FM_FREditorPanel editor = new FM_FREditorPanel(
            frid, startDate, endDate, sales, pos, totalRevenue, totalExpenditure
        );
        JDialog dlg = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Financial Report – " + frid,
            Dialog.ModalityType.APPLICATION_MODAL
        );
        dlg.setContentPane(editor);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }
    
    private void showReportDialog(int row) {
        String[] rec = financialReports.get(row);
        String frid = rec[0];

        String detailFile = "financial_report_" + frid + ".txt";
        
        java.io.File f = new java.io.File(detailFile);
        if (!f.exists()) {
            JOptionPane.showMessageDialog(this,
                "FRID: " + frid + "\n"
            + "Date Generated: " + rec[1] + "\n"
            + "Generated by: "    + rec[2],
                "View Report: " + frid,
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<DailySaleEntry> sales = new ArrayList<>();
        List<POEntry>         pos   = new ArrayList<>();
        Date startDate = null, endDate = null;
        double totalRev = 0, totalExp = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            br.readLine();
            String range = br.readLine().substring("Date Range: ".length());
            String[] dates = range.split(" to ");
            startDate = sdf.parse(dates[0].trim());
            endDate   = sdf.parse(dates[1].trim());

            br.readLine();

            br.readLine(); // "Revenue:"
            while (!(line = br.readLine()).startsWith("Total Revenue:")) {
                String[] p = line.split("\\|");
                sales.add(new DailySaleEntry(
                    sdf.parse(p[0]), p[1], Integer.parseInt(p[2]), Double.parseDouble(p[3])
                ));
            }
            totalRev = Double.parseDouble(line.substring("Total Revenue: ".length()));

            br.readLine();

            // "Expenditure:"
            br.readLine();
            while (!(line = br.readLine()).startsWith("Total Expenditure:")) {
                String[] p = line.split("\\|");
                pos.add(new POEntry(
                p[0],
                null,         
                sdf.parse(p[1]),
                p[2],
                Integer.parseInt(p[3]),
                Double.parseDouble(p[4])
                ));
            }
            totalExp = Double.parseDouble(line.substring("Total Expenditure: ".length()));

            // Read Profit
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
            "Error reading " + detailFile + ":\n" + ex.getMessage(),
            "I/O Error",
            JOptionPane.ERROR_MESSAGE);
            return;
        }

        FM_FREditorPanel editor = new FM_FREditorPanel(
            frid, startDate, endDate, sales, pos, totalRev, totalExp
        );
        JDialog dlg = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Financial Report – " + frid,
            Dialog.ModalityType.APPLICATION_MODAL
        );
        dlg.setContentPane(editor);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        DateStarted_TB = new javax.swing.JTextField();
        DateEnded_TB = new javax.swing.JTextField();
        Genearate_Report_Button = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "FRID", "Date Generated", "Generated by", "Action"
            }
        ));
        table.setRowHeight(35);
        jScrollPane1.setViewportView(table);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("Financial Report");

        jLabel2.setText("Date Started:");

        jLabel3.setText("Date Ended:");

        DateStarted_TB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DateStarted_TBActionPerformed(evt);
            }
        });

        Genearate_Report_Button.setText("Generate Report");
        Genearate_Report_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Genearate_Report_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 838, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(DateEnded_TB, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DateStarted_TB, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(32, 32, 32)
                        .addComponent(Genearate_Report_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(DateStarted_TB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(DateEnded_TB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(Genearate_Report_Button)))
                .addContainerGap(56, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void DateStarted_TBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DateStarted_TBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DateStarted_TBActionPerformed

    private void Genearate_Report_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Genearate_Report_ButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Genearate_Report_ButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField DateEnded_TB;
    private javax.swing.JTextField DateStarted_TB;
    private javax.swing.JButton Genearate_Report_Button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
