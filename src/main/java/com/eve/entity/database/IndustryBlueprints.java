package com.eve.entity.database;

import java.io.Serializable;

public class IndustryBlueprints implements Serializable {
    private Integer id;

    private Integer blueprinttypeid;

    private Integer maxproductionlimit;

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

    public Integer getMaxproductionlimit() {
        return maxproductionlimit;
    }

    public void setMaxproductionlimit(Integer maxproductionlimit) {
        this.maxproductionlimit = maxproductionlimit;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", blueprinttypeid=").append(blueprinttypeid);
        sb.append(", maxproductionlimit=").append(maxproductionlimit);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}