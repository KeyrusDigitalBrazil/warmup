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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.client.ConfigurationClient;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ServiceVersionProviderImplTest
{
	private static final String SERVICE_URL = "https://url/v2";

	private ServiceVersionProviderImpl classUnderTest;

	@Mock
	private YaasConfigurationService yaasConfigurationServiceMock;

	private YaasServiceModel serviceModel;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ServiceVersionProviderImpl();
		classUnderTest.setYaasConfigurationService(yaasConfigurationServiceMock);

		serviceModel = new YaasServiceModel();
		serviceModel.setServiceURL(SERVICE_URL);

		given(yaasConfigurationServiceMock.getYaasServiceForId(ConfigurationClient.class.getSimpleName())).willReturn(serviceModel);
	}


	@Test
	public void testGetVersion()
	{
		final String version = classUnderTest.getVersion(ConfigurationClient.class.getSimpleName());
		assertEquals("v2", version);
	}
}
