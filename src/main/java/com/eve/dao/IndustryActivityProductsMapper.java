package com.eve.dao;

import com.eve.entity.database.IndustryActivityProducts;
import com.eve.entity.database.IndustryActivityProductsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IndustryActivityProductsMapper {
    long countByExample(IndustryActivityProductsExample example);

    int deleteByExample(IndustryActivityProductsExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(IndustryActivityProducts record);

    int insertSelective(IndustryActivityProducts record);

    List<IndustryActivityProducts> selectByExample(IndustryActivityProductsExample example);

    IndustryActivityProducts selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") IndustryActivityProducts record, @Param("example") IndustryActivityProductsExample example);

    int updateByExample(@Param("record") IndustryActivityProducts record, @Param("example") IndustryActivityProductsExample example);

    int updateByPrimaryKeySelective(IndustryActivityProducts record);

    int updateByPrimaryKey(IndustryActivityProducts record);
}