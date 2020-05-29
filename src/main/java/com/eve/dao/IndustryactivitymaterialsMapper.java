package com.eve.dao;

import com.eve.entity.database.Industryactivitymaterials;
import com.eve.entity.database.IndustryactivitymaterialsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IndustryactivitymaterialsMapper {
    long countByExample(IndustryactivitymaterialsExample example);

    int deleteByExample(IndustryactivitymaterialsExample example);

    int insert(Industryactivitymaterials record);

    int insertSelective(Industryactivitymaterials record);

    List<Industryactivitymaterials> selectByExample(IndustryactivitymaterialsExample example);

    int updateByExampleSelective(@Param("record") Industryactivitymaterials record, @Param("example") IndustryactivitymaterialsExample example);

    int updateByExample(@Param("record") Industryactivitymaterials record, @Param("example") IndustryactivitymaterialsExample example);
}