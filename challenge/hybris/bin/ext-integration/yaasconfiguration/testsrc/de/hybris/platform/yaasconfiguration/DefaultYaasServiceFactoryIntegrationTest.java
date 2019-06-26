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

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yaasconfiguration.client.ProductClient;
import de.hybris.platform.yaasconfiguration.model.BaseSiteServiceMappingModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasProjectModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class DefaultYaasServiceFactoryIntegrationTest extends ServicelayerBaseTest
{

	@Resource
	private ModelService modelService;

	@Resource
	private YaasConfigurationService yaasConfigurationService;

	@Resource
	private YaasServiceFactory yaasServiceFactory;

	@Resource
	private SessionService sessionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private CharonFactory charonFactory;

	@Resource
	private BaseSiteService baseSiteService;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private YaasProjectModel yaasProject;

	private BaseSiteServiceMappingModel serviceMapper;
	private YaasClientCredentialModel yassCredential;
	private YaasServiceModel yaasService;

	private BaseSiteModel baseSite;

	@Before
	public void before()
	{
		baseSite = new BaseSiteModel();
		baseSite.setUid("testSite");

		yaasProject = modelService.create(YaasProjectModel.class);
		yaasProject.setIdentifier("devproject");
		yaasProject.setBaseSite(baseSite);

		yassCredential = modelService.create(YaasClientCredentialModel.class);
		yassCredential.setIdentifier("devapplication");
		yassCredential.setClientId("5jfdsAxDYBJJc5DEtoN9rtZdxfrF7h5R");
		yassCredential.setClientSecret("4MAKkTecaOFewdlH");
		yassCredential.setPubsubClient("Order");
		yassCredential.setOauthURL("https://api.yaas.io/hybris/oauth2/v1");
		yassCredential.setYaasProject(yaasProject);

		yaasService = modelService.create(YaasServiceModel.class);
		yaasService.setIdentifier("ProductClient");
		yaasService.setServiceURL("https://api.yaas.io/hybris/product/v2");
		yaasService.setServiceScope("hybris.product_read_unpublished");

		modelService.saveAll(Arrays.asList(baseSite, yassCredential, yaasProject, yaasService));

	}

	@Test
	public void testYaasClientCreation_FromGivenMapper() throws Exception
	{
		serviceMapper = new BaseSiteServiceMappingModel();
		serviceMapper.setBaseSite("testSite");
		serviceMapper.setYaasService(yaasService);
		serviceMapper.setYaasClientCredential(yassCredential);

		modelService.save(serviceMapper);

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("testSite");
		baseSiteService.setCurrentBaseSite(baseSite, false);

		final ProductClient productClient = yaasServiceFactory.lookupService(ProductClient.class);

		assertNotNull(productClient);
	}

	@Test
	public void testYaasClientCreation_NullCredential_withoutServiceMapper() throws Exception
	{
		serviceMapper = new BaseSiteServiceMappingModel();
		serviceMapper.setBaseSite("electronics");
		serviceMapper.setYaasService(yaasService);
		serviceMapper.setYaasClientCredential(yassCredential);

		modelService.save(serviceMapper);

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("testSite");
		baseSiteService.setCurrentBaseSite(baseSite, false);

		expectedException.expectMessage(
				containsString("Failed to find Yaas client credential configuration for the given serviceType :ProductClient"));
		yaasServiceFactory.lookupService(ProductClient.class);
	}

}
