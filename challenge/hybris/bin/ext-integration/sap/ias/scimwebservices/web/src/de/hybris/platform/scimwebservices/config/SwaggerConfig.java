/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.scimwebservices.config;

import static de.hybris.platform.scimwebservices.constants.ScimwebservicesConstants.AUTHORIZATION_URL;
import static de.hybris.platform.scimwebservices.constants.ScimwebservicesConstants.CLIENT_CREDENTIAL_AUTHORIZATION_NAME;
import static de.hybris.platform.scimwebservices.constants.ScimwebservicesConstants.PASSWORD_AUTHORIZATION_NAME;

import de.hybris.platform.scimwebservices.constants.ScimwebservicesConstants;
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
import springfox.documentation.service.ClientCredentialsGrant;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@Component
public class SwaggerConfig
{

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Bean
	public Docket apiDocumentation()
	{
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().paths(PathSelectors.any()).build()
				.securitySchemes(Arrays.asList(clientCredentialFlow())).securityContexts(Arrays.asList(securityContext()));
	}

	protected ApiInfo apiInfo()
	{
		return new ApiInfoBuilder().title(getPropertyValue(ScimwebservicesConstants.DOCUMENTATION_TITLE_PROPERTY))
				.description(getPropertyValue(ScimwebservicesConstants.DOCUMENTATION_DESC_PROPERTY))
				.termsOfServiceUrl(getPropertyValue(ScimwebservicesConstants.TERMS_OF_SERVICE_URL_PROPERTY))
				.license(getPropertyValue(ScimwebservicesConstants.LICENSE_PROPERTY))
				.licenseUrl(getPropertyValue(ScimwebservicesConstants.LICENSE_URL_PROPERTY))
				.version(ScimwebservicesConstants.API_VERSION).build();
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
		return new SecurityReference(CLIENT_CREDENTIAL_AUTHORIZATION_NAME, authorizationScopes);
	}

	private String getAuthorizationScope()
	{
		return configurationService.getConfiguration().getString(ScimwebservicesConstants.AUTHORIZATION_SCOPE_PROPERTY);
	}

	protected OAuth clientCredentialFlow()
	{
		final AuthorizationScope authorizationScope = new AuthorizationScope(getAuthorizationScope(), StringUtils.EMPTY);
		final ClientCredentialsGrant clientCredentialsGrant = new ClientCredentialsGrant(AUTHORIZATION_URL);
		return new OAuth(CLIENT_CREDENTIAL_AUTHORIZATION_NAME, Arrays.asList(authorizationScope),
				Arrays.asList(clientCredentialsGrant));
	}

	protected String getPropertyValue(final String propertyName)
	{
		return configurationService.getConfiguration().getString(propertyName);
	}

	protected SecurityContext oauthSecurityContext()
	{
		return SecurityContext.builder().securityReferences(oauthSecurityReferences()).forPaths(PathSelectors.any()).build();
	}

	protected List<SecurityReference> oauthSecurityReferences()
	{
		final AuthorizationScope[] authorizationScopes = {};
		return Arrays.asList(new SecurityReference(PASSWORD_AUTHORIZATION_NAME, authorizationScopes),
				new SecurityReference(CLIENT_CREDENTIAL_AUTHORIZATION_NAME, authorizationScopes));
	}

}
