package com.eve.entity.database;

import java.io.Serializable;

public class CorpExchangeMaterials implements Serializable {
    private String corporationName;

    private String itemName;

    private String materialsName;

    private Integer materialsQuantity;

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

    public String getMaterialsName() {
        return materialsName;
    }

    public void setMaterialsName(String materialsName) {
        this.materialsName = materialsName == null ? null : materialsName.trim();
    }

    public Integer getMaterialsQuantity() {
        return materialsQuantity;
    }

    public void setMaterialsQuantity(Integer materialsQuantity) {
        this.materialsQuantity = materialsQuantity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", corporationName=").append(corporationName);
        sb.append(", itemName=").append(itemName);
        sb.append(", materialsName=").append(materialsName);
        sb.append(", materialsQuantity=").append(materialsQuantity);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}