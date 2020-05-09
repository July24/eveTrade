package com.eve.entity.database;

import java.io.Serializable;

public class Items implements Serializable {
    private Integer id;

    private Double baseprice;

    private Integer graphicid;

    private Integer groupid;

    private Integer iconid;

    private Integer marketgroupid;

    private Integer metagroupid;

    private String cnName;

    private String enName;

    private Double volume;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getBaseprice() {
        return baseprice;
    }

    public void setBaseprice(Double baseprice) {
        this.baseprice = baseprice;
    }

    public Integer getGraphicid() {
        return graphicid;
    }

    public void setGraphicid(Integer graphicid) {
        this.graphicid = graphicid;
    }

    public Integer getGroupid() {
        return groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public Integer getIconid() {
        return iconid;
    }

    public void setIconid(Integer iconid) {
        this.iconid = iconid;
    }

    public Integer getMarketgroupid() {
        return marketgroupid;
    }

    public void setMarketgroupid(Integer marketgroupid) {
        this.marketgroupid = marketgroupid;
    }

    public Integer getMetagroupid() {
        return metagroupid;
    }

    public void setMetagroupid(Integer metagroupid) {
        this.metagroupid = metagroupid;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName == null ? null : cnName.trim();
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName == null ? null : enName.trim();
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", baseprice=").append(baseprice);
        sb.append(", graphicid=").append(graphicid);
        sb.append(", groupid=").append(groupid);
        sb.append(", iconid=").append(iconid);
        sb.append(", marketgroupid=").append(marketgroupid);
        sb.append(", metagroupid=").append(metagroupid);
        sb.append(", cnName=").append(cnName);
        sb.append(", enName=").append(enName);
        sb.append(", volume=").append(volume);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}