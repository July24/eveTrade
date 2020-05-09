package com.eve.entity.database;

import java.util.ArrayList;
import java.util.List;

public class IndustryActivityProductsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public IndustryActivityProductsExample() {
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

        public Criteria andBlueprinttypeidIsNull() {
            addCriterion("blueprintTypeID is null");
            return (Criteria) this;
        }

        public Criteria andBlueprinttypeidIsNotNull() {
            addCriterion("blueprintTypeID is not null");
            return (Criteria) this;
        }

        public Criteria andBlueprinttypeidEqualTo(Integer value) {
            addCriterion("blueprintTypeID =", value, "blueprinttypeid");
            return (Criteria) this;
        }

        public Criteria andBlueprinttypeidNotEqualTo(Integer value) {
            addCriterion("blueprintTypeID <>", value, "blueprinttypeid");
            return (Criteria) this;
        }

        public Criteria andBlueprinttypeidGreaterThan(Integer value) {
            addCriterion("blueprintTypeID >", value, "blueprinttypeid");
            return (Criteria) this;
        }

        public Criteria andBlueprinttypeidGreaterThanOrEqualTo(Integer value) {
            addCriterion("blueprintTypeID >=", value, "blueprinttypeid");
            return (Criteria) this;
        }

        public Criteria andBlueprinttypeidLessThan(Integer value) {
            addCriterion("blueprintTypeID <", value, "blueprinttypeid");
            return (Criteria) this;
        }

        public Criteria andBlueprinttypeidLessThanOrEqualTo(Integer value) {
            addCriterion("blueprintTypeID <=", value, "blueprinttypeid");
            return (Criteria) this;
        }

        public Criteria andBlueprinttypeidIn(List<Integer> values) {
            addCriterion("blueprintTypeID in", values, "blueprinttypeid");
            return (Criteria) this;
        }

        public Criteria andBlueprinttypeidNotIn(List<Integer> values) {
            addCriterion("blueprintTypeID not in", values, "blueprinttypeid");
            return (Criteria) this;
        }

        public Criteria andBlueprinttypeidBetween(Integer value1, Integer value2) {
            addCriterion("blueprintTypeID between", value1, value2, "blueprinttypeid");
            return (Criteria) this;
        }

        public Criteria andBlueprinttypeidNotBetween(Integer value1, Integer value2) {
            addCriterion("blueprintTypeID not between", value1, value2, "blueprinttypeid");
            return (Criteria) this;
        }

        public Criteria andActivityidIsNull() {
            addCriterion("activityID is null");
            return (Criteria) this;
        }

        public Criteria andActivityidIsNotNull() {
            addCriterion("activityID is not null");
            return (Criteria) this;
        }

        public Criteria andActivityidEqualTo(Integer value) {
            addCriterion("activityID =", value, "activityid");
            return (Criteria) this;
        }

        public Criteria andActivityidNotEqualTo(Integer value) {
            addCriterion("activityID <>", value, "activityid");
            return (Criteria) this;
        }

        public Criteria andActivityidGreaterThan(Integer value) {
            addCriterion("activityID >", value, "activityid");
            return (Criteria) this;
        }

        public Criteria andActivityidGreaterThanOrEqualTo(Integer value) {
            addCriterion("activityID >=", value, "activityid");
            return (Criteria) this;
        }

        public Criteria andActivityidLessThan(Integer value) {
            addCriterion("activityID <", value, "activityid");
            return (Criteria) this;
        }

        public Criteria andActivityidLessThanOrEqualTo(Integer value) {
            addCriterion("activityID <=", value, "activityid");
            return (Criteria) this;
        }

        public Criteria andActivityidIn(List<Integer> values) {
            addCriterion("activityID in", values, "activityid");
            return (Criteria) this;
        }

        public Criteria andActivityidNotIn(List<Integer> values) {
            addCriterion("activityID not in", values, "activityid");
            return (Criteria) this;
        }

        public Criteria andActivityidBetween(Integer value1, Integer value2) {
            addCriterion("activityID between", value1, value2, "activityid");
            return (Criteria) this;
        }

        public Criteria andActivityidNotBetween(Integer value1, Integer value2) {
            addCriterion("activityID not between", value1, value2, "activityid");
            return (Criteria) this;
        }

        public Criteria andProducttypeidIsNull() {
            addCriterion("productTypeID is null");
            return (Criteria) this;
        }

        public Criteria andProducttypeidIsNotNull() {
            addCriterion("productTypeID is not null");
            return (Criteria) this;
        }

        public Criteria andProducttypeidEqualTo(Integer value) {
            addCriterion("productTypeID =", value, "producttypeid");
            return (Criteria) this;
        }

        public Criteria andProducttypeidNotEqualTo(Integer value) {
            addCriterion("productTypeID <>", value, "producttypeid");
            return (Criteria) this;
        }

        public Criteria andProducttypeidGreaterThan(Integer value) {
            addCriterion("productTypeID >", value, "producttypeid");
            return (Criteria) this;
        }

        public Criteria andProducttypeidGreaterThanOrEqualTo(Integer value) {
            addCriterion("productTypeID >=", value, "producttypeid");
            return (Criteria) this;
        }

        public Criteria andProducttypeidLessThan(Integer value) {
            addCriterion("productTypeID <", value, "producttypeid");
            return (Criteria) this;
        }

        public Criteria andProducttypeidLessThanOrEqualTo(Integer value) {
            addCriterion("productTypeID <=", value, "producttypeid");
            return (Criteria) this;
        }

        public Criteria andProducttypeidIn(List<Integer> values) {
            addCriterion("productTypeID in", values, "producttypeid");
            return (Criteria) this;
        }

        public Criteria andProducttypeidNotIn(List<Integer> values) {
            addCriterion("productTypeID not in", values, "producttypeid");
            return (Criteria) this;
        }

        public Criteria andProducttypeidBetween(Integer value1, Integer value2) {
            addCriterion("productTypeID between", value1, value2, "producttypeid");
            return (Criteria) this;
        }

        public Criteria andProducttypeidNotBetween(Integer value1, Integer value2) {
            addCriterion("productTypeID not between", value1, value2, "producttypeid");
            return (Criteria) this;
        }

        public Criteria andQuantityIsNull() {
            addCriterion("quantity is null");
            return (Criteria) this;
        }

        public Criteria andQuantityIsNotNull() {
            addCriterion("quantity is not null");
            return (Criteria) this;
        }

        public Criteria andQuantityEqualTo(Integer value) {
            addCriterion("quantity =", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityNotEqualTo(Integer value) {
            addCriterion("quantity <>", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityGreaterThan(Integer value) {
            addCriterion("quantity >", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("quantity >=", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityLessThan(Integer value) {
            addCriterion("quantity <", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("quantity <=", value, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityIn(List<Integer> values) {
            addCriterion("quantity in", values, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityNotIn(List<Integer> values) {
            addCriterion("quantity not in", values, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityBetween(Integer value1, Integer value2) {
            addCriterion("quantity between", value1, value2, "quantity");
            return (Criteria) this;
        }

        public Criteria andQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("quantity not between", value1, value2, "quantity");
            return (Criteria) this;
        }

        public Criteria andProbabilityIsNull() {
            addCriterion("probability is null");
            return (Criteria) this;
        }

        public Criteria andProbabilityIsNotNull() {
            addCriterion("probability is not null");
            return (Criteria) this;
        }

        public Criteria andProbabilityEqualTo(Double value) {
            addCriterion("probability =", value, "probability");
            return (Criteria) this;
        }

        public Criteria andProbabilityNotEqualTo(Double value) {
            addCriterion("probability <>", value, "probability");
            return (Criteria) this;
        }

        public Criteria andProbabilityGreaterThan(Double value) {
            addCriterion("probability >", value, "probability");
            return (Criteria) this;
        }

        public Criteria andProbabilityGreaterThanOrEqualTo(Double value) {
            addCriterion("probability >=", value, "probability");
            return (Criteria) this;
        }

        public Criteria andProbabilityLessThan(Double value) {
            addCriterion("probability <", value, "probability");
            return (Criteria) this;
        }

        public Criteria andProbabilityLessThanOrEqualTo(Double value) {
            addCriterion("probability <=", value, "probability");
            return (Criteria) this;
        }

        public Criteria andProbabilityIn(List<Double> values) {
            addCriterion("probability in", values, "probability");
            return (Criteria) this;
        }

        public Criteria andProbabilityNotIn(List<Double> values) {
            addCriterion("probability not in", values, "probability");
            return (Criteria) this;
        }

        public Criteria andProbabilityBetween(Double value1, Double value2) {
            addCriterion("probability between", value1, value2, "probability");
            return (Criteria) this;
        }

        public Criteria andProbabilityNotBetween(Double value1, Double value2) {
            addCriterion("probability not between", value1, value2, "probability");
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