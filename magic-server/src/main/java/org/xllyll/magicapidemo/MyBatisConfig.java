package org.xllyll.magicapidemo;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.datasource.model.MagicDynamicDataSource;

import javax.sql.DataSource;

@Component
public class MyBatisConfig {

    private final Logger logger = LoggerFactory.getLogger(MyBatisConfig.class);

//    @Bean
//    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
//        if (dataSource instanceof MagicDynamicDataSource) {
//            logger.info("Dynamic data source is correctly injected.");
//        } else {
//            logger.warn("Injected data source is not a dynamic data source: {}", dataSource.getClass().getName());
//        }
//        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
//        sessionFactory.setDataSource(dataSource); // 动态数据源
//        return sessionFactory.getObject();
//    }

}
