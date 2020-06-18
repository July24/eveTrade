package com.eve.entity.database;

import java.util.ArrayList;
import java.util.List;

public class CorpExchangeMaterialsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public CorpExchangeMaterialsExample() {
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

        public Criteria andCorporationNameIsNull() {
            addCriterion("corporation_name is null");
            return (Criteria) this;
        }

        public Criteria andCorporationNameIsNotNull() {
            addCriterion("corporation_name is not null");
            return (Criteria) this;
        }

        public Criteria andCorporationNameEqualTo(String value) {
            addCriterion("corporation_name =", value, "corporationName");
            return (Criteria) this;
        }

        public Criteria andCorporationNameNotEqualTo(String value) {
            addCriterion("corporation_name <>", value, "corporationName");
            return (Criteria) this;
        }

        public Criteria andCorporationNameGreaterThan(String value) {
            addCriterion("corporation_name >", value, "corporationName");
            return (Criteria) this;
        }

        public Criteria andCorporationNameGreaterThanOrEqualTo(String value) {
            addCriterion("corporation_name >=", value, "corporationName");
            return (Criteria) this;
        }

        public Criteria andCorporationNameLessThan(String value) {
            addCriterion("corporation_name <", value, "corporationName");
            return (Criteria) this;
        }

        public Criteria andCorporationNameLessThanOrEqualTo(String value) {
            addCriterion("corporation_name <=", value, "corporationName");
            return (Criteria) this;
        }

        public Criteria andCorporationNameLike(String value) {
            addCriterion("corporation_name like", value, "corporationName");
            return (Criteria) this;
        }

        public Criteria andCorporationNameNotLike(String value) {
            addCriterion("corporation_name not like", value, "corporationName");
            return (Criteria) this;
        }

        public Criteria andCorporationNameIn(List<String> values) {
            addCriterion("corporation_name in", values, "corporationName");
            return (Criteria) this;
        }

        public Criteria andCorporationNameNotIn(List<String> values) {
            addCriterion("corporation_name not in", values, "corporationName");
            return (Criteria) this;
        }

        public Criteria andCorporationNameBetween(String value1, String value2) {
            addCriterion("corporation_name between", value1, value2, "corporationName");
            return (Criteria) this;
        }

        public Criteria andCorporationNameNotBetween(String value1, String value2) {
            addCriterion("corporation_name not between", value1, value2, "corporationName");
            return (Criteria) this;
        }

        public Criteria andItemNameIsNull() {
            addCriterion("item_name is null");
            return (Criteria) this;
        }

        public Criteria andItemNameIsNotNull() {
            addCriterion("item_name is not null");
            return (Criteria) this;
        }

        public Criteria andItemNameEqualTo(String value) {
            addCriterion("item_name =", value, "itemName");
            return (Criteria) this;
        }

        public Criteria andItemNameNotEqualTo(String value) {
            addCriterion("item_name <>", value, "itemName");
            return (Criteria) this;
        }

        public Criteria andItemNameGreaterThan(String value) {
            addCriterion("item_name >", value, "itemName");
            return (Criteria) this;
        }

        public Criteria andItemNameGreaterThanOrEqualTo(String value) {
            addCriterion("item_name >=", value, "itemName");
            return (Criteria) this;
        }

        public Criteria andItemNameLessThan(String value) {
            addCriterion("item_name <", value, "itemName");
            return (Criteria) this;
        }

        public Criteria andItemNameLessThanOrEqualTo(String value) {
            addCriterion("item_name <=", value, "itemName");
            return (Criteria) this;
        }

        public Criteria andItemNameLike(String value) {
            addCriterion("item_name like", value, "itemName");
            return (Criteria) this;
        }

        public Criteria andItemNameNotLike(String value) {
            addCriterion("item_name not like", value, "itemName");
            return (Criteria) this;
        }

        public Criteria andItemNameIn(List<String> values) {
            addCriterion("item_name in", values, "itemName");
            return (Criteria) this;
        }

        public Criteria andItemNameNotIn(List<String> values) {
            addCriterion("item_name not in", values, "itemName");
            return (Criteria) this;
        }

        public Criteria andItemNameBetween(String value1, String value2) {
            addCriterion("item_name between", value1, value2, "itemName");
            return (Criteria) this;
        }

        public Criteria andItemNameNotBetween(String value1, String value2) {
            addCriterion("item_name not between", value1, value2, "itemName");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameIsNull() {
            addCriterion("materials_name is null");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameIsNotNull() {
            addCriterion("materials_name is not null");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameEqualTo(String value) {
            addCriterion("materials_name =", value, "materialsName");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameNotEqualTo(String value) {
            addCriterion("materials_name <>", value, "materialsName");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameGreaterThan(String value) {
            addCriterion("materials_name >", value, "materialsName");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameGreaterThanOrEqualTo(String value) {
            addCriterion("materials_name >=", value, "materialsName");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameLessThan(String value) {
            addCriterion("materials_name <", value, "materialsName");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameLessThanOrEqualTo(String value) {
            addCriterion("materials_name <=", value, "materialsName");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameLike(String value) {
            addCriterion("materials_name like", value, "materialsName");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameNotLike(String value) {
            addCriterion("materials_name not like", value, "materialsName");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameIn(List<String> values) {
            addCriterion("materials_name in", values, "materialsName");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameNotIn(List<String> values) {
            addCriterion("materials_name not in", values, "materialsName");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameBetween(String value1, String value2) {
            addCriterion("materials_name between", value1, value2, "materialsName");
            return (Criteria) this;
        }

        public Criteria andMaterialsNameNotBetween(String value1, String value2) {
            addCriterion("materials_name not between", value1, value2, "materialsName");
            return (Criteria) this;
        }

        public Criteria andMaterialsQuantityIsNull() {
            addCriterion("materials_quantity is null");
            return (Criteria) this;
        }

        public Criteria andMaterialsQuantityIsNotNull() {
            addCriterion("materials_quantity is not null");
            return (Criteria) this;
        }

        public Criteria andMaterialsQuantityEqualTo(Integer value) {
            addCriterion("materials_quantity =", value, "materialsQuantity");
            return (Criteria) this;
        }

        public Criteria andMaterialsQuantityNotEqualTo(Integer value) {
            addCriterion("materials_quantity <>", value, "materialsQuantity");
            return (Criteria) this;
        }

        public Criteria andMaterialsQuantityGreaterThan(Integer value) {
            addCriterion("materials_quantity >", value, "materialsQuantity");
            return (Criteria) this;
        }

        public Criteria andMaterialsQuantityGreaterThanOrEqualTo(Integer value) {
            addCriterion("materials_quantity >=", value, "materialsQuantity");
            return (Criteria) this;
        }

        public Criteria andMaterialsQuantityLessThan(Integer value) {
            addCriterion("materials_quantity <", value, "materialsQuantity");
            return (Criteria) this;
        }

        public Criteria andMaterialsQuantityLessThanOrEqualTo(Integer value) {
            addCriterion("materials_quantity <=", value, "materialsQuantity");
            return (Criteria) this;
        }

        public Criteria andMaterialsQuantityIn(List<Integer> values) {
            addCriterion("materials_quantity in", values, "materialsQuantity");
            return (Criteria) this;
        }

        public Criteria andMaterialsQuantityNotIn(List<Integer> values) {
            addCriterion("materials_quantity not in", values, "materialsQuantity");
            return (Criteria) this;
        }

        public Criteria andMaterialsQuantityBetween(Integer value1, Integer value2) {
            addCriterion("materials_quantity between", value1, value2, "materialsQuantity");
            return (Criteria) this;
        }

        public Criteria andMaterialsQuantityNotBetween(Integer value1, Integer value2) {
            addCriterion("materials_quantity not between", value1, value2, "materialsQuantity");
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