/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousingwebservices.warehousingwebservices.util;

import de.hybris.platform.basecommerce.util.SpringCustomContextLoader;
import de.hybris.platform.core.Registry;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.warehousingwebservices.constants.WarehousingwebservicesConstants;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.annotation.Resource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;


@NeedsEmbeddedServer(webExtensions = { WarehousingwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@ContextConfiguration(locations = { "classpath:/warehousingwebservices-spring-test.xml" })
public class BaseWebservicesIntegrationTest extends ServicelayerTest
{
	protected static SpringCustomContextLoader springCustomContextLoader = null;

	@Resource
	private EnumerationService enumerationService;

	public BaseWebservicesIntegrationTest()
	{
		if (springCustomContextLoader == null)
		{
			try
			{
				springCustomContextLoader = new SpringCustomContextLoader(getClass());
				springCustomContextLoader.loadApplicationContexts((GenericApplicationContext) Registry.getCoreApplicationContext());
				springCustomContextLoader
						.loadApplicationContextByConvention((GenericApplicationContext) Registry.getCoreApplicationContext());
			}
			catch (final Exception e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Retrieves a new unauthenticated {@link WsRequestBuilder} in order to build web requests
	 *
	 * @return the unauthenticated {@link WsRequestBuilder}
	 */
	protected WsRequestBuilder getWsRequestBuilder()
	{
		final JacksonJsonProvider provider = new JacksonJaxbJsonProvider()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false)
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		final DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(isoDateFormat);

		provider.setMapper(objectMapper);
		final ClientConfig config = new ClientConfig(provider);
		return new WsRequestBuilder().extensionName(WarehousingwebservicesConstants.EXTENSIONNAME).clientConfig(config);
	}

	public EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
