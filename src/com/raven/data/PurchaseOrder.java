/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raven.data;

import java.util.Date;
import java.util.List;

public class PurchaseOrder {
    private String poId;
    private Date dateRequired;
    private String raisedBy;
    private String status;
    private List<POItem> items;

    public PurchaseOrder(String poId, Date dateRequired, String raisedBy, String status, List<POItem> items) {
        this.poId = poId;
        this.dateRequired = dateRequired;
        this.raisedBy = raisedBy;
        this.status = status;
        this.items = items;
    }

    // Getters
    public String getPoId() { return poId; }
    public Date getDateRequired() { return dateRequired; }
    public String getRaisedBy() { return raisedBy; }
    public String getStatus() { return status; }
    public List<POItem> getItems() { return items; }
    
    //Setter for Status
    public void setStatus(String status){
        this.status = status;
    }
}

