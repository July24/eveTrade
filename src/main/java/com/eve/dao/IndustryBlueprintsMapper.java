package com.eve.dao;

import com.eve.entity.database.IndustryBlueprints;
import com.eve.entity.database.IndustryBlueprintsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IndustryBlueprintsMapper {
    long countByExample(IndustryBlueprintsExample example);

    int deleteByExample(IndustryBlueprintsExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(IndustryBlueprints record);

    int insertSelective(IndustryBlueprints record);

    List<IndustryBlueprints> selectByExample(IndustryBlueprintsExample example);

    IndustryBlueprints selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") IndustryBlueprints record, @Param("example") IndustryBlueprintsExample example);

    int updateByExample(@Param("record") IndustryBlueprints record, @Param("example") IndustryBlueprintsExample example);

    int updateByPrimaryKeySelective(IndustryBlueprints record);

    int updateByPrimaryKey(IndustryBlueprints record);
}