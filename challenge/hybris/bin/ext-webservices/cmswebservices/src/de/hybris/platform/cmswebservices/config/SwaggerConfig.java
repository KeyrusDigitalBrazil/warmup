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
package de.hybris.platform.cmswebservices.config;

import com.google.common.collect.Sets;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@EnableSwagger2
@Component
public class SwaggerConfig
{
	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	public static final String UNAUTHORIZED_MESSAGE = "Must be authenticated as an Admin or CMS Manager to access this resource";

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
				.produces(Sets.newHashSet(APPLICATION_JSON))
				.globalResponseMessage(RequestMethod.GET, globalGETResponseMessages())
				.globalResponseMessage(RequestMethod.POST, globalPOSTResponseMessages())
				.globalResponseMessage(RequestMethod.PUT, globalPUTResponseMessages())
				.globalResponseMessage(RequestMethod.DELETE, globalDETELEResponseMessages())
				.enableUrlTemplating(true)
				.useDefaultResponseMessages(false);
	}

	protected ApiInfo apiInfo()
	{
		return new ApiInfoBuilder()//
				.title(getPropertyValue(DOCUMENTATION_TITLE_PROPERTY))
				.description(getPropertyValue(DOCUMENTATION_DESC_PROPERTY))
				.termsOfServiceUrl(getPropertyValue(TERMS_OF_SERVICE_URL_PROPERTY))
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

	protected ResponseMessage unauthorizedResponseMessage()
	{
		return new ResponseMessageBuilder()
				.code(HttpStatus.UNAUTHORIZED.value())
				.message(UNAUTHORIZED_MESSAGE)
				.build();
	}

	protected ResponseMessage genericMessage(HttpStatus httpStatus)
	{
		return new ResponseMessageBuilder()
				.code(httpStatus.value())
				.message(httpStatus.getReasonPhrase())
				.build();
	}

	protected List<ResponseMessage> globalGETResponseMessages()
	{
		return Lists.newArrayList(
				unauthorizedResponseMessage(),
				genericMessage(HttpStatus.OK)
		);
	}

	protected List<ResponseMessage> globalPUTResponseMessages()
	{
		return Lists.newArrayList(
				unauthorizedResponseMessage(),
				genericMessage(HttpStatus.OK));
	}

	protected List<ResponseMessage> globalPOSTResponseMessages()
	{
		return Lists.newArrayList(
				unauthorizedResponseMessage()
		);
	}

	protected List<ResponseMessage> globalDETELEResponseMessages()
	{
		return Lists.newArrayList(unauthorizedResponseMessage(),
				genericMessage(HttpStatus.NO_CONTENT));
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

	protected String getPropertyValue(final String propertyName)
	{
		return getConfigurationService().getConfiguration().getString(propertyName);
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
