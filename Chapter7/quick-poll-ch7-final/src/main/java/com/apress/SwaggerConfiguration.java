package com.apress;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import java.util.Collections;


@Configuration
public class SwaggerConfiguration {

	@Bean
	public Docket apiV1() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.regex("/v1/*.*"))
				.build()
				.apiInfo(apiInfo("v1"))
				.groupName("v1")
				.useDefaultResponseMessages(false);
	}

	@Bean
	public Docket apiV2() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.regex("/v2/*.*"))
				.build()
				.apiInfo(apiInfo("v2"))
				.groupName("v2")
				.useDefaultResponseMessages(false);
	}

	private ApiInfo apiInfo(String version) {
		return new ApiInfo(
				"QuickPoll REST API",
				"QuickPoll Api for creating and managing polls",
				version,
				"Terms of service",
				new Contact("Maxim Bartkov", "www.linkedin.com/in/bartkov-maxim", "maxgalayoutop@gmail.com"),
				"MIT License", "http://opensource.org/licenses/MIT", Collections.emptyList());
	}

}
