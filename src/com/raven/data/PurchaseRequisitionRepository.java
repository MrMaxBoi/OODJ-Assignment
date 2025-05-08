// PurchaseRequisitionRepository.java
package com.raven.data;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class PurchaseRequisitionRepository {
    private static final String FILE_PATH = "purchase_requisitions.txt";
    private static final String DELIMITER = "|";
    private static final String ITEM_DELIMITER = ":";
    
public static List<PurchaseRequisition> loadPRs() throws IOException {
    List<PurchaseRequisition> prs = new ArrayList<>();
    File file = new File(FILE_PATH);
    
    if (!file.exists()) {
        file.createNewFile();
        return prs;
    }
    
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length >= 5) {
                String prId = parts[0];
                Date dateRequired = new Date(Long.parseLong(parts[1]));
                String raisedBy = parts[2];
                String status = parts[3];
                
                // Parse items
                Map<String, Integer> items = new HashMap<>();
                if (!parts[4].isEmpty()) {
                    for (String itemPair : parts[4].split(",")) {
                        String[] itemParts = itemPair.split(":");
                        if (itemParts.length == 2) {
                            items.put(itemParts[0], Integer.parseInt(itemParts[1]));
                        }
                    }
                }
                
                PurchaseRequisition pr = new PurchaseRequisition(prId, dateRequired, items, raisedBy);
                pr.setStatus(status);
                prs.add(pr);
            }
        }
    }
    return prs;
}
    
    public static void savePRs(List<PurchaseRequisition> prs) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (PurchaseRequisition pr : prs) {
                writer.write(pr.toFileString());
                writer.newLine();
            }
        }
    }
    
    public static String generateNextPRId() throws IOException {
        List<PurchaseRequisition> prs = loadPRs();
        if (prs.isEmpty()) {
            return "PR001";
        }
        
        String lastId = prs.get(prs.size() - 1).getPrId();
        int number = Integer.parseInt(lastId.substring(2));
        return String.format("PR%03d", number + 1);
    }
}
