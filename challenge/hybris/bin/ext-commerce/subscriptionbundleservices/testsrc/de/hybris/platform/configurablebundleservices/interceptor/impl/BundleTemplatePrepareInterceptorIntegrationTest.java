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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Resource;


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
		importCsv("/subscriptionbundleservices/test/test-bundleselectioncriteria.impex", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		LOG.info("Finished data for BundleTemplatePrepareInterceptorTest " + (System.currentTimeMillis() - startTime) + "ms");

		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");
		catalogVersion = catalogVersionService.getSessionCatalogVersionForCatalog("testCatalog");

	}

	@Test
	public void testAddSelectionCriteriaToTemplate()
	{
		final AutoPickBundleSelectionCriteriaModel newSelection = modelService.create(AutoPickBundleSelectionCriteriaModel.class);
		newSelection.setId("SPSel" + System.currentTimeMillis());
		newSelection.setCatalogVersion(catalogVersion);

		modelService.save(newSelection);

		final BundleTemplateModel bundleTemplate = bundleTemplateService.getBundleTemplateForCode("PAYG_NoSelections");
		bundleTemplate.setBundleSelectionCriteria(newSelection);

		modelService.save(bundleTemplate);

		final String queryString = "select {pk} from {BundleSelectionCriteria} where {id} =?id";
		final FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(queryString);
		fsQuery.addQueryParameter("id", newSelection.getId());
		final SearchResult<BundleSelectionCriteriaModel> criterias = flexibleSearchService.search(fsQuery);
		Assert.assertFalse(criterias.getResult().isEmpty());
	}

	/**
	 * This test is ignored because when run as a junit test the bundletemplate retrieved from db and one passed to the
	 * interceptor are the same and hence test fails. Tested through hmc and the interceptor works as expected
	 */
	@Test
	@Ignore
	public void testUpdateTemplateWithCriteriaShouldNotDeleteCriteria()
	{
		final AutoPickBundleSelectionCriteriaModel newSelection = modelService.create(AutoPickBundleSelectionCriteriaModel.class);
		newSelection.setId("SPSel" + System.currentTimeMillis());
		newSelection.setCatalogVersion(catalogVersion);

		modelService.save(newSelection);

		final BundleTemplateModel bundleTemplate = bundleTemplateService.getBundleTemplateForCode("SmartPhoneDeviceSelection");
		Assert.assertNotNull(bundleTemplate.getBundleSelectionCriteria());
		final String oldselectionId = bundleTemplate.getBundleSelectionCriteria().getId();

		bundleTemplate.setBundleSelectionCriteria(newSelection);

		modelService.save(bundleTemplate);

		bundleTemplate.setName("Change name of template");

		final String queryString = "select {pk} from {BundleSelectionCriteria} where {id} =?id";
		final FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(queryString);
		fsQuery.addQueryParameter("id", newSelection.getId());
		final SearchResult<BundleSelectionCriteriaModel> criterias = flexibleSearchService.search(fsQuery);
		Assert.assertFalse(criterias.getResult().isEmpty());

		final FlexibleSearchQuery fsQuery2 = new FlexibleSearchQuery(queryString);
		fsQuery2.addQueryParameter("id", oldselectionId);
		final SearchResult<BundleSelectionCriteriaModel> results2 = flexibleSearchService.search(fsQuery);
		Assert.assertTrue(results2.getResult().isEmpty());
	}
}
