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
*
*/
package de.hybris.platform.yaasconfiguration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yaasconfiguration.model.BaseSiteServiceMappingModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;
import de.hybris.platform.yaasconfiguration.service.impl.DefaultBaseSiteClientCredentialLocator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultYaasServiceMappingLocatorUnitTest extends YaasConfigurationTestUtils
{
	@Mock
	private YaasConfigurationService yaasConfigurationService;

	@Mock
	private BaseSiteServiceMappingModel serviceMapper;

	@Mock
	private YaasServiceModel yaasServiceModel;

	@Mock
	private YaasClientCredentialModel yaasClientCredentialModel;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private BaseSiteModel baseSite;

	private final DefaultBaseSiteClientCredentialLocator mapper = new DefaultBaseSiteClientCredentialLocator();

	@Before
	public void setup()
	{
		mapper.setYaasConfigurationService(yaasConfigurationService);
		when(yaasConfigurationService.getYaasClientCredentialForId("credential")).thenReturn(yaasClientCredentialModel);

	}

	@Test
	public void testLookup_validation()
	{
		errorMustBeReported("serviceModel must not be null");
		mapper.lookup(null);
	}


	@Test
	public void testLookup_YaasClientCredentialFromMapper()
	{
		mapper.setBaseSiteService(baseSiteService);
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);

		when(baseSite.getUid()).thenReturn("testSite");
		when(yaasConfigurationService.getBaseSiteServiceMappingForId(Mockito.any(), any(YaasServiceModel.class)))
				.thenReturn(serviceMapper);
		when(serviceMapper.getYaasClientCredential()).thenReturn(yaasClientCredentialModel);

		final YaasClientCredentialModel yaasClientCredential = mapper.lookup(yaasServiceModel);

		//It should get the YaasClientCredential from the given serviceMapper which holds the relation between client credential to service
		//it is counted as 1, because the method getYaasClientCredential() should be called once under mapper.
		verify(serviceMapper, times(1)).getYaasClientCredential();

		assertNotNull(yaasClientCredential);
	}

	@Test
	public void testLookup_NullCredential_withoutServiceMapper()
	{
		mapper.setBaseSiteService(baseSiteService);
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);

		when(baseSite.getUid()).thenReturn("testSite");

		when(yaasConfigurationService.getBaseSiteServiceMappingForId(Mockito.any(), any(YaasServiceModel.class)))
				.thenThrow(new ModelNotFoundException("error"));

		final YaasClientCredentialModel yaasClientCredential = mapper.lookup(yaasServiceModel);

		assertNull(yaasClientCredential);

	}

}
