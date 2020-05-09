package com.eve.dao;

import com.eve.entity.database.IndustryActivities;
import com.eve.entity.database.IndustryActivitiesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IndustryActivitiesMapper {
    long countByExample(IndustryActivitiesExample example);

    int deleteByExample(IndustryActivitiesExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(IndustryActivities record);

    int insertSelective(IndustryActivities record);

    List<IndustryActivities> selectByExample(IndustryActivitiesExample example);

    IndustryActivities selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") IndustryActivities record, @Param("example") IndustryActivitiesExample example);

    int updateByExample(@Param("record") IndustryActivities record, @Param("example") IndustryActivitiesExample example);

    int updateByPrimaryKeySelective(IndustryActivities record);

    int updateByPrimaryKey(IndustryActivities record);
}