package com.eve.entity.database;

import java.util.ArrayList;
import java.util.List;

public class ItemsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ItemsExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andBasepriceIsNull() {
            addCriterion("basePrice is null");
            return (Criteria) this;
        }

        public Criteria andBasepriceIsNotNull() {
            addCriterion("basePrice is not null");
            return (Criteria) this;
        }

        public Criteria andBasepriceEqualTo(Double value) {
            addCriterion("basePrice =", value, "baseprice");
            return (Criteria) this;
        }

        public Criteria andBasepriceNotEqualTo(Double value) {
            addCriterion("basePrice <>", value, "baseprice");
            return (Criteria) this;
        }

        public Criteria andBasepriceGreaterThan(Double value) {
            addCriterion("basePrice >", value, "baseprice");
            return (Criteria) this;
        }

        public Criteria andBasepriceGreaterThanOrEqualTo(Double value) {
            addCriterion("basePrice >=", value, "baseprice");
            return (Criteria) this;
        }

        public Criteria andBasepriceLessThan(Double value) {
            addCriterion("basePrice <", value, "baseprice");
            return (Criteria) this;
        }

        public Criteria andBasepriceLessThanOrEqualTo(Double value) {
            addCriterion("basePrice <=", value, "baseprice");
            return (Criteria) this;
        }

        public Criteria andBasepriceIn(List<Double> values) {
            addCriterion("basePrice in", values, "baseprice");
            return (Criteria) this;
        }

        public Criteria andBasepriceNotIn(List<Double> values) {
            addCriterion("basePrice not in", values, "baseprice");
            return (Criteria) this;
        }

        public Criteria andBasepriceBetween(Double value1, Double value2) {
            addCriterion("basePrice between", value1, value2, "baseprice");
            return (Criteria) this;
        }

        public Criteria andBasepriceNotBetween(Double value1, Double value2) {
            addCriterion("basePrice not between", value1, value2, "baseprice");
            return (Criteria) this;
        }

        public Criteria andGraphicidIsNull() {
            addCriterion("graphicID is null");
            return (Criteria) this;
        }

        public Criteria andGraphicidIsNotNull() {
            addCriterion("graphicID is not null");
            return (Criteria) this;
        }

        public Criteria andGraphicidEqualTo(Integer value) {
            addCriterion("graphicID =", value, "graphicid");
            return (Criteria) this;
        }

        public Criteria andGraphicidNotEqualTo(Integer value) {
            addCriterion("graphicID <>", value, "graphicid");
            return (Criteria) this;
        }

        public Criteria andGraphicidGreaterThan(Integer value) {
            addCriterion("graphicID >", value, "graphicid");
            return (Criteria) this;
        }

        public Criteria andGraphicidGreaterThanOrEqualTo(Integer value) {
            addCriterion("graphicID >=", value, "graphicid");
            return (Criteria) this;
        }

        public Criteria andGraphicidLessThan(Integer value) {
            addCriterion("graphicID <", value, "graphicid");
            return (Criteria) this;
        }

        public Criteria andGraphicidLessThanOrEqualTo(Integer value) {
            addCriterion("graphicID <=", value, "graphicid");
            return (Criteria) this;
        }

        public Criteria andGraphicidIn(List<Integer> values) {
            addCriterion("graphicID in", values, "graphicid");
            return (Criteria) this;
        }

        public Criteria andGraphicidNotIn(List<Integer> values) {
            addCriterion("graphicID not in", values, "graphicid");
            return (Criteria) this;
        }

        public Criteria andGraphicidBetween(Integer value1, Integer value2) {
            addCriterion("graphicID between", value1, value2, "graphicid");
            return (Criteria) this;
        }

        public Criteria andGraphicidNotBetween(Integer value1, Integer value2) {
            addCriterion("graphicID not between", value1, value2, "graphicid");
            return (Criteria) this;
        }

        public Criteria andGroupidIsNull() {
            addCriterion("groupID is null");
            return (Criteria) this;
        }

        public Criteria andGroupidIsNotNull() {
            addCriterion("groupID is not null");
            return (Criteria) this;
        }

        public Criteria andGroupidEqualTo(Integer value) {
            addCriterion("groupID =", value, "groupid");
            return (Criteria) this;
        }

        public Criteria andGroupidNotEqualTo(Integer value) {
            addCriterion("groupID <>", value, "groupid");
            return (Criteria) this;
        }

        public Criteria andGroupidGreaterThan(Integer value) {
            addCriterion("groupID >", value, "groupid");
            return (Criteria) this;
        }

        public Criteria andGroupidGreaterThanOrEqualTo(Integer value) {
            addCriterion("groupID >=", value, "groupid");
            return (Criteria) this;
        }

        public Criteria andGroupidLessThan(Integer value) {
            addCriterion("groupID <", value, "groupid");
            return (Criteria) this;
        }

        public Criteria andGroupidLessThanOrEqualTo(Integer value) {
            addCriterion("groupID <=", value, "groupid");
            return (Criteria) this;
        }

        public Criteria andGroupidIn(List<Integer> values) {
            addCriterion("groupID in", values, "groupid");
            return (Criteria) this;
        }

        public Criteria andGroupidNotIn(List<Integer> values) {
            addCriterion("groupID not in", values, "groupid");
            return (Criteria) this;
        }

        public Criteria andGroupidBetween(Integer value1, Integer value2) {
            addCriterion("groupID between", value1, value2, "groupid");
            return (Criteria) this;
        }

        public Criteria andGroupidNotBetween(Integer value1, Integer value2) {
            addCriterion("groupID not between", value1, value2, "groupid");
            return (Criteria) this;
        }

        public Criteria andIconidIsNull() {
            addCriterion("iconID is null");
            return (Criteria) this;
        }

        public Criteria andIconidIsNotNull() {
            addCriterion("iconID is not null");
            return (Criteria) this;
        }

        public Criteria andIconidEqualTo(Integer value) {
            addCriterion("iconID =", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidNotEqualTo(Integer value) {
            addCriterion("iconID <>", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidGreaterThan(Integer value) {
            addCriterion("iconID >", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidGreaterThanOrEqualTo(Integer value) {
            addCriterion("iconID >=", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidLessThan(Integer value) {
            addCriterion("iconID <", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidLessThanOrEqualTo(Integer value) {
            addCriterion("iconID <=", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidIn(List<Integer> values) {
            addCriterion("iconID in", values, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidNotIn(List<Integer> values) {
            addCriterion("iconID not in", values, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidBetween(Integer value1, Integer value2) {
            addCriterion("iconID between", value1, value2, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidNotBetween(Integer value1, Integer value2) {
            addCriterion("iconID not between", value1, value2, "iconid");
            return (Criteria) this;
        }

        public Criteria andMarketgroupidIsNull() {
            addCriterion("marketGroupID is null");
            return (Criteria) this;
        }

        public Criteria andMarketgroupidIsNotNull() {
            addCriterion("marketGroupID is not null");
            return (Criteria) this;
        }

        public Criteria andMarketgroupidEqualTo(Integer value) {
            addCriterion("marketGroupID =", value, "marketgroupid");
            return (Criteria) this;
        }

        public Criteria andMarketgroupidNotEqualTo(Integer value) {
            addCriterion("marketGroupID <>", value, "marketgroupid");
            return (Criteria) this;
        }

        public Criteria andMarketgroupidGreaterThan(Integer value) {
            addCriterion("marketGroupID >", value, "marketgroupid");
            return (Criteria) this;
        }

        public Criteria andMarketgroupidGreaterThanOrEqualTo(Integer value) {
            addCriterion("marketGroupID >=", value, "marketgroupid");
            return (Criteria) this;
        }

        public Criteria andMarketgroupidLessThan(Integer value) {
            addCriterion("marketGroupID <", value, "marketgroupid");
            return (Criteria) this;
        }

        public Criteria andMarketgroupidLessThanOrEqualTo(Integer value) {
            addCriterion("marketGroupID <=", value, "marketgroupid");
            return (Criteria) this;
        }

        public Criteria andMarketgroupidIn(List<Integer> values) {
            addCriterion("marketGroupID in", values, "marketgroupid");
            return (Criteria) this;
        }

        public Criteria andMarketgroupidNotIn(List<Integer> values) {
            addCriterion("marketGroupID not in", values, "marketgroupid");
            return (Criteria) this;
        }

        public Criteria andMarketgroupidBetween(Integer value1, Integer value2) {
            addCriterion("marketGroupID between", value1, value2, "marketgroupid");
            return (Criteria) this;
        }

        public Criteria andMarketgroupidNotBetween(Integer value1, Integer value2) {
            addCriterion("marketGroupID not between", value1, value2, "marketgroupid");
            return (Criteria) this;
        }

        public Criteria andMetagroupidIsNull() {
            addCriterion("metaGroupID is null");
            return (Criteria) this;
        }

        public Criteria andMetagroupidIsNotNull() {
            addCriterion("metaGroupID is not null");
            return (Criteria) this;
        }

        public Criteria andMetagroupidEqualTo(Integer value) {
            addCriterion("metaGroupID =", value, "metagroupid");
            return (Criteria) this;
        }

        public Criteria andMetagroupidNotEqualTo(Integer value) {
            addCriterion("metaGroupID <>", value, "metagroupid");
            return (Criteria) this;
        }

        public Criteria andMetagroupidGreaterThan(Integer value) {
            addCriterion("metaGroupID >", value, "metagroupid");
            return (Criteria) this;
        }

        public Criteria andMetagroupidGreaterThanOrEqualTo(Integer value) {
            addCriterion("metaGroupID >=", value, "metagroupid");
            return (Criteria) this;
        }

        public Criteria andMetagroupidLessThan(Integer value) {
            addCriterion("metaGroupID <", value, "metagroupid");
            return (Criteria) this;
        }

        public Criteria andMetagroupidLessThanOrEqualTo(Integer value) {
            addCriterion("metaGroupID <=", value, "metagroupid");
            return (Criteria) this;
        }

        public Criteria andMetagroupidIn(List<Integer> values) {
            addCriterion("metaGroupID in", values, "metagroupid");
            return (Criteria) this;
        }

        public Criteria andMetagroupidNotIn(List<Integer> values) {
            addCriterion("metaGroupID not in", values, "metagroupid");
            return (Criteria) this;
        }

        public Criteria andMetagroupidBetween(Integer value1, Integer value2) {
            addCriterion("metaGroupID between", value1, value2, "metagroupid");
            return (Criteria) this;
        }

        public Criteria andMetagroupidNotBetween(Integer value1, Integer value2) {
            addCriterion("metaGroupID not between", value1, value2, "metagroupid");
            return (Criteria) this;
        }

        public Criteria andCnNameIsNull() {
            addCriterion("cn_name is null");
            return (Criteria) this;
        }

        public Criteria andCnNameIsNotNull() {
            addCriterion("cn_name is not null");
            return (Criteria) this;
        }

        public Criteria andCnNameEqualTo(String value) {
            addCriterion("cn_name =", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameNotEqualTo(String value) {
            addCriterion("cn_name <>", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameGreaterThan(String value) {
            addCriterion("cn_name >", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameGreaterThanOrEqualTo(String value) {
            addCriterion("cn_name >=", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameLessThan(String value) {
            addCriterion("cn_name <", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameLessThanOrEqualTo(String value) {
            addCriterion("cn_name <=", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameLike(String value) {
            addCriterion("cn_name like", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameNotLike(String value) {
            addCriterion("cn_name not like", value, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameIn(List<String> values) {
            addCriterion("cn_name in", values, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameNotIn(List<String> values) {
            addCriterion("cn_name not in", values, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameBetween(String value1, String value2) {
            addCriterion("cn_name between", value1, value2, "cnName");
            return (Criteria) this;
        }

        public Criteria andCnNameNotBetween(String value1, String value2) {
            addCriterion("cn_name not between", value1, value2, "cnName");
            return (Criteria) this;
        }

        public Criteria andEnNameIsNull() {
            addCriterion("en_name is null");
            return (Criteria) this;
        }

        public Criteria andEnNameIsNotNull() {
            addCriterion("en_name is not null");
            return (Criteria) this;
        }

        public Criteria andEnNameEqualTo(String value) {
            addCriterion("en_name =", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameNotEqualTo(String value) {
            addCriterion("en_name <>", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameGreaterThan(String value) {
            addCriterion("en_name >", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameGreaterThanOrEqualTo(String value) {
            addCriterion("en_name >=", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameLessThan(String value) {
            addCriterion("en_name <", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameLessThanOrEqualTo(String value) {
            addCriterion("en_name <=", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameLike(String value) {
            addCriterion("en_name like", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameNotLike(String value) {
            addCriterion("en_name not like", value, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameIn(List<String> values) {
            addCriterion("en_name in", values, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameNotIn(List<String> values) {
            addCriterion("en_name not in", values, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameBetween(String value1, String value2) {
            addCriterion("en_name between", value1, value2, "enName");
            return (Criteria) this;
        }

        public Criteria andEnNameNotBetween(String value1, String value2) {
            addCriterion("en_name not between", value1, value2, "enName");
            return (Criteria) this;
        }

        public Criteria andVolumeIsNull() {
            addCriterion("volume is null");
            return (Criteria) this;
        }

        public Criteria andVolumeIsNotNull() {
            addCriterion("volume is not null");
            return (Criteria) this;
        }

        public Criteria andVolumeEqualTo(Double value) {
            addCriterion("volume =", value, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeNotEqualTo(Double value) {
            addCriterion("volume <>", value, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeGreaterThan(Double value) {
            addCriterion("volume >", value, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeGreaterThanOrEqualTo(Double value) {
            addCriterion("volume >=", value, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeLessThan(Double value) {
            addCriterion("volume <", value, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeLessThanOrEqualTo(Double value) {
            addCriterion("volume <=", value, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeIn(List<Double> values) {
            addCriterion("volume in", values, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeNotIn(List<Double> values) {
            addCriterion("volume not in", values, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeBetween(Double value1, Double value2) {
            addCriterion("volume between", value1, value2, "volume");
            return (Criteria) this;
        }

        public Criteria andVolumeNotBetween(Double value1, Double value2) {
            addCriterion("volume not between", value1, value2, "volume");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}