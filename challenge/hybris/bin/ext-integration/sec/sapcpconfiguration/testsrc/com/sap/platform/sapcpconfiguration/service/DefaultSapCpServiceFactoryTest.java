/*
  [y] hybris Platform

  Copyright (c) 2000-2018 SAP SE All rights reserved.

  This software is the confidential and proprietary information of SAP Hybris ("Confidential Information"). You shall
  not disclose such Confidential Information and shall use it only in accordance with the terms of the license
  agreement you entered into with SAP Hybris.
  */


package com.sap.platform.sapcpconfiguration.service;

import static com.sap.platform.constants.SapcpconfigurationConstants.SAPCP_CLIENT_SCOPE;
import static com.sap.platform.constants.SapcpconfigurationConstants.SAPCP_CLIENT_URL;
import static com.sap.platform.constants.SapcpconfigurationConstants.SAPCP_TENANT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.search.impl.DefaultFlexibleSearchService;
import com.sap.platform.factory.SapCharonFactory;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.platform.model.CecServiceModel;
import com.sap.platform.model.CecTechnicalUserModel;
import com.sap.platform.sapcpconfiguration.service.impl.DefaultSapCpConfigurationService;
import com.sap.platform.sapcpconfiguration.service.impl.DefaultSapCpServiceFactory;



@UnitTest
public class DefaultSapCpServiceFactoryTest
{

	@InjectMocks
	private DefaultSapCpServiceFactory classUnderTest;

	@Mock
	private SapCharonFactory sapCharonFactory;

	@Mock
	private DefaultSapCpConfigurationService sapCpConfigurationService;

	@Mock
	private DefaultFlexibleSearchService flexibleSearchService;

	@Mock
	private CecTechnicalUserLookup lookupCredentials;

	private CecServiceModel cecServiceModel = null;

	private CecTechnicalUserModel cecTechnicalUser = null;

	private Map<String, String> config = null;




	@Before
	public void setUp()

	{
		MockitoAnnotations.initMocks(this);
		cecServiceModel = new CecServiceModel();
		cecTechnicalUser = new CecTechnicalUserModel();
		cecTechnicalUser.setTenantName("yectest1");
		cecServiceModel.setServiceURL("http:test-url.com");


		config = new HashMap();
		config.put(SAPCP_CLIENT_URL, cecServiceModel.getServiceURL());
		config.put(SAPCP_CLIENT_SCOPE, cecTechnicalUser.getTenantName());


		config.put(SAPCP_TENANT, cecTechnicalUser.getTenantName());


	}

	@Test
	public void lookupServiceTest()
	{


		when(sapCpConfigurationService.getCecServiceForId(Mockito.anyString())).thenReturn((cecServiceModel));
		when(sapCpConfigurationService.buildSapCpConfiguration(Mockito.any(), Mockito.any())).thenReturn(config);
		when(sapCharonFactory.client(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(TicketServiceDummyClient.class);
		when(lookupCredentials.lookup(Mockito.any())).thenReturn(cecTechnicalUser);
		assertEquals(com.sap.platform.sapcpconfiguration.service.TicketServiceDummyClient.class,
				classUnderTest.lookupService(com.sap.platform.sapcpconfiguration.service.TicketServiceDummyClient.class));
	}

	@Test(expected = SystemException.class)
	public void lookServiceNegativeTest()
	{
		when(sapCpConfigurationService.getCecServiceForId(Mockito.anyString())).thenThrow(ModelNotFoundException.class);
		classUnderTest.lookupService(com.sap.platform.sapcpconfiguration.service.TicketServiceDummyClient.class);
	}

	@Test(expected = SystemException.class)
	public void lookupServiceNullPointerException()
	{
		when(sapCpConfigurationService.getCecServiceForId(Mockito.anyString())).thenReturn(new CecServiceModel());
		classUnderTest.lookupService(com.sap.platform.sapcpconfiguration.service.TicketServiceDummyClient.class);

	}


}
