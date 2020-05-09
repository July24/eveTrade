package com.eve.dao;

import com.eve.entity.database.IndustryActivitySkills;
import com.eve.entity.database.IndustryActivitySkillsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IndustryActivitySkillsMapper {
    long countByExample(IndustryActivitySkillsExample example);

    int deleteByExample(IndustryActivitySkillsExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(IndustryActivitySkills record);

    int insertSelective(IndustryActivitySkills record);

    List<IndustryActivitySkills> selectByExample(IndustryActivitySkillsExample example);

    IndustryActivitySkills selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") IndustryActivitySkills record, @Param("example") IndustryActivitySkillsExample example);

    int updateByExample(@Param("record") IndustryActivitySkills record, @Param("example") IndustryActivitySkillsExample example);

    int updateByPrimaryKeySelective(IndustryActivitySkills record);

    int updateByPrimaryKey(IndustryActivitySkills record);
}