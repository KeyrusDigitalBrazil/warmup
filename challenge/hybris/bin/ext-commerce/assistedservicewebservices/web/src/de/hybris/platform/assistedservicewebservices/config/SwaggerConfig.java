/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.assistedservicewebservices.config;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@Component
public class SwaggerConfig
{
	private static final String AUTHORIZATION_SCOPE = "assistedservicewebservices.oaut2.scope";
	private static final String LICENSE_URL = "https://github.com/springfox/springfox/blob/master/LICENSE";
	private static final String TERMS_OF_SERVICE_URL = "http://springfox.io";
	private static final String LICENSE = "Apache License Version 2.0";
	private static final String DESC = "ASM Webservices";
	private static final String TITLE = "Assisted Service Module";
	private static final String OAUTH2_SCHEMA = "oauth2";
	private static final String AUTHORIZATION_URL = "/authorizationserver/oauth/token";
	private static final String VERSION = "1.0.0";

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Bean
	public Docket apiDocumentation()
	{
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().paths(PathSelectors.any()).build()
				.securitySchemes(Arrays.asList(securitySchema())).securityContexts(Arrays.asList(securityContext()));
	}

	private ApiInfo apiInfo()
	{
		return new ApiInfoBuilder().title(TITLE).description(DESC).termsOfServiceUrl(TERMS_OF_SERVICE_URL).license(LICENSE)
				.licenseUrl(LICENSE_URL).version(VERSION).build();
	}

	private OAuth securitySchema()
	{
		final AuthorizationScope authorizationScope = new AuthorizationScope(getAuthorizationScope(), StringUtils.EMPTY);
		final ResourceOwnerPasswordCredentialsGrant clientCredentialsGrant = new ResourceOwnerPasswordCredentialsGrant(
				AUTHORIZATION_URL);
		return new OAuth(OAUTH2_SCHEMA, Arrays.asList(authorizationScope), Arrays.asList(clientCredentialsGrant));
	}

	private String getAuthorizationScope()
	{
		return getConfigurationService().getConfiguration().getString(AUTHORIZATION_SCOPE);
	}

	private SecurityContext securityContext()
	{
		return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build();
	}

	private List<SecurityReference> defaultAuth()
	{
		return Arrays.asList(getSecurityReference());
	}

	private SecurityReference getSecurityReference()
	{
		final AuthorizationScope[] authorizationScopes =
		{ new AuthorizationScope(getAuthorizationScope(), StringUtils.EMPTY) };
		return new SecurityReference(OAUTH2_SCHEMA, authorizationScopes);
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	protected void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}