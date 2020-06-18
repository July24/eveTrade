package com.eve.dao;

import com.eve.entity.database.CorpExchange;
import com.eve.entity.database.CorpExchangeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CorpExchangeMapper {
    long countByExample(CorpExchangeExample example);

    int deleteByExample(CorpExchangeExample example);

    int deleteByPrimaryKey(@Param("corporationName") String corporationName, @Param("itemName") String itemName);

    int insert(CorpExchange record);

    int insertSelective(CorpExchange record);

    List<CorpExchange> selectByExample(CorpExchangeExample example);

    CorpExchange selectByPrimaryKey(@Param("corporationName") String corporationName, @Param("itemName") String itemName);

    int updateByExampleSelective(@Param("record") CorpExchange record, @Param("example") CorpExchangeExample example);

    int updateByExample(@Param("record") CorpExchange record, @Param("example") CorpExchangeExample example);

    int updateByPrimaryKeySelective(CorpExchange record);

    int updateByPrimaryKey(CorpExchange record);
}