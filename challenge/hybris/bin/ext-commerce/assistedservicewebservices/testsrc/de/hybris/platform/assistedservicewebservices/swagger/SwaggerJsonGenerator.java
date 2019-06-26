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
package de.hybris.platform.assistedservicewebservices.swagger;

import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.platform.assistedservicewebservices.constants.AssistedservicewebservicesConstants;
import de.hybris.platform.licence.sap.HybrisAdminTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Required;


@NeedsEmbeddedServer(webExtensions =
{ AssistedservicewebservicesConstants.EXTENSIONNAME })
public class SwaggerJsonGenerator extends ServicelayerTest
{
	private final static Logger LOG = Logger.getLogger(SwaggerJsonGenerator.class.getName());
	private final static String SWAGGER_LOCATION = "assistedservicewebservices.swagger.location";
	private final static String SWAGGER_JSON_URL = "/v2/api-docs";
	@Resource
	private ConfigurationService configurationService;



	@Test
	public void generateSwaggerDocumentationJson()
	{
		final String content = getSwaggerJsonContent();
		saveSwaggerFile(content);
	}

	protected String getSwaggerJsonContent()
	{
		final Response result = getWsSecuredRequestBuilder().path(SWAGGER_JSON_URL).build().accept(MediaType.APPLICATION_JSON)
				.get();
		result.bufferEntity();
		return result.readEntity(String.class);
	}

	protected WsRequestBuilder getWsSecuredRequestBuilder()
	{
		return new WsRequestBuilder().extensionName(AssistedservicewebservicesConstants.EXTENSIONNAME);
	}

	private void saveSwaggerFile(final String content)
	{
		final File file = new File(getSwaggerFileLocation());
		try
		{
			FileUtils.writeStringToFile(file, content);
		}
		catch (final IOException e)
		{
			LOG.error("Couldn't save swagger file", e);
		}
	}

	private String getSwaggerFileLocation()
	{
		return ConfigUtil.getPlatformConfig(HybrisAdminTest.class).getSystemConfig().getDataDir()
				+ configurationService.getConfiguration().getString(SWAGGER_LOCATION);
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
