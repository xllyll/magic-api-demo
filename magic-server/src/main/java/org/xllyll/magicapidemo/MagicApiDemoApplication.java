package org.xllyll.magicapidemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@MapperScan(basePackages = "org.xllyll.magicapidemo.mapper")
public class MagicApiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MagicApiDemoApplication.class, args);
    }

}
