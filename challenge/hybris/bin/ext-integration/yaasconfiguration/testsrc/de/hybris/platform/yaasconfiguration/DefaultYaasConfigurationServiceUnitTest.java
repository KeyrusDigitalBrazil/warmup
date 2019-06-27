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

import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_CLIENT_SCOPE;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_CLIENT_URL;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_CLIENTID;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_CLIENTSECRET;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_URL;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_TENANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.yaasconfiguration.client.ProductClient;
import de.hybris.platform.yaasconfiguration.model.BaseSiteServiceMappingModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasProjectModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.impl.DefaultYaasConfigurationService;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultYaasConfigurationServiceUnitTest extends YaasConfigurationTestUtils
{

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private YaasServiceModel yaasServiceModel;

	@Mock
	private YaasClientCredentialModel yaasClientCredentialModel;

	@Mock
	private BaseSiteServiceMappingModel yaasServiceMappingModel;


	private DefaultYaasConfigurationService configurationService;

	@Before
	public void setup()
	{
		configurationService = new DefaultYaasConfigurationService();
		configurationService.setFlexibleSearchService(flexibleSearchService);
		when(yaasClientCredentialModel.getIdentifier()).thenReturn("clientCredentialId");
		when(yaasServiceModel.getIdentifier()).thenReturn("serviceId");
		when(yaasServiceMappingModel.getBaseSite()).thenReturn("mapperId");
	}

	@Test
	public void testGetYaasClientCredentialForId()
	{
		when(flexibleSearchService.getModelByExample(Mockito.any())).thenReturn(yaasClientCredentialModel);

		assertEquals("clientCredentialId", configurationService.getYaasClientCredentialForId("clientCredentialId").getIdentifier());
	}

	@Test
	public void testGetYaasClientCredentialForId_validation()
	{
		errorMustBeReported("Yaas Client Credential configuration must not be null");
		configurationService.getYaasClientCredentialForId(null);
	}

	@Test
	public void testGetYaasServiceForId()
	{
		when(flexibleSearchService.getModelByExample(Mockito.any())).thenReturn(yaasServiceModel);

		assertEquals("serviceId", configurationService.getYaasServiceForId("serviceId").getIdentifier());
	}

	@Test
	public void testGetYaasServiceForId_validation()
	{
		errorMustBeReported("Yaas Servcie configuration must not be null");
		configurationService.getYaasServiceForId(null);
	}

	@Test
	public void testGetBaseSiteServiceMappingForId()
	{
		when(flexibleSearchService.getModelByExample(Mockito.any())).thenReturn(yaasServiceMappingModel);

		assertEquals("mapperId", configurationService.getBaseSiteServiceMappingForId("mapperId", yaasServiceModel).getBaseSite());
	}

	@Test
	public void testGetBaseSiteServiceMappingForId_validation()
	{
		errorMustBeReported("id must not be null");
		configurationService.getBaseSiteServiceMappingForId(null, yaasServiceModel).getBaseSite();
	}

	@Test
	public void testGetBaseSiteServiceMappingForId_serviceModel_validation()
	{
		errorMustBeReported("serviceModel must not be null");
		configurationService.getBaseSiteServiceMappingForId("id", null).getBaseSite();
	}

	@Test
	public void testBuildYaasConfig_UsingYaasClientCredential()
	{

		final YaasClientCredentialModel yaasClientCredential = createYaasCredentialClient();
		final YaasServiceModel yaasService = createYaasService();

		configurationService.setFlexibleSearchService(flexibleSearchService);

		when(flexibleSearchService.getModelByExample(Mockito.isA(YaasClientCredentialModel.class)))
				.thenReturn(yaasClientCredential);

		when(flexibleSearchService.getModelByExample(Mockito.isA(YaasServiceModel.class))).thenReturn(yaasService);

		final Map<String, String> configValue = configurationService.buildYaasConfig(yaasClientCredential, ProductClient.class);

		assertNotNull(configValue);

		assertEquals(configValue.get(YAAS_OAUTH_URL), yaasClientCredential.getOauthURL());
		assertEquals(configValue.get(YAAS_OAUTH_CLIENTID), yaasClientCredential.getClientId());
		assertEquals(configValue.get(YAAS_OAUTH_CLIENTSECRET), yaasClientCredential.getClientSecret());
		assertEquals(configValue.get(YAAS_TENANT), yaasClientCredential.getYaasProject().getIdentifier());
		assertEquals(configValue.get(YAAS_CLIENT_URL), yaasService.getServiceURL());
		assertEquals(configValue.get(YAAS_CLIENT_SCOPE), yaasService.getServiceScope());

	}

	private YaasClientCredentialModel createYaasCredentialClient()
	{

		final YaasProjectModel yaasProject = new YaasProjectModel();
		yaasProject.setIdentifier("devproject");

		final YaasClientCredentialModel clientCredential = new YaasClientCredentialModel();

		clientCredential.setIdentifier("devapplication");
		clientCredential.setClientId("5jfdsAxDYBJJc5DEtoN9rtZdxfrF7h5R");
		clientCredential.setClientSecret("4MAKkTecaOFewdlH");
		clientCredential.setOauthURL("https://api.yaas.io/hybris/oauth2/v1");
		clientCredential.setYaasProject(yaasProject);

		return clientCredential;
	}

	private YaasServiceModel createYaasService()
	{

		final YaasServiceModel yaasService = new YaasServiceModel();

		yaasService.setIdentifier("ProductClient");
		yaasService.setServiceURL("https://api.yaas.io/hybris/product/v2");
		yaasService.setServiceScope("hybris.product_read_unpublished");

		return yaasService;
	}

}
