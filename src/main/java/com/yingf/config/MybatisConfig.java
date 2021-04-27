package com.yingf.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * @author yf-Fangjunjin
 * @version 1.0
 * @since 4/25/21 4:43 PM
 */

@Configuration
@MapperScan("com.yingf.mapper")
public class MybatisConfig {

    private final Environment env;

    @Autowired
    public MybatisConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dataSource) throws Exception {
        String mapperLocations = env.getProperty("mybatis.mapperLocations");

        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        org.apache.ibatis.session.Configuration configuration=new org.apache.ibatis.session.Configuration();
        configuration.setCallSettersOnNulls(true);
        sessionFactory.setConfiguration(configuration);
        // 设置Mybatis数据源为 dynamicDatasource
        sessionFactory.setDataSource(dataSource);
        assert mapperLocations != null : "Mybatis mapper locations 这个属性没有配置!";
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        return sessionFactory.getObject();
    }
}
