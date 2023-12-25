package com.lmy;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * swagger 配置
 *
 * @author : mingyang.lu
 * @date : 2023/12/13 11:25
 */
@Configuration
@Controller
public class Swagger2Configuration {
    @Bean
    public Docket docket() {
        String[] contextHeaderNames = new String[]{"LMY-HEADER-USER-ID"};
        List<RequestParameter> contextParams = Arrays.stream(contextHeaderNames).map(name -> {
            RequestParameterBuilder builder = new RequestParameterBuilder();
            return builder.in(ParameterType.HEADER).name(name).build();
        }).collect(Collectors.toList());

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.lmy.controller"))
                .paths(PathSelectors.any()).build().globalRequestParameters(contextParams);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("lmy-common-test")
                .description("lmy-common 测试")
                .termsOfServiceUrl("")
                .version("1.0.0-SNAPSHOT")
                .build();
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation(value = "重定向到swagger首页")
    public void index(HttpServletResponse response) throws Exception {
        response.sendRedirect("/swagger-ui/index.html");
    }

}
