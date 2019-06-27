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

package de.hybris.platform.configurablebundleservices.daos.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.daos.BundleTemplateDao;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;


/**
 * Integration test suite for {@link DefaultBundleTemplateDao}
 * 
 */
@IntegrationTest
public class DefaultBundleTemplateDaoIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultBundleTemplateDaoIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private BundleTemplateDao bundleTemplateDao;

	@Resource
	private ProductService productService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private ModelService modelService;

	private ProductModel galaxyNexus;
	private ProductModel noBundleProduct;
	private ProductModel planStandard1Y;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for DefaultBundleTemplateDaoIntegrationTest ...");
		userService.setCurrentUser(userService.getAdminUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);

		// importing test csv
		final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		LOG.info("Existing value for " + ImpExConstants.Params.LEGACY_MODE_KEY + " :" + legacyModeBackup);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "false");
		importCsv("/configurablebundleservices/test/testBundleCommerceCartService.impex", "utf-8");
		importCsv("/configurablebundleservices/test/testApproveAllBundleTemplates.impex", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		LOG.info("Finished data for DefaultBundleTemplateDaoIntegrationTest " + (System.currentTimeMillis() - startTime) + "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");

		modelService.detachAll();

		galaxyNexus = productService.getProductForCode("GALAXY_NEXUS");
		noBundleProduct = productService.getProductForCode("2047052");
		planStandard1Y = productService.getProductForCode("PLAN_STANDARD_1Y");
	}

	@Test
	public void testFindBundleTemplatesByProduct()
	{

		final List<BundleTemplateModel> templates1 = bundleTemplateDao.findBundleTemplatesByProduct(galaxyNexus);
		Assert.assertEquals(1, templates1.size());
		for (final BundleTemplateModel template : templates1)
		{
			Assert.assertTrue(!template.getProducts().isEmpty());
			Assert.assertTrue(template.getProducts().contains(galaxyNexus));
		}

		int counter = 0;
		final List<BundleTemplateModel> templates2 = bundleTemplateDao.findBundleTemplatesByProduct(planStandard1Y);
		Assert.assertEquals(2, templates2.size());
		for (final BundleTemplateModel template : templates2)
		{
			Assert.assertTrue(!template.getProducts().isEmpty());
			Assert.assertTrue(template.getProducts().contains(planStandard1Y));
			if (counter == 0)
			{
				Assert.assertEquals("IPhonePlanSelection", template.getId());
			}
			else
			{
				Assert.assertEquals("SmartPhonePlanSelection", template.getId());
			}
			counter++;
		}

		final List<BundleTemplateModel> templates0 = bundleTemplateDao.findBundleTemplatesByProduct(noBundleProduct);
		Assert.assertTrue(templates0.isEmpty());
	}

	@Test
	public void testFindBundleByIdAndVersion()
	{
		final BundleTemplateModel validSmartPhoneDeviceSelection = bundleTemplateDao.findBundleTemplateByIdAndVersion(
				"SmartPhonePlanSelection", "1.0");
		Assert.assertNotNull(validSmartPhoneDeviceSelection);

	}

	@Test
	public void testCannotFindBundleByIdAndVersion()
	{
		thrown.expect(ModelNotFoundException.class);
		bundleTemplateDao.findBundleTemplateByIdAndVersion("SmartPhonePlanSelection", "1.1");

	}
}
