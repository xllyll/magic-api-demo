package org.ssssssss.magicapi.spring.boot.starter;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.ssssssss.magicapi.core.config.Cache;
import org.ssssssss.magicapi.core.config.MagicAPIProperties;
import org.ssssssss.magicapi.core.config.Page;
import org.ssssssss.magicapi.core.interceptor.DefaultResultProvider;
import org.ssssssss.magicapi.core.interceptor.ResultProvider;
import org.ssssssss.magicapi.core.servlet.MagicRequestContextHolder;
import org.ssssssss.magicapi.datasource.model.MagicDynamicDataSource;
import org.ssssssss.magicapi.jsr223.JSR223LanguageProvider;
import org.ssssssss.magicapi.modules.db.ColumnMapperAdapter;
import org.ssssssss.magicapi.modules.db.SQLModule;
import org.ssssssss.magicapi.modules.db.cache.DefaultSqlCache;
import org.ssssssss.magicapi.modules.db.cache.SqlCache;
import org.ssssssss.magicapi.modules.db.dialect.Dialect;
import org.ssssssss.magicapi.modules.db.dialect.DialectAdapter;
import org.ssssssss.magicapi.modules.db.inteceptor.DefaultSqlInterceptor;
import org.ssssssss.magicapi.modules.db.inteceptor.NamedTableInterceptor;
import org.ssssssss.magicapi.modules.db.inteceptor.SQLInterceptor;
import org.ssssssss.magicapi.modules.db.provider.ColumnMapperProvider;
import org.ssssssss.magicapi.modules.db.provider.DefaultPageProvider;
import org.ssssssss.magicapi.modules.db.provider.PageProvider;
import org.ssssssss.magicapi.modules.http.HttpModule;
import org.ssssssss.magicapi.modules.servlet.RequestModule;
import org.ssssssss.magicapi.modules.servlet.ResponseModule;
import org.ssssssss.magicapi.modules.spring.EnvModule;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Configuration
@EnableConfigurationProperties(MagicAPIProperties.class)
public class MagicDatasourceConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MagicDatasourceConfiguration.class);

	private final MagicAPIProperties properties;

	/**
	 * SQL拦截器
	 */
	private final ObjectProvider<List<SQLInterceptor>> sqlInterceptorsProvider;

	/**
	 * 单表API拦截器
	 */
	private final ObjectProvider<List<NamedTableInterceptor>> namedTableInterceptorsProvider;

	/**
	 * 自定义的方言
	 */
	private final ObjectProvider<List<Dialect>> dialectsProvider;

	/**
	 * 自定义的列名转换
	 */
	private final ObjectProvider<List<ColumnMapperProvider>> columnMapperProvidersProvider;


	@Autowired(required = false)
	private MultipartResolver multipartResolver;

	public MagicDatasourceConfiguration(MagicAPIProperties properties,
                                        ObjectProvider<List<SQLInterceptor>> sqlInterceptorsProvider,
                                        ObjectProvider<List<NamedTableInterceptor>> namedTableInterceptorsProvider,
                                        ObjectProvider<List<Dialect>> dialectsProvider,
                                        ObjectProvider<List<ColumnMapperProvider>> columnMapperProvidersProvider) {
		this.properties = properties;
		this.sqlInterceptorsProvider = sqlInterceptorsProvider;
		this.namedTableInterceptorsProvider = namedTableInterceptorsProvider;
		this.dialectsProvider = dialectsProvider;
		this.columnMapperProvidersProvider = columnMapperProvidersProvider;
	}

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;


//	@Bean
//	public DataSource masterDataSource()
//	{
//		DataSource datasource = createDataSource(url,username,password);
//		return datasource;
//	}

	private DataSource createDataSource(String url, String username, String password) {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}
	/**
	 * 注入动态数据源
	 */
	@Bean(name = "dynamicDataSource")
	@Primary
	@ConditionalOnMissingBean(MagicDynamicDataSource.class)
	public MagicDynamicDataSource magicDynamicDataSource() {
		DataSource dataSource = createDataSource(url,username,password);
		MagicDynamicDataSource dynamicDataSource = new MagicDynamicDataSource(dataSource);
		if (dataSource != null) {
			dynamicDataSource.put(dataSource);
		} else {
			logger.warn("当前数据源未配置");
		}
		return dynamicDataSource;
	}

	@Bean
	@ConditionalOnMissingBean(PageProvider.class)
	public PageProvider pageProvider() {
		Page pageConfig = properties.getPage();
		logger.info("未找到分页实现,采用默认分页实现,分页配置:(页码={},页大小={},默认首页={},默认页大小={},最大页大小={})", pageConfig.getPage(), pageConfig.getSize(), pageConfig.getDefaultPage(), pageConfig.getDefaultSize(), pageConfig.getMaxPageSize());
		return new DefaultPageProvider(pageConfig.getPage(), pageConfig.getSize(), pageConfig.getDefaultPage(), pageConfig.getDefaultSize(), pageConfig.getMaxPageSize());
	}

	/**
	 * 注入SQL缓存实现
	 */
	@Bean
	@ConditionalOnMissingBean(SqlCache.class)
	public SqlCache sqlCache() {
		Cache cacheConfig = properties.getCache();
		logger.info("未找到SQL缓存实现，采用默认缓存实现(LRU+TTL)，缓存配置:(容量={},TTL={})", cacheConfig.getCapacity(), cacheConfig.getTtl());
		return new DefaultSqlCache(cacheConfig.getCapacity(), cacheConfig.getTtl());
	}

	/**
	 * 注入数据库查询模块
	 */
	@Bean
	@ConditionalOnBean({MagicDynamicDataSource.class})
	public SQLModule magicSqlModule(MagicDynamicDataSource dynamicDataSource,
									ResultProvider resultProvider,
									PageProvider pageProvider,
									SqlCache sqlCache) {
		SQLModule sqlModule = new SQLModule(dynamicDataSource);
		if (!dynamicDataSource.isEmpty()) {
			sqlModule.setDataSourceNode(dynamicDataSource.getDataSource());
		}
		sqlModule.setResultProvider(resultProvider);
		sqlModule.setPageProvider(pageProvider);
		List<SQLInterceptor> sqlInterceptors = sqlInterceptorsProvider.getIfAvailable(ArrayList::new);
		if (properties.isShowSql()) {
			sqlInterceptors.add(new DefaultSqlInterceptor());
		}
		sqlModule.setSqlInterceptors(sqlInterceptors);
		sqlModule.setNamedTableInterceptors(namedTableInterceptorsProvider.getIfAvailable(Collections::emptyList));
		ColumnMapperAdapter columnMapperAdapter = new ColumnMapperAdapter();
		this.columnMapperProvidersProvider.getIfAvailable(Collections::emptyList).stream().filter(mapperProvider -> !"default".equals(mapperProvider.name())).forEach(columnMapperAdapter::add);
		columnMapperAdapter.setDefault(properties.getSqlColumnCase());
		sqlModule.setColumnMapperProvider(columnMapperAdapter);
		sqlModule.setColumnMapRowMapper(columnMapperAdapter.getDefaultColumnMapRowMapper());
		sqlModule.setRowMapColumnMapper(columnMapperAdapter.getDefaultRowMapColumnMapper());
		sqlModule.setSqlCache(sqlCache);
		DialectAdapter dialectAdapter = new DialectAdapter();
		dialectsProvider.getIfAvailable(Collections::emptyList).forEach(dialectAdapter::add);
		sqlModule.setDialectAdapter(dialectAdapter);
		sqlModule.setLogicDeleteColumn(properties.getCrud().getLogicDeleteColumn());
		sqlModule.setLogicDeleteValue(properties.getCrud().getLogicDeleteValue());
		return sqlModule;
	}



}
