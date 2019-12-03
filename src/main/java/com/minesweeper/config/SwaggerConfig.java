package com.minesweeper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket requiredFieldsApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.minesweeper"))
				.build()
				.useDefaultResponseMessages(false)
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("Minesweeper REST API")
				.description("Microservice that handles the Server required to play the classic game Minesweeper.")
				.contact(new Contact("Gaston", "Tulipani", "gtulipani@hotmail.com"))
				.license("MIT")
				.licenseUrl("https://github.com/gtulipani/minesweeper/blob/master/LICENSE")
				.version("1.0.0")
				.build();
	}

}
