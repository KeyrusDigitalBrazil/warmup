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

package de.hybris.platform.configurablebundleservices.interceptor.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.enums.BundleTemplateStatusEnum;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.util.Config;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for BundleTemplatePrepareIntercetpr
 */
@IntegrationTest
public class BundleTemplatePrepareInterceptorIntegrationTest extends ServicelayerTest
{

	private static final Logger LOG = Logger.getLogger(BundleTemplatePrepareInterceptorIntegrationTest.class);

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private BundleTemplateService bundleTemplateService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private CatalogVersionModel catalogVersion;

	@Before
	public void setUp() throws ImpExException
	{
		//
		LOG.info("Creating data for BundleTemplatePrepareInterceptorTest ..");
		// importing test csv
		final long startTime = System.currentTimeMillis();
		final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		LOG.info("Existing value for " + ImpExConstants.Params.LEGACY_MODE_KEY + " :" + legacyModeBackup);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "false");
		importCsv("/configurablebundleservices/test/test-bundleselectioncriteria.impex", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		LOG.info("Finished data for BundleTemplatePrepareInterceptorTest " + (System.currentTimeMillis() - startTime) + "ms");

		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");
		catalogVersion = catalogVersionService.getSessionCatalogVersionForCatalog("testCatalog");

	}

	@Test
	public void testCreateBundleTemplateCheckBundleStatus()
	{
		final BundleTemplateModel parentTemplate = modelService.create(BundleTemplateModel.class);
		parentTemplate.setCatalogVersion(catalogVersion);
		parentTemplate.setId("MyParentTemplate");
		parentTemplate.setName("MyParentTemplate");
		parentTemplate.setVersion("1.0");
		modelService.save(parentTemplate);
		modelService.refresh(parentTemplate);

		assertNotNull(parentTemplate.getStatus());
		assertEquals(BundleTemplateStatusEnum.UNAPPROVED, parentTemplate.getStatus().getStatus());
		assertEquals(parentTemplate.getCatalogVersion(), parentTemplate.getStatus().getCatalogVersion());

		parentTemplate.getStatus().setStatus(BundleTemplateStatusEnum.APPROVED);
		modelService.save(parentTemplate);
		modelService.refresh(parentTemplate);

		assertEquals(BundleTemplateStatusEnum.APPROVED, parentTemplate.getStatus().getStatus());

		final BundleTemplateModel childTemplate = modelService.create(BundleTemplateModel.class);
		childTemplate.setCatalogVersion(parentTemplate.getCatalogVersion());
		childTemplate.setId("MyChildTemplate");
		childTemplate.setName("MyChildTemplate");
		childTemplate.setVersion(parentTemplate.getVersion());
		childTemplate.setParentTemplate(parentTemplate);
		modelService.save(childTemplate);
		modelService.refresh(childTemplate);

		assertNotNull(childTemplate.getStatus());
		assertEquals(parentTemplate.getStatus(), childTemplate.getStatus());
	}
}
