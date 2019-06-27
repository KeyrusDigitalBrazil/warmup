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

import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_URL;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.yaasconfiguration.client.ProductClient;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasClientCredentialLocator;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;
import de.hybris.platform.yaasconfiguration.service.impl.DefaultYaasServiceFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultYaasServiceFactoryUnitTest extends YaasConfigurationTestUtils
{
	@Mock
	private YaasConfigurationService yaasConfigurationService;

	@Mock
	private CharonFactory charonFactory;

	@Mock
	private YaasClientCredentialLocator yaasMappingLocator;

	@Mock
	private YaasServiceModel yaasServiceModel;

	@Mock
	private YaasClientCredentialModel yaasClientCredentialModel;

	@Mock
	YaasClientCredentialLocator strategy;

	@Spy
	private final List<YaasClientCredentialLocator> lookupLocators = new ArrayList();

	@InjectMocks
	private final DefaultYaasServiceFactory yaasServiceAdaptor = new DefaultYaasServiceFactory();

	@Before
	public void setup()
	{
		yaasServiceAdaptor.setYaasConfigurationService(yaasConfigurationService);
		yaasServiceAdaptor.setCharonFactory(charonFactory);
		lookupLocators.add(strategy);
	}

	@Test
	public void testLookupService_validateParameter()
	{
		errorMustBeReported("serviceType must not be null");
		yaasServiceAdaptor.lookupService(null);
	}

	@Test
	public void testLookupService_NoYaasServiceConfiguration()
	{
		when(yaasConfigurationService.getYaasServiceForId("ProductClient")).thenThrow(new ModelNotFoundException(""));
		errorMustBeReported("Failed to find YaaS service configuration for the given serviceType :ProductClient");
		yaasServiceAdaptor.lookupService(ProductClient.class);
	}

	@Test
	public void testLookupService_NoYaasYaasClientCredentialConfiguration()
	{
		when(yaasConfigurationService.getYaasServiceForId("ProductClient")).thenReturn(yaasServiceModel);

		errorMustBeReported("Failed to find Yaas client credential configuration for the given serviceType :ProductClient");
		yaasServiceAdaptor.lookupService(ProductClient.class);
	}

	@Test
	public void testLookupService_YaasClientCredentialFromStrategy()
	{
		prepareClient(ProductClient.class);

		//Strategy returned from the List
		when(strategy.lookup(any(YaasServiceModel.class))).thenReturn(Mockito.mock(YaasClientCredentialModel.class));

		yaasServiceAdaptor.lookupService(ProductClient.class);

		// It should invoke YaasClientCredentialLocator.lookup() at once to get the YaasServiceModel
		verify(strategy, times(1)).lookup(any(YaasServiceModel.class));
	}

	protected void prepareClient(final Class client)
	{
		final Map<String, String> yaasConfig = new HashMap();
		yaasConfig.put(YAAS_OAUTH_URL, "https://api.yaas.io/hybris/oauth2/v1");
		when(yaasConfigurationService.getYaasServiceForId("ProductClient")).thenReturn(yaasServiceModel);
		when(yaasClientCredentialModel.getIdentifier()).thenReturn("ClientCredential");
		when(yaasConfigurationService.buildYaasConfig(Mockito.mock(YaasClientCredentialModel.class), client))
				.thenReturn(yaasConfig);
	}

}
