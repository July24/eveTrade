package com.eve.entity.database;

import java.io.Serializable;

public class CorpExchange implements Serializable {
    private String corporationName;

    private String itemName;

    private Integer lpCost;

    private Integer iskCost;

    private Integer quantity;

    private static final long serialVersionUID = 1L;

    public String getCorporationName() {
        return corporationName;
    }

    public void setCorporationName(String corporationName) {
        this.corporationName = corporationName == null ? null : corporationName.trim();
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName == null ? null : itemName.trim();
    }

    public Integer getLpCost() {
        return lpCost;
    }

    public void setLpCost(Integer lpCost) {
        this.lpCost = lpCost;
    }

    public Integer getIskCost() {
        return iskCost;
    }

    public void setIskCost(Integer iskCost) {
        this.iskCost = iskCost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", corporationName=").append(corporationName);
        sb.append(", itemName=").append(itemName);
        sb.append(", lpCost=").append(lpCost);
        sb.append(", iskCost=").append(iskCost);
        sb.append(", quantity=").append(quantity);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}