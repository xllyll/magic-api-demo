package org.ssssssss.magicapi.datasource.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * druid 配置属性
 * 
 * @author ruoyi
 */
@Configuration
public class DruidProperties
{
    @Value("${spring.datasource.druid.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.druid.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.druid.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.druid.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.druid.connectTimeout}")
    private int connectTimeout;

    @Value("${spring.datasource.druid.socketTimeout}")
    private int socketTimeout;

    @Value("${spring.datasource.druid.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.druid.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.druid.maxEvictableIdleTimeMillis}")
    private int maxEvictableIdleTimeMillis;

    @Value("${spring.datasource.druid.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.druid.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.druid.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.druid.testOnReturn}")
    private boolean testOnReturn;


    public int getInitialSize() {
        return initialSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public int getMaxEvictableIdleTimeMillis() {
        return maxEvictableIdleTimeMillis;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }
}
