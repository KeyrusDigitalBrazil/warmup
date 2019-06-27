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

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yaasconfiguration.model.BaseSiteServiceMappingModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasProjectModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasClientCredentialLocator;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class DefaultYaasServiceMappingIntegrationTest extends ServicelayerBaseTest
{

	@Resource
	private ModelService modelService;

	@Resource
	private YaasConfigurationService yaasConfigurationService;

	@Resource
	private YaasClientCredentialLocator defaultBaseSiteClientCredentialLocator;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private BaseSiteService baseSiteService;

	private YaasProjectModel yaasProject;

	private BaseSiteServiceMappingModel serviceMapper;
	private YaasClientCredentialModel yassCredential;
	private YaasServiceModel yaasService;

	@Before
	public void before()
	{

		final BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setUid("electronics");

		yaasProject = modelService.create(YaasProjectModel.class);
		yaasProject.setIdentifier("devproject");
		yaasProject.setBaseSite(baseSite);

		yassCredential = modelService.create(YaasClientCredentialModel.class);
		yassCredential.setIdentifier("FromRelation");
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
	public void testYaasClientCredential_FromGivenMapper() throws Exception
	{
		yassCredential = modelService.create(YaasClientCredentialModel.class);
		yassCredential.setIdentifier("MapperApplication");
		yassCredential.setClientId("5jfdsAxDYBJJc5DEtoN9rtZdxfrF7h5R");
		yassCredential.setClientSecret("4MAKkTecaOFewdlH");
		yassCredential.setPubsubClient("Order");
		yassCredential.setOauthURL("https://api.yaas.io/hybris/oauth2/v1");
		yassCredential.setYaasProject(yaasProject);

		yaasService = modelService.create(YaasServiceModel.class);
		yaasService.setIdentifier("MapperClient");
		yaasService.setServiceURL("https://api.yaas.io/hybris/product/v2");
		yaasService.setServiceScope("hybris.product_read_unpublished");

		serviceMapper = new BaseSiteServiceMappingModel();
		serviceMapper.setBaseSite("electronics");
		serviceMapper.setYaasClientCredential(yassCredential);
		serviceMapper.setYaasService(yaasService);

		modelService.saveAll(Arrays.asList(yassCredential, yaasService, serviceMapper));

		//Set the current basesite to match the serviceMapper and expect the YaasClientCredentialModel
		//from the serviceMapper.
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("electronics");
		baseSiteService.setCurrentBaseSite(baseSite, false);

		final YaasClientCredentialModel result = defaultBaseSiteClientCredentialLocator.lookup(yaasService);

		//The resulted client is from the mapper
		assertThat(result.getIdentifier()).isEqualTo("MapperApplication");
	}

}
