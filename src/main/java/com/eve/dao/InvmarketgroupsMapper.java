package com.eve.dao;

import com.eve.entity.database.Invmarketgroups;
import com.eve.entity.database.InvmarketgroupsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface InvmarketgroupsMapper {
    long countByExample(InvmarketgroupsExample example);

    int deleteByExample(InvmarketgroupsExample example);

    int deleteByPrimaryKey(Integer marketgroupid);

    int insert(Invmarketgroups record);

    int insertSelective(Invmarketgroups record);

    List<Invmarketgroups> selectByExample(InvmarketgroupsExample example);

    Invmarketgroups selectByPrimaryKey(Integer marketgroupid);

    int updateByExampleSelective(@Param("record") Invmarketgroups record, @Param("example") InvmarketgroupsExample example);

    int updateByExample(@Param("record") Invmarketgroups record, @Param("example") InvmarketgroupsExample example);

    int updateByPrimaryKeySelective(Invmarketgroups record);

    int updateByPrimaryKey(Invmarketgroups record);
}