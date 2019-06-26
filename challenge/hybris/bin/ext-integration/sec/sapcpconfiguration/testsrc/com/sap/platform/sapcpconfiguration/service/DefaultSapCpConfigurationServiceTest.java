/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.platform.sapcpconfiguration.service;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.platform.enums.ServiceClient;
import com.sap.platform.model.BaseSiteCecServiceMappingModel;
import com.sap.platform.model.CecServiceModel;
import com.sap.platform.model.CecTechnicalUserModel;
import com.sap.platform.sapcpconfiguration.service.impl.DefaultSapCpConfigurationService;

import reactor.util.Assert;

@UnitTest
public class DefaultSapCpConfigurationServiceTest
{

	@InjectMocks
	private DefaultSapCpConfigurationService classUnderTest;

	@Mock
	private ModelService modelService;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	private CecServiceModel cecServiceModel;

	private CecTechnicalUserModel cecTechnicalUser;

	private final Map<String, String> config = null;



	public ModelService getModelService()
	{
		return modelService;
	}


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		cecTechnicalUser = new CecTechnicalUserModel();
		cecTechnicalUser.setTenantName("TechnicalUserCredential");
		cecTechnicalUser.setOauthURL("http://test.com");
		cecTechnicalUser.setPassword("password");
		cecTechnicalUser.setTechnicalUser("Testuser");
		cecServiceModel = new CecServiceModel();
		cecServiceModel.setIdentifier(ServiceClient.TICKETSERVICECLIENT);

		cecServiceModel.setServiceURL("http://test.com");

		when(getModelService().create(CecServiceModel.class)).thenReturn(cecServiceModel);
		when(flexibleSearchService.getModelByExample(Mockito.any())).thenReturn(cecServiceModel);
	}

	@Test
	public void testBuildSapCpConfiguration()
	{

		final Class clientType = com.sap.platform.sapcpconfiguration.service.TicketServiceDummyClient.class;


		Assert.notNull(classUnderTest.buildSapCpConfiguration(cecTechnicalUser, clientType).toString());
	}

	@Test
	public void testFetCecServiceForId()
	{
		de.hybris.platform.testframework.Assert.assertEquals("TicketServiceClient",
				classUnderTest.getCecServiceForId("TicketServiceClient").getIdentifier());

	}

	@Test
	public void getBaseSiteCecServiceMappingForIdTest()
	{
		final String id = "electronics";
		final BaseSiteCecServiceMappingModel serviceMappingModel = new BaseSiteCecServiceMappingModel();
		when(getModelService().create(BaseSiteCecServiceMappingModel.class)).thenReturn(serviceMappingModel);
		when(flexibleSearchService.getModelByExample(Mockito.any())).thenReturn(serviceMappingModel);
		classUnderTest.getBaseSiteCecServiceMappingForId(id, cecServiceModel);
	}


}
