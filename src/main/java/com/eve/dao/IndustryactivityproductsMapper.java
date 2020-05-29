package com.eve.dao;

import com.eve.entity.database.Industryactivityproducts;
import com.eve.entity.database.IndustryactivityproductsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IndustryactivityproductsMapper {
    long countByExample(IndustryactivityproductsExample example);

    int deleteByExample(IndustryactivityproductsExample example);

    int insert(Industryactivityproducts record);

    int insertSelective(Industryactivityproducts record);

    List<Industryactivityproducts> selectByExample(IndustryactivityproductsExample example);

    int updateByExampleSelective(@Param("record") Industryactivityproducts record, @Param("example") IndustryactivityproductsExample example);

    int updateByExample(@Param("record") Industryactivityproducts record, @Param("example") IndustryactivityproductsExample example);
}