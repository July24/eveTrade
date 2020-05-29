package com.eve.entity.database;

import java.io.Serializable;

public class Invmarketgroups implements Serializable {
    private Integer marketgroupid;

    private Integer parentgroupid;

    private String marketgroupname;

    private String description;

    private Integer iconid;

    private Boolean hastypes;

    private static final long serialVersionUID = 1L;

    public Integer getMarketgroupid() {
        return marketgroupid;
    }

    public void setMarketgroupid(Integer marketgroupid) {
        this.marketgroupid = marketgroupid;
    }

    public Integer getParentgroupid() {
        return parentgroupid;
    }

    public void setParentgroupid(Integer parentgroupid) {
        this.parentgroupid = parentgroupid;
    }

    public String getMarketgroupname() {
        return marketgroupname;
    }

    public void setMarketgroupname(String marketgroupname) {
        this.marketgroupname = marketgroupname == null ? null : marketgroupname.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getIconid() {
        return iconid;
    }

    public void setIconid(Integer iconid) {
        this.iconid = iconid;
    }

    public Boolean getHastypes() {
        return hastypes;
    }

    public void setHastypes(Boolean hastypes) {
        this.hastypes = hastypes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", marketgroupid=").append(marketgroupid);
        sb.append(", parentgroupid=").append(parentgroupid);
        sb.append(", marketgroupname=").append(marketgroupname);
        sb.append(", description=").append(description);
        sb.append(", iconid=").append(iconid);
        sb.append(", hastypes=").append(hastypes);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}