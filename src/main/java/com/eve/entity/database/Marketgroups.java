package com.eve.entity.database;

import java.io.Serializable;

public class Marketgroups implements Serializable {
    private Integer marketgroupid;

    private String descriptionid;

    private String hastypes;

    private String iconid;

    private String nameid;

    private Integer parentgroupid;

    private static final long serialVersionUID = 1L;

    public Integer getMarketgroupid() {
        return marketgroupid;
    }

    public void setMarketgroupid(Integer marketgroupid) {
        this.marketgroupid = marketgroupid;
    }

    public String getDescriptionid() {
        return descriptionid;
    }

    public void setDescriptionid(String descriptionid) {
        this.descriptionid = descriptionid == null ? null : descriptionid.trim();
    }

    public String getHastypes() {
        return hastypes;
    }

    public void setHastypes(String hastypes) {
        this.hastypes = hastypes == null ? null : hastypes.trim();
    }

    public String getIconid() {
        return iconid;
    }

    public void setIconid(String iconid) {
        this.iconid = iconid == null ? null : iconid.trim();
    }

    public String getNameid() {
        return nameid;
    }

    public void setNameid(String nameid) {
        this.nameid = nameid == null ? null : nameid.trim();
    }

    public Integer getParentgroupid() {
        return parentgroupid;
    }

    public void setParentgroupid(Integer parentgroupid) {
        this.parentgroupid = parentgroupid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", marketgroupid=").append(marketgroupid);
        sb.append(", descriptionid=").append(descriptionid);
        sb.append(", hastypes=").append(hastypes);
        sb.append(", iconid=").append(iconid);
        sb.append(", nameid=").append(nameid);
        sb.append(", parentgroupid=").append(parentgroupid);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}