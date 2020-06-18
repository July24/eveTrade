package com.eve.dao;

import com.eve.entity.database.CorpExchangeMaterials;
import com.eve.entity.database.CorpExchangeMaterialsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CorpExchangeMaterialsMapper {
    long countByExample(CorpExchangeMaterialsExample example);

    int deleteByExample(CorpExchangeMaterialsExample example);

    int deleteByPrimaryKey(@Param("corporationName") String corporationName, @Param("itemName") String itemName, @Param("materialsName") String materialsName);

    int insert(CorpExchangeMaterials record);

    int insertSelective(CorpExchangeMaterials record);

    List<CorpExchangeMaterials> selectByExample(CorpExchangeMaterialsExample example);

    CorpExchangeMaterials selectByPrimaryKey(@Param("corporationName") String corporationName, @Param("itemName") String itemName, @Param("materialsName") String materialsName);

    int updateByExampleSelective(@Param("record") CorpExchangeMaterials record, @Param("example") CorpExchangeMaterialsExample example);

    int updateByExample(@Param("record") CorpExchangeMaterials record, @Param("example") CorpExchangeMaterialsExample example);

    int updateByPrimaryKeySelective(CorpExchangeMaterials record);

    int updateByPrimaryKey(CorpExchangeMaterials record);
}