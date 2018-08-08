package com.samuel.bowling.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Filter corsFilter() {
        return new CorsFilter();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.samuel.bowling."))
                .paths(PathSelectors.any())
                .build()
                .groupName("1.0")
                .apiInfo(metaData("1.0"));
    }

    private ApiInfo metaData(String version) {
        Contact contact = new Contact("Samuel Marques", "", "samueljtmarques@gmail.com");

        return new ApiInfo(
                "Bowling House API",
                "An Api to be consumed by a bowling house\n",
                version,
                "",//API terms of service

                contact,
                "",
                "", //API license URL
                Collections.emptyList()
        );
    }

    /**
     * Filter to enable Cross-Origin Resource Sharing operations in Swagger
     * @see <a href="https://github.com/swagger-api/swagger-ui/blob/master/README.md#cors-support</a>
     */
    private static class CorsFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            //No initialization needed for this filter
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

            if (servletResponse instanceof HttpServletResponse) {

                HttpServletResponse response = (HttpServletResponse) servletResponse;

                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "validation-requested-with, Content-Type, api_key, Authorization");

                filterChain.doFilter(servletRequest, response);
            }
        }

        @Override
        public void destroy() {
            //Nothing to do in this method
        }
    }

}