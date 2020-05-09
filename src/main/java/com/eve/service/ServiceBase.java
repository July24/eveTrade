package com.eve.service;

import com.eve.dao.*;
import com.eve.entity.database.*;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class ServiceBase {
    private static SqlSession sqlSession;

    public ServiceBase() {
        String mybatisConfigPath = "mybatisconfig.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(mybatisConfigPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession();
    }

    protected ItemsMapper getItemsMapper() {
        return sqlSession.getMapper(ItemsMapper.class);
    }

    protected IndustryActivitiesMapper getIndustryActivitiesMapper() {
        return sqlSession.getMapper(IndustryActivitiesMapper.class);
    }

    protected IndustryActivityMaterialsMapper getIndustryActivityMaterialsMapper() {
        return sqlSession.getMapper(IndustryActivityMaterialsMapper.class);
    }

    protected IndustryActivityProductsMapper getIndustryActivityProductsMapper() {
        return sqlSession.getMapper(IndustryActivityProductsMapper.class);
    }

    protected IndustryActivitySkillsMapper getIndustryActivitySkillsMapper() {
        return sqlSession.getMapper(IndustryActivitySkillsMapper.class);
    }

    protected IndustryBlueprintsMapper getIndustryBlueprintsMapper() {
        return sqlSession.getMapper(IndustryBlueprintsMapper.class);
    }
}
