package org.xllyll.magicapidemo;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusPropertiesCustomizer mybatisPlusPropertiesCustomizer() {
        return properties -> {
            // 配置 typeAliasesPackage
            properties.setTypeAliasesPackage("org.xllyll.magicapidemo.model");
            // 配置 mapperLocations
            properties.setMapperLocations(new String[]{"classpath:mapper/*.xml"});
        };
    }
}
