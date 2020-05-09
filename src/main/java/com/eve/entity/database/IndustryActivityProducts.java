package com.eve.entity.database;

import java.io.Serializable;

public class IndustryActivityProducts implements Serializable {
    private Integer id;

    private Integer blueprinttypeid;

    private Integer activityid;

    private Integer producttypeid;

    private Integer quantity;

    private Double probability;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBlueprinttypeid() {
        return blueprinttypeid;
    }

    public void setBlueprinttypeid(Integer blueprinttypeid) {
        this.blueprinttypeid = blueprinttypeid;
    }

    public Integer getActivityid() {
        return activityid;
    }

    public void setActivityid(Integer activityid) {
        this.activityid = activityid;
    }

    public Integer getProducttypeid() {
        return producttypeid;
    }

    public void setProducttypeid(Integer producttypeid) {
        this.producttypeid = producttypeid;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", blueprinttypeid=").append(blueprinttypeid);
        sb.append(", activityid=").append(activityid);
        sb.append(", producttypeid=").append(producttypeid);
        sb.append(", quantity=").append(quantity);
        sb.append(", probability=").append(probability);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}