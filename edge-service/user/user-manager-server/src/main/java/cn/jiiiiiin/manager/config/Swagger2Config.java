package cn.jiiiiiin.manager.config;

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
 * 解决：Unable to infer base url. This is common when using dynamic servlet registration or when the API is behind an API Gateway. The base url is the root of where all the swagger resources are served. For e.g. if the api is available at http://example.org/api/v2/api-docs then the base url is http://example.org/api/. Please enter the location manually:
 * https://zhaoxuyang.com/springboot%E4%BD%BF%E7%94%A8swagger2%E5%87%BA%E7%8E%B0unable-to-infer-base-url-this-is-common-when-using-dynamic-servlet-registration-or-when-the-api-is-behind-an-api-gateway/
 * <p>
 * https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
 *
 * @author jiiiiiin
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    /**
     * 解决：Unable to infer base url. This is common when using dynamic servlet registration or when the API is behind an API Gateway. The base url is the root of where all the swagger resources are served. For e.g. if the api is available at http://example.org/api/v2/api-docs then the base url is http://example.org/api/. Please enter the location manually:
     * https://github.com/springfox/springfox/issues/1996
     */
    public static final String[] AUTH_WHITELIST = {
            // -- swagger ui
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/webjars/**"
    };


    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.jiiiiiin.module"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("jiiiiiin-security APIs")
                .description("一个前后端分离的内管基础项目")
                .termsOfServiceUrl("https://github.com/Jiiiiiin/")
                .contact(new Contact("jiiiiiin", "https://github.com/Jiiiiiin/jiiiiiin-security", "15398699939@163.com"))
                .version("1.0")
                .build();
    }
}
