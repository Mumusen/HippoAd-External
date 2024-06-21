package com.transmartx.hippo.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @apior: Kosne
 */
@Configuration
@Slf4j
@MapperScan(basePackages = "com.transmartx.hippo.mapper")
public class ApiDruidDataSource {

    @Value("${spring.datasource.api.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.api.url}")
    private String url;

    @Value("${spring.datasource.api.username}")
    private String username;

    @Value("${spring.datasource.api.password}")
    private String password;

    @Value("${spring.datasource.api.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.api.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.api.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.api.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.api.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.api.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.api.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.api.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.api.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.api.testOnReturn}")
    private boolean testOnReturn;

    @Value("${spring.datasource.api.poolPreparedStatements}")
    private boolean poolPreparedStatements;

    @Value("${spring.datasource.api.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize;

    @Value("${spring.datasource.api.filters}")
    private String filters;

    @Value("${spring.datasource.api.useGlobalDataSourceStat}")
    private boolean useGlobalDataSourceStat;

    @Value("${spring.datasource.api.connectionProperties}")
    private String connectionProperties;

    @Bean(name = "apiDataSource")
    @Qualifier("apiDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.api")
    public DataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();

        datasource.setDriverClassName(driverClassName);
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);

        // configuration
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            datasource.setFilters(filters);
        } catch (SQLException e) {
            log.error("datasource.setFilters error", e);
        }
        datasource.setUseGlobalDataSourceStat(useGlobalDataSourceStat);
        datasource.setConnectionProperties(connectionProperties);

        return datasource;
    }

    // mybatis-plus?
    @Bean(name = "sqlSessionFactoryBean")
    @Qualifier("sqlSessionFactoryBean")
    public SqlSessionFactoryBean sqlSessionFactoryBean() {
        try {
            SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
            sessionFactoryBean.setDataSource(dataSource());
            sessionFactoryBean.setTypeAliasesPackage("com.transmartx.hippo.dto");
            sessionFactoryBean.setConfigLocation(new ClassPathResource("sqlmap/mybatis-config.xml"));
            return sessionFactoryBean;
        } catch (Exception e) {
            log.error("初始化mybatis 异常: ", e);
            return null;
        }
    }

    @Bean(name = "apiJdbcTemplate")
    @Qualifier("apiJdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }




}
