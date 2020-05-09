package com.eve.entity.database;

import java.io.Serializable;

public class IndustryActivitySkills implements Serializable {
    private Integer id;

    private Integer blueprinttypeid;

    private Integer activityid;

    private Integer skillid;

    private Integer level;

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

    public Integer getSkillid() {
        return skillid;
    }

    public void setSkillid(Integer skillid) {
        this.skillid = skillid;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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
        sb.append(", skillid=").append(skillid);
        sb.append(", level=").append(level);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}