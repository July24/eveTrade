package com.eve.entity.database;

import java.io.Serializable;

public class Items implements Serializable {
    private Integer id;

    private String enName;

    private String cnName;

    private Float volumn;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName == null ? null : enName.trim();
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName == null ? null : cnName.trim();
    }

    public Float getVolumn() {
        return volumn;
    }

    public void setVolumn(Float volumn) {
        this.volumn = volumn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", enName=").append(enName);
        sb.append(", cnName=").append(cnName);
        sb.append(", volumn=").append(volumn);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}