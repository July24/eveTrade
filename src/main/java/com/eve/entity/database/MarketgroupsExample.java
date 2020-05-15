package com.eve.entity.database;

import java.util.ArrayList;
import java.util.List;

public class MarketgroupsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public MarketgroupsExample() {
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

        public Criteria andDescriptionidIsNull() {
            addCriterion("descriptionID is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionidIsNotNull() {
            addCriterion("descriptionID is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionidEqualTo(String value) {
            addCriterion("descriptionID =", value, "descriptionid");
            return (Criteria) this;
        }

        public Criteria andDescriptionidNotEqualTo(String value) {
            addCriterion("descriptionID <>", value, "descriptionid");
            return (Criteria) this;
        }

        public Criteria andDescriptionidGreaterThan(String value) {
            addCriterion("descriptionID >", value, "descriptionid");
            return (Criteria) this;
        }

        public Criteria andDescriptionidGreaterThanOrEqualTo(String value) {
            addCriterion("descriptionID >=", value, "descriptionid");
            return (Criteria) this;
        }

        public Criteria andDescriptionidLessThan(String value) {
            addCriterion("descriptionID <", value, "descriptionid");
            return (Criteria) this;
        }

        public Criteria andDescriptionidLessThanOrEqualTo(String value) {
            addCriterion("descriptionID <=", value, "descriptionid");
            return (Criteria) this;
        }

        public Criteria andDescriptionidLike(String value) {
            addCriterion("descriptionID like", value, "descriptionid");
            return (Criteria) this;
        }

        public Criteria andDescriptionidNotLike(String value) {
            addCriterion("descriptionID not like", value, "descriptionid");
            return (Criteria) this;
        }

        public Criteria andDescriptionidIn(List<String> values) {
            addCriterion("descriptionID in", values, "descriptionid");
            return (Criteria) this;
        }

        public Criteria andDescriptionidNotIn(List<String> values) {
            addCriterion("descriptionID not in", values, "descriptionid");
            return (Criteria) this;
        }

        public Criteria andDescriptionidBetween(String value1, String value2) {
            addCriterion("descriptionID between", value1, value2, "descriptionid");
            return (Criteria) this;
        }

        public Criteria andDescriptionidNotBetween(String value1, String value2) {
            addCriterion("descriptionID not between", value1, value2, "descriptionid");
            return (Criteria) this;
        }

        public Criteria andHastypesIsNull() {
            addCriterion("hasTypes is null");
            return (Criteria) this;
        }

        public Criteria andHastypesIsNotNull() {
            addCriterion("hasTypes is not null");
            return (Criteria) this;
        }

        public Criteria andHastypesEqualTo(String value) {
            addCriterion("hasTypes =", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesNotEqualTo(String value) {
            addCriterion("hasTypes <>", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesGreaterThan(String value) {
            addCriterion("hasTypes >", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesGreaterThanOrEqualTo(String value) {
            addCriterion("hasTypes >=", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesLessThan(String value) {
            addCriterion("hasTypes <", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesLessThanOrEqualTo(String value) {
            addCriterion("hasTypes <=", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesLike(String value) {
            addCriterion("hasTypes like", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesNotLike(String value) {
            addCriterion("hasTypes not like", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesIn(List<String> values) {
            addCriterion("hasTypes in", values, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesNotIn(List<String> values) {
            addCriterion("hasTypes not in", values, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesBetween(String value1, String value2) {
            addCriterion("hasTypes between", value1, value2, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesNotBetween(String value1, String value2) {
            addCriterion("hasTypes not between", value1, value2, "hastypes");
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

        public Criteria andIconidEqualTo(String value) {
            addCriterion("iconID =", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidNotEqualTo(String value) {
            addCriterion("iconID <>", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidGreaterThan(String value) {
            addCriterion("iconID >", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidGreaterThanOrEqualTo(String value) {
            addCriterion("iconID >=", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidLessThan(String value) {
            addCriterion("iconID <", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidLessThanOrEqualTo(String value) {
            addCriterion("iconID <=", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidLike(String value) {
            addCriterion("iconID like", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidNotLike(String value) {
            addCriterion("iconID not like", value, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidIn(List<String> values) {
            addCriterion("iconID in", values, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidNotIn(List<String> values) {
            addCriterion("iconID not in", values, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidBetween(String value1, String value2) {
            addCriterion("iconID between", value1, value2, "iconid");
            return (Criteria) this;
        }

        public Criteria andIconidNotBetween(String value1, String value2) {
            addCriterion("iconID not between", value1, value2, "iconid");
            return (Criteria) this;
        }

        public Criteria andNameidIsNull() {
            addCriterion("nameID is null");
            return (Criteria) this;
        }

        public Criteria andNameidIsNotNull() {
            addCriterion("nameID is not null");
            return (Criteria) this;
        }

        public Criteria andNameidEqualTo(String value) {
            addCriterion("nameID =", value, "nameid");
            return (Criteria) this;
        }

        public Criteria andNameidNotEqualTo(String value) {
            addCriterion("nameID <>", value, "nameid");
            return (Criteria) this;
        }

        public Criteria andNameidGreaterThan(String value) {
            addCriterion("nameID >", value, "nameid");
            return (Criteria) this;
        }

        public Criteria andNameidGreaterThanOrEqualTo(String value) {
            addCriterion("nameID >=", value, "nameid");
            return (Criteria) this;
        }

        public Criteria andNameidLessThan(String value) {
            addCriterion("nameID <", value, "nameid");
            return (Criteria) this;
        }

        public Criteria andNameidLessThanOrEqualTo(String value) {
            addCriterion("nameID <=", value, "nameid");
            return (Criteria) this;
        }

        public Criteria andNameidLike(String value) {
            addCriterion("nameID like", value, "nameid");
            return (Criteria) this;
        }

        public Criteria andNameidNotLike(String value) {
            addCriterion("nameID not like", value, "nameid");
            return (Criteria) this;
        }

        public Criteria andNameidIn(List<String> values) {
            addCriterion("nameID in", values, "nameid");
            return (Criteria) this;
        }

        public Criteria andNameidNotIn(List<String> values) {
            addCriterion("nameID not in", values, "nameid");
            return (Criteria) this;
        }

        public Criteria andNameidBetween(String value1, String value2) {
            addCriterion("nameID between", value1, value2, "nameid");
            return (Criteria) this;
        }

        public Criteria andNameidNotBetween(String value1, String value2) {
            addCriterion("nameID not between", value1, value2, "nameid");
            return (Criteria) this;
        }

        public Criteria andParentgroupidIsNull() {
            addCriterion("parentGroupID is null");
            return (Criteria) this;
        }

        public Criteria andParentgroupidIsNotNull() {
            addCriterion("parentGroupID is not null");
            return (Criteria) this;
        }

        public Criteria andParentgroupidEqualTo(Integer value) {
            addCriterion("parentGroupID =", value, "parentgroupid");
            return (Criteria) this;
        }

        public Criteria andParentgroupidNotEqualTo(Integer value) {
            addCriterion("parentGroupID <>", value, "parentgroupid");
            return (Criteria) this;
        }

        public Criteria andParentgroupidGreaterThan(Integer value) {
            addCriterion("parentGroupID >", value, "parentgroupid");
            return (Criteria) this;
        }

        public Criteria andParentgroupidGreaterThanOrEqualTo(Integer value) {
            addCriterion("parentGroupID >=", value, "parentgroupid");
            return (Criteria) this;
        }

        public Criteria andParentgroupidLessThan(Integer value) {
            addCriterion("parentGroupID <", value, "parentgroupid");
            return (Criteria) this;
        }

        public Criteria andParentgroupidLessThanOrEqualTo(Integer value) {
            addCriterion("parentGroupID <=", value, "parentgroupid");
            return (Criteria) this;
        }

        public Criteria andParentgroupidIn(List<Integer> values) {
            addCriterion("parentGroupID in", values, "parentgroupid");
            return (Criteria) this;
        }

        public Criteria andParentgroupidNotIn(List<Integer> values) {
            addCriterion("parentGroupID not in", values, "parentgroupid");
            return (Criteria) this;
        }

        public Criteria andParentgroupidBetween(Integer value1, Integer value2) {
            addCriterion("parentGroupID between", value1, value2, "parentgroupid");
            return (Criteria) this;
        }

        public Criteria andParentgroupidNotBetween(Integer value1, Integer value2) {
            addCriterion("parentGroupID not between", value1, value2, "parentgroupid");
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