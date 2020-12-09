package com.imitatejd.maven.imitatejd_search_controller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Auther: DestinyStone
 * @Date: 2020/11/10 19:53
 * @Description:
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo()) //设置ui界面信息
                .select()  //选择以下的两项
                .apis(RequestHandlerSelectors.basePackage("com.imitatejd.maven.imitatejd_search_controller.controller")) //添加扫描包
                .paths(PathSelectors.any())  //过滤出来路径  any 全部
                .build();   //建造者模式
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("周晓枫", "https://www.baidu.com", "2777679537@qq.com");
        return new ApiInfoBuilder()
                .title("客户管理")
                .description("客户管理中心 API 1.0 操作文档")
                .termsOfServiceUrl("https://www.baidu.com")
                .version("1.0")
                .contact(contact)
                .build();
    }
}
