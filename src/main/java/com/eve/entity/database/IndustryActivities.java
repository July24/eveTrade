package com.eve.entity.database;

import java.io.Serializable;

public class IndustryActivities implements Serializable {
    private Integer id;

    private Integer blueprinttypeid;

    private Integer activityid;

    private Integer time;

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

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
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
        sb.append(", time=").append(time);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}