package com.eve.entity.database;

import java.util.ArrayList;
import java.util.List;

public class InvmarketgroupsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public InvmarketgroupsExample() {
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

        public Criteria andMarketgroupnameIsNull() {
            addCriterion("marketGroupName is null");
            return (Criteria) this;
        }

        public Criteria andMarketgroupnameIsNotNull() {
            addCriterion("marketGroupName is not null");
            return (Criteria) this;
        }

        public Criteria andMarketgroupnameEqualTo(String value) {
            addCriterion("marketGroupName =", value, "marketgroupname");
            return (Criteria) this;
        }

        public Criteria andMarketgroupnameNotEqualTo(String value) {
            addCriterion("marketGroupName <>", value, "marketgroupname");
            return (Criteria) this;
        }

        public Criteria andMarketgroupnameGreaterThan(String value) {
            addCriterion("marketGroupName >", value, "marketgroupname");
            return (Criteria) this;
        }

        public Criteria andMarketgroupnameGreaterThanOrEqualTo(String value) {
            addCriterion("marketGroupName >=", value, "marketgroupname");
            return (Criteria) this;
        }

        public Criteria andMarketgroupnameLessThan(String value) {
            addCriterion("marketGroupName <", value, "marketgroupname");
            return (Criteria) this;
        }

        public Criteria andMarketgroupnameLessThanOrEqualTo(String value) {
            addCriterion("marketGroupName <=", value, "marketgroupname");
            return (Criteria) this;
        }

        public Criteria andMarketgroupnameLike(String value) {
            addCriterion("marketGroupName like", value, "marketgroupname");
            return (Criteria) this;
        }

        public Criteria andMarketgroupnameNotLike(String value) {
            addCriterion("marketGroupName not like", value, "marketgroupname");
            return (Criteria) this;
        }

        public Criteria andMarketgroupnameIn(List<String> values) {
            addCriterion("marketGroupName in", values, "marketgroupname");
            return (Criteria) this;
        }

        public Criteria andMarketgroupnameNotIn(List<String> values) {
            addCriterion("marketGroupName not in", values, "marketgroupname");
            return (Criteria) this;
        }

        public Criteria andMarketgroupnameBetween(String value1, String value2) {
            addCriterion("marketGroupName between", value1, value2, "marketgroupname");
            return (Criteria) this;
        }

        public Criteria andMarketgroupnameNotBetween(String value1, String value2) {
            addCriterion("marketGroupName not between", value1, value2, "marketgroupname");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
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

        public Criteria andHastypesIsNull() {
            addCriterion("hasTypes is null");
            return (Criteria) this;
        }

        public Criteria andHastypesIsNotNull() {
            addCriterion("hasTypes is not null");
            return (Criteria) this;
        }

        public Criteria andHastypesEqualTo(Boolean value) {
            addCriterion("hasTypes =", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesNotEqualTo(Boolean value) {
            addCriterion("hasTypes <>", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesGreaterThan(Boolean value) {
            addCriterion("hasTypes >", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesGreaterThanOrEqualTo(Boolean value) {
            addCriterion("hasTypes >=", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesLessThan(Boolean value) {
            addCriterion("hasTypes <", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesLessThanOrEqualTo(Boolean value) {
            addCriterion("hasTypes <=", value, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesIn(List<Boolean> values) {
            addCriterion("hasTypes in", values, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesNotIn(List<Boolean> values) {
            addCriterion("hasTypes not in", values, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesBetween(Boolean value1, Boolean value2) {
            addCriterion("hasTypes between", value1, value2, "hastypes");
            return (Criteria) this;
        }

        public Criteria andHastypesNotBetween(Boolean value1, Boolean value2) {
            addCriterion("hasTypes not between", value1, value2, "hastypes");
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