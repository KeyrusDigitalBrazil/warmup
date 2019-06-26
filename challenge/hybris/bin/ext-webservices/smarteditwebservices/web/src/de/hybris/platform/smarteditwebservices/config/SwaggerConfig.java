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
package de.hybris.platform.smarteditwebservices.config;

import static de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants.DOCUMENTATION_API_VERSION;
import static de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants.AUTHORIZATION_SCOPE_PROPERTY;
import static de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants.AUTHORIZATION_URL;
import static de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants.DOCUMENTATION_DESC_PROPERTY;
import static de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants.DOCUMENTATION_TITLE_PROPERTY;
import static de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants.LICENSE_PROPERTY;
import static de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants.LICENSE_URL_PROPERTY;
import static de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants.PASSWORD_AUTHORIZATION_NAME;
import static de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants.TERMS_OF_SERVICE_URL_PROPERTY;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.google.common.collect.Sets;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Collections;
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
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * Configuration for swagger api documentation
 */
@EnableSwagger2
@Component
public class SwaggerConfig
{
	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Bean
	public Docket apiDocumentation()
	{
		return new Docket(DocumentationType.SWAGGER_2)//
				.apiInfo(apiInfo())//
				.select()//
				.paths(PathSelectors.any())//
				.build()//
				.securitySchemes(Collections.singletonList(passwordSecurityScheme()))//
				.securityContexts(Collections.singletonList(oauthSecurityContext()))//
				.produces(Sets.newHashSet(APPLICATION_JSON))//
				.tags(new Tag("configurations", "Smartedit Configurations"),
						new Tag("languages", "Smartedit Languages"));
	}

	protected ApiInfo apiInfo()
	{
		return new ApiInfoBuilder()//
				.title(getPropertyValue(DOCUMENTATION_TITLE_PROPERTY))//
				.description(getPropertyValue(DOCUMENTATION_DESC_PROPERTY))//
				.termsOfServiceUrl(getPropertyValue(TERMS_OF_SERVICE_URL_PROPERTY))//
				.license(getPropertyValue(LICENSE_PROPERTY))//
				.licenseUrl(getPropertyValue(LICENSE_URL_PROPERTY))//
				.version(DOCUMENTATION_API_VERSION)//
				.build();
	}

	protected OAuth passwordSecurityScheme()
	{
		final AuthorizationScope authorizationScope = new AuthorizationScope(getPropertyValue(AUTHORIZATION_SCOPE_PROPERTY),
				StringUtils.EMPTY);
		final ResourceOwnerPasswordCredentialsGrant resourceOwnerPasswordCredentialsGrant = new ResourceOwnerPasswordCredentialsGrant(
				AUTHORIZATION_URL);
		return new OAuth(PASSWORD_AUTHORIZATION_NAME, Collections.singletonList(authorizationScope),
				Collections.singletonList(resourceOwnerPasswordCredentialsGrant));
	}

	protected String getPropertyValue(final String propertyName)
	{
		return getConfigurationService().getConfiguration().getString(propertyName);
	}

	protected SecurityContext oauthSecurityContext()
	{
		return SecurityContext.builder().securityReferences(oauthSecurityReferences()).forPaths(PathSelectors.any()).build();
	}

	protected List<SecurityReference> oauthSecurityReferences()
	{
		final AuthorizationScope[] authorizationScopes = {};
		return Collections.singletonList(new SecurityReference(PASSWORD_AUTHORIZATION_NAME, authorizationScopes));
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
