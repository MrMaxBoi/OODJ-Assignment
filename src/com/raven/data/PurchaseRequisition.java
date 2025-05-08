// PurchaseRequisition.java
package com.raven.data;

import java.util.*;
import java.util.stream.Collectors;

public class PurchaseRequisition {
    private String prId;
    private Date dateRequired;
    private Map<String, Integer> items; // Item code -> Quantity
    private String raisedBy;
    private String status = "Pending";
    
    public PurchaseRequisition(String prId, Date dateRequired, Map<String, Integer> items, String raisedBy) {
        this.prId = prId;
        this.dateRequired = dateRequired;
        this.items = new HashMap<>(items);
        this.raisedBy = raisedBy;
        this.status = "Pending";
    }
    
    public String toFileString() {
        // Format: PR001|1620000000000|SM001|Pending|IC001:50,IC002:30
        String itemsString = items.entrySet().stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.joining(","));
        
        return String.join("|", 
            prId,
            String.valueOf(dateRequired.getTime()),
            raisedBy,
            status,
            itemsString
        );
    }
    
    // Getters and setters
    public void addItem(String itemCode, int quantity) {
        items.put(itemCode, quantity);
    }
    
    public void removeItem(String itemCode) {
        items.remove(itemCode);
    }

    public String getPrId() {
        return prId;
    }

    public void setPrId(String prId) {
        this.prId = prId;
    }

    public Date getDateRequired() {
        return dateRequired;
    }

    public void setDateRequired(Date dateRequired) {
        this.dateRequired = dateRequired;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public String getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(String raisedBy) {
        this.raisedBy = raisedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
