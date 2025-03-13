package org.xllyll.magicapidemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 程序入口
 */
@SpringBootApplication
// 指定要扫描的Mapper类的包的路径
@MapperScan("org.xllyll.**.mapper")
public class MagicApiDemoApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MagicApiDemoApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MagicApiDemoApplication.class, args);
    }

}
