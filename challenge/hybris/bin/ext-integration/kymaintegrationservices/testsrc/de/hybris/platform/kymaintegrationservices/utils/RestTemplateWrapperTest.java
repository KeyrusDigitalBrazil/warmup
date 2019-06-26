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

package de.hybris.platform.kymaintegrationservices.utils;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;


@IntegrationTest
public class RestTemplateWrapperTest extends ServicelayerTest
{
	private static final String DESTINATION_ID = "testDestination";

	@Resource(name = "kymaExportRestTemplate")
	private RestTemplate restTemplate;

	@Resource
	private DestinationService<AbstractDestinationModel> destinationService;

	private RestTemplateWrapper restTemplateWrapper;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/test/certificate.impex", "UTF-8");

		restTemplateWrapper = new RestTemplateWrapper();
		restTemplateWrapper.setRestTemplate(restTemplate);
		restTemplateWrapper.setDestinationService(destinationService);
	}

	@Test
	public void testUpdateCredentials() throws CredentialException
	{
		final AbstractDestinationModel apiDestination = destinationService.getDestinationById(DESTINATION_ID);
		restTemplateWrapper.updateCredentials(apiDestination);
		assertTrue(restTemplateWrapper.getUpdatedRestTemplate().getRequestFactory() != null);
	}
}
