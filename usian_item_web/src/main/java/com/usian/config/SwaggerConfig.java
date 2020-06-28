package com.usian.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2  //开启swagger
public class SwaggerConfig {

    //生成接口文档
    @Bean
    public Docket getDocket(){
        return new Docket(DocumentationType.SWAGGER_2)  //指定版本信息
                         .apiInfo(apiInfo())
                         .select()
                         .apis(RequestHandlerSelectors.basePackage("com.usian.controller"))   //扫描那个包下的
                         .paths(PathSelectors.any())   //扫描包里的路径
                         .build();
    }

    //接口文档的描述
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("优思安商城后台管理系统")  //生成文档的标题
                .description("商品管理模块接口文档")  //文档描述
                .version("1.0")  //文档版本
                .build();

    }
}
