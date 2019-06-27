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
/**
 *
 */
package de.hybris.platform.personalizationcms.action.interceptor;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.personalizationcms.model.CxCmsActionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CxActionRemoveInterceptorTest
{
	private final CxActionRemoveInterceptor interceptor = new CxActionRemoveInterceptor();

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private CMSComponentService cmsComponentService;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private ModelService modelService;

	@Mock
	private CatalogVersionModel catalogVersion;


	@Before
	public void setupMocks()
	{
		MockitoAnnotations.initMocks(this);
		BDDMockito.given(configurationService.getConfiguration()).willReturn(configuration);
		BDDMockito
				.given(configuration.getString("personalizationcms.containers.cleanup.cxcatalogtocmscatalog.mockcatalog.mockversion"))
				.willReturn("mock1,mock2");
		BDDMockito.given(catalogVersionService.getCatalogVersion("mock1", "mock2")).willReturn(catalogVersion);
		interceptor.setCatalogVersionService(catalogVersionService);
		interceptor.setConfigurationService(configurationService);
		interceptor.setFlexibleSearchService(flexibleSearchService);
		interceptor.setModelService(modelService);
	}

	@Test
	public void shouldRetrieveCatalogVersionFromProperty()
	{
		final CxCmsActionModel action = new CxCmsActionModel();
		action.setCatalogVersion(new CatalogVersionModel());
		action.getCatalogVersion().setCatalog(new CatalogModel());
		action.getCatalogVersion().setVersion("mockversion");
		action.getCatalogVersion().getCatalog().setId("mockcatalog");
		final CatalogVersionModel contentCatalog = interceptor.getContentCatalog(action).get();
		Assert.assertEquals(catalogVersion, contentCatalog);
	}

	@Test
	public void shouldRetrieveNoCatalogVersionFromBadlyFormatedProperty()
	{
		BDDMockito
				.given(configuration.getString("personalizationcms.containers.cleanup.cxcatalogtocmscatalog.mockcatalog.mockversion"))
				.willReturn("mock1");

		final CxCmsActionModel action = new CxCmsActionModel();
		action.setCatalogVersion(new CatalogVersionModel());
		action.getCatalogVersion().setCatalog(new CatalogModel());
		action.getCatalogVersion().setVersion("mockversion");
		action.getCatalogVersion().getCatalog().setId("mockcatalog");
		Assert.assertFalse(interceptor.getContentCatalog(action).isPresent());
	}

	@Test
	public void shouldRetrieveDefaultCatalogVersion()
	{
		final CxCmsActionModel action = new CxCmsActionModel();
		final CatalogVersionModel cv = new CatalogVersionModel();
		action.setCatalogVersion(cv);
		action.getCatalogVersion().setCatalog(new CatalogModel());
		action.getCatalogVersion().setVersion("unknownversion");
		action.getCatalogVersion().getCatalog().setId("unknowncatalog");

		final CatalogVersionModel contentCatalog = interceptor.getContentCatalog(action).get();
		Assert.assertEquals(cv, contentCatalog);
	}


}
