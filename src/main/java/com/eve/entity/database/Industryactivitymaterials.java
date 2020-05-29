package com.eve.entity.database;

import java.io.Serializable;

public class Industryactivitymaterials implements Serializable {
    private Integer typeid;

    private Integer activityid;

    private Integer materialtypeid;

    private Integer quantity;

    private static final long serialVersionUID = 1L;

    public Integer getTypeid() {
        return typeid;
    }

    public void setTypeid(Integer typeid) {
        this.typeid = typeid;
    }

    public Integer getActivityid() {
        return activityid;
    }

    public void setActivityid(Integer activityid) {
        this.activityid = activityid;
    }

    public Integer getMaterialtypeid() {
        return materialtypeid;
    }

    public void setMaterialtypeid(Integer materialtypeid) {
        this.materialtypeid = materialtypeid;
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
        sb.append(", typeid=").append(typeid);
        sb.append(", activityid=").append(activityid);
        sb.append(", materialtypeid=").append(materialtypeid);
        sb.append(", quantity=").append(quantity);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}