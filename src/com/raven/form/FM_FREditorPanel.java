/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.form;

import com.raven.data.ItemManager;
import com.raven.data.POItem;
import com.raven.data.PurchaseOrder;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FM_FREditorPanel extends JPanel {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public FM_FREditorPanel(
        String frid,
        Date start, Date end,
        List<Form_FM_Report.DailySaleEntry> sales,
        List<Form_FM_Report.POEntry> pos,
        double totalRevenue,
        double totalExpenditure
    ) {
        setLayout(new BorderLayout(10,10));

        // — Header —
        JPanel hdr = new JPanel(new GridLayout(2,1));
        hdr.add(new JLabel("FRID: " + frid));
        hdr.add(new JLabel("From " + sdf.format(start) + " to " + sdf.format(end)));
        add(hdr, BorderLayout.NORTH);

        // — Tables & totals —
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        // Revenue table
        body.add(buildSection(
          "Revenue",
          new String[]{"Date", "Item Code", "Qty", "Amount"},
          sales.stream()
               .map(e -> new Object[]{sdf.format(e.date), e.itemCode, e.quantity, e.amount})
               .toArray(Object[][]::new),
          "Total Revenue:", totalRevenue
        ));

        // Expenditure table
        body.add(buildSection(
          "Expenditure",
          new String[]{"PO ID", "Date", "PR ID", "Item Code", "Qty", "Amount"},
          pos.stream()
             .map(e -> new Object[]{e.poId, sdf.format(e.date), e.prId, e.itemCode, e.quantity, e.amount})
             .toArray(Object[][]::new),
          "Total Expenditure:", totalExpenditure
        ));

        // Profit
        double profit = totalRevenue - totalExpenditure;
        JPanel profitP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        profitP.add(new JLabel("Profit:"));
        JLabel profLbl = new JLabel(String.format("%.2f", profit));
        if (profit < 0) profLbl.setText("-" + profLbl.getText());
        profitP.add(profLbl);
        body.add(profitP);

        add(new JScrollPane(body), BorderLayout.CENTER);

        // — Close button —
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        close.addActionListener(e -> SwingUtilities.getWindowAncestor(this).dispose());
        foot.add(close);
        add(foot, BorderLayout.SOUTH);
    }

    /** Helper to build the titled table + total line. */
    private JPanel buildSection(
      String title,
      String[] columns,
      Object[][] data,
      String totalLabel,
      double total
    ) {
        JPanel p = new JPanel(new BorderLayout(5,5));
        p.add(new JLabel(title), BorderLayout.NORTH);

        JTable tbl = new JTable(new DefaultTableModel(data, columns) {
            @Override public boolean isCellEditable(int r,int c){ return false; }
        });
        tbl.setRowHeight(24);
        p.add(new JScrollPane(tbl), BorderLayout.CENTER);

        JPanel totP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totP.add(new JLabel(totalLabel));
        totP.add(new JLabel(String.format("%.2f", total)));
        p.add(totP, BorderLayout.SOUTH);

        return p;
    }
}
