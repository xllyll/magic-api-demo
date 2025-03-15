package org.ssssssss.magicapi.spring.boot.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.ssssssss.magicapi.core.config.MagicAPIProperties;
import org.ssssssss.magicapi.core.interceptor.DefaultResultProvider;
import org.ssssssss.magicapi.core.interceptor.ResultProvider;
import org.ssssssss.magicapi.core.servlet.MagicRequestContextHolder;
import org.ssssssss.magicapi.jsr223.JSR223LanguageProvider;
import org.ssssssss.magicapi.modules.http.HttpModule;
import org.ssssssss.magicapi.modules.servlet.RequestModule;
import org.ssssssss.magicapi.modules.servlet.ResponseModule;
import org.ssssssss.magicapi.modules.spring.EnvModule;

import java.nio.charset.StandardCharsets;
import java.util.Collections;


@AutoConfigureAfter(MagicDatasourceConfiguration.class)
public class MagicModuleConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(MagicModuleConfiguration.class);

	private final MagicAPIProperties properties;

	private final Environment environment;

	@Autowired(required = false)
	private MultipartResolver multipartResolver;

	public MagicModuleConfiguration(MagicAPIProperties properties,
									Environment environment) {
		this.properties = properties;
		this.environment = environment;
	}

	@Bean
	public JSR223LanguageProvider jsr223LanguageProvider() {
		return new JSR223LanguageProvider();
	}

	@Bean
	@ConditionalOnMissingBean(HttpModule.class)
	public HttpModule magicHttpModule() {
		return new HttpModule(createRestTemplate());
	}

	@Bean
	@ConditionalOnMissingBean
	public EnvModule magicEnvModule(){
		return new EnvModule(environment);
	}

	@Bean
	@ConditionalOnMissingBean
	public RequestModule magicRequestModule(MagicRequestContextHolder magicRequestContextHolder){
		return new RequestModule(magicRequestContextHolder);
	}

	/**
	 * 注入结果构建方法
	 */
	@Bean
	@ConditionalOnMissingBean(ResultProvider.class)
	public ResultProvider resultProvider() {
		return new DefaultResultProvider(properties.getResponse());
	}


	@Bean
	@ConditionalOnMissingBean
	public ResponseModule magicResponseModule(ResultProvider resultProvider){
		return new ResponseModule(resultProvider);
	}

	private RestTemplate createRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8) {
			{
				setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
			}

			@Override
			public boolean supports(Class<?> clazz) {
				return true;
			}
		});
		return restTemplate;
	}

}
