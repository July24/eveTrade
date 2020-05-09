package com.eve.dao;

import com.eve.entity.database.IndustryActivityMaterials;
import com.eve.entity.database.IndustryActivityMaterialsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IndustryActivityMaterialsMapper {
    long countByExample(IndustryActivityMaterialsExample example);

    int deleteByExample(IndustryActivityMaterialsExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(IndustryActivityMaterials record);

    int insertSelective(IndustryActivityMaterials record);

    List<IndustryActivityMaterials> selectByExample(IndustryActivityMaterialsExample example);

    IndustryActivityMaterials selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") IndustryActivityMaterials record, @Param("example") IndustryActivityMaterialsExample example);

    int updateByExample(@Param("record") IndustryActivityMaterials record, @Param("example") IndustryActivityMaterialsExample example);

    int updateByPrimaryKeySelective(IndustryActivityMaterials record);

    int updateByPrimaryKey(IndustryActivityMaterials record);
}