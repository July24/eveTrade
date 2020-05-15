package com.eve.dao;

import com.eve.entity.database.Marketgroups;
import com.eve.entity.database.MarketgroupsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MarketgroupsMapper {
    long countByExample(MarketgroupsExample example);

    int deleteByExample(MarketgroupsExample example);

    int deleteByPrimaryKey(Integer marketgroupid);

    int insert(Marketgroups record);

    int insertSelective(Marketgroups record);

    List<Marketgroups> selectByExample(MarketgroupsExample example);

    Marketgroups selectByPrimaryKey(Integer marketgroupid);

    int updateByExampleSelective(@Param("record") Marketgroups record, @Param("example") MarketgroupsExample example);

    int updateByExample(@Param("record") Marketgroups record, @Param("example") MarketgroupsExample example);

    int updateByPrimaryKeySelective(Marketgroups record);

    int updateByPrimaryKey(Marketgroups record);
}