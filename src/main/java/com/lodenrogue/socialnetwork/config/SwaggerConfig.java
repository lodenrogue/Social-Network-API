package com.lodenrogue.socialnetwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {
		//@formatter:off
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.regex("/api/.*"))
				.build()
				.apiInfo(apiInfo());
		//@formatter:on
	}

	private ApiInfo apiInfo() {
		//@formatter:off
		  ApiInfo apiInfo = new ApiInfo(
				  "Social Network API", 
				  "RESTful API for Social Network Back-end", 
				  "1.0.0", 
				  "", 
				  "http://github.com/lodenrogue", 
				  "", 
				  "");
		  //@formatter:on
		return apiInfo;
	}

}
