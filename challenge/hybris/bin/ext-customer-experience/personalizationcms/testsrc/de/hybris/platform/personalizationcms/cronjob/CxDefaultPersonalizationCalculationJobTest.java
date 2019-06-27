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
package de.hybris.platform.personalizationcms.cronjob;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.personalizationservices.action.dao.CxActionResultDao;
import de.hybris.platform.personalizationservices.cronjob.CxDefaultPersonalizationCalculationJob;
import de.hybris.platform.personalizationservices.model.CxResultsModel;
import de.hybris.platform.personalizationservices.model.process.CxDefaultPersonalizationCalculationCronJobModel;
import de.hybris.platform.personalizationservices.service.CxCatalogService;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionTokenService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;


@IntegrationTest
public class CxDefaultPersonalizationCalculationJobTest extends ServicelayerTransactionalTest
{

	private static final String TEST_SITE_1 = "testSite1";
	private static final String TEST_SITE_2 = "testSite2";
	private static final String TEST_SITE_3 = "testSite3";
	private static final String CATALOG_ID = "testContentCatalog";
	private static final String CATALOG_ID1 = "testContentCatalog1";

	@Resource(mappedName = "cxDefaultPersonalizationCalculationJob")
	private CxDefaultPersonalizationCalculationJob cxDefaultPersonalizationCalculationJob;

	@Resource
	private CxService cxService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private CxCatalogService cxCatalogService;

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	FlexibleSearchService flexibleSearchService;

	@Resource
	private CxActionResultDao cxActionResultDao;

	@Resource
	private DefaultSessionTokenService defaultSessionTokenService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/personalizationcms/test/testdata_cronjob.impex", "utf-8");
	}

	protected void assertDefaultResultStoredInDatabase(final String catalogId)
	{
		final List<CxResultsModel> resultsList = getDefaultResultsForSession();
		Assert.assertTrue(resultsList.size() == 1);
		final CxResultsModel cxResult = resultsList.iterator().next();
		Assert.assertThat(cxResult.getCatalogVersion().getVersion(), CoreMatchers.equalTo("Online"));
		Assert.assertThat(cxResult.getCatalogVersion().getCatalog().getId(), CoreMatchers.equalTo(catalogId));
		Assert.assertTrue(cxResult.isDefault());
		Assert.assertNotNull(cxResult.getResults());
	}

	protected List<CxResultsModel> getDefaultResultsForSession()
	{
		return cxActionResultDao.findResultsBySessionKey(defaultSessionTokenService.getOrCreateSessionToken())//
				.stream()//
				.filter(CxResultsModel::isDefault)//
				.collect(Collectors.toList());
	}


	protected void assertDefaultResultStoredInDatabase(final String catalogId1, final String catalogId2)
	{
		final List<CxResultsModel> resultsList = getDefaultResultsForSession();
		Assert.assertTrue(resultsList.size() == 2);
		final CxResultsModel cxResult = resultsList.get(0);
		Assert.assertThat(cxResult.getCatalogVersion().getVersion(), CoreMatchers.equalTo("Online"));
		Assert.assertThat(cxResult.getCatalogVersion().getCatalog().getId(),
				CoreMatchers.anyOf(CoreMatchers.equalTo(catalogId1), CoreMatchers.equalTo(catalogId2)));
		Assert.assertTrue(cxResult.isDefault());
		Assert.assertNotNull(cxResult.getResults());
		final CxResultsModel cxResultnext = resultsList.get(1);
		Assert.assertThat(cxResultnext.getCatalogVersion().getVersion(), CoreMatchers.equalTo("Online"));
		Assert.assertThat(cxResultnext.getCatalogVersion().getCatalog().getId(),
				CoreMatchers.anyOf(CoreMatchers.equalTo(catalogId1), CoreMatchers.equalTo(catalogId2)));
		Assert.assertTrue(cxResultnext.isDefault());
		Assert.assertNotNull(cxResultnext.getResults());
	}

	protected void assertNoResultsInDatabase()
	{
		final List<CxResultsModel> resultsList = cxActionResultDao
				.findResultsBySessionKey(defaultSessionTokenService.getOrCreateSessionToken());
		Assert.assertTrue(resultsList.size() == 0);
	}

	@Test
	public void shouldReturnFailureIfThereIsNoBaseSite()
	{
		// given
		final CxDefaultPersonalizationCalculationCronJobModel cronJobModel = new CxDefaultPersonalizationCalculationCronJobModel();

		// when
		final PerformResult performResult = cxDefaultPersonalizationCalculationJob.perform(cronJobModel);

		// then
		Assert.assertEquals("Job should be failure", CronJobResult.FAILURE, performResult.getResult());
		Assert.assertEquals("Job should be finished", CronJobStatus.FINISHED, performResult.getStatus());
	}

	@Test
	public void shouldReturnSuccessIfThereIsBaseSite()
	{
		// given
		final CxDefaultPersonalizationCalculationCronJobModel cronJobModel = new CxDefaultPersonalizationCalculationCronJobModel();
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(TEST_SITE_1);
		cronJobModel.setBaseSites(Collections.singleton(baseSite));

		// when
		final PerformResult performResult = cxDefaultPersonalizationCalculationJob.perform(cronJobModel);

		// then
		Assert.assertEquals("Job should be successful", CronJobResult.SUCCESS, performResult.getResult());
		Assert.assertEquals("Job should be finished", CronJobStatus.FINISHED, performResult.getStatus());
	}

	@Test
	public void shouldCalculateAndStoreNoResultsForBaseSite()
	{
		//given
		final CxDefaultPersonalizationCalculationCronJobModel cronJobModel = new CxDefaultPersonalizationCalculationCronJobModel();
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(TEST_SITE_3);
		cronJobModel.setBaseSites(Collections.singleton(baseSite));

		// when
		final PerformResult performResult = cxDefaultPersonalizationCalculationJob.perform(cronJobModel);

		// then
		Assert.assertEquals("Job should be successful", CronJobResult.SUCCESS, performResult.getResult());
		Assert.assertEquals("Job should be finished", CronJobStatus.FINISHED, performResult.getStatus());
		assertNoResultsInDatabase();
	}

	@Test
	public void shouldCalculateAndStoreResultsForOneBaseSite()
	{
		//given
		final CxDefaultPersonalizationCalculationCronJobModel cronJobModel = new CxDefaultPersonalizationCalculationCronJobModel();
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(TEST_SITE_1);
		cronJobModel.setBaseSites(Collections.singleton(baseSite));

		// when
		final PerformResult performResult = cxDefaultPersonalizationCalculationJob.perform(cronJobModel);

		// then
		Assert.assertEquals("Job should be successful", CronJobResult.SUCCESS, performResult.getResult());
		Assert.assertEquals("Job should be finished", CronJobStatus.FINISHED, performResult.getStatus());
		assertDefaultResultStoredInDatabase(CATALOG_ID);
	}

	@Test
	public void shouldCalculateAndStoreResultsForTwoBaseSites()
	{
		//given
		final CxDefaultPersonalizationCalculationCronJobModel cronJobModel = new CxDefaultPersonalizationCalculationCronJobModel();
		final BaseSiteModel baseSite1 = baseSiteService.getBaseSiteForUID(TEST_SITE_1);
		final BaseSiteModel baseSite2 = baseSiteService.getBaseSiteForUID(TEST_SITE_2);
		cronJobModel.setBaseSites(Sets.newHashSet(baseSite1, baseSite2));

		// when
		final PerformResult performResult = cxDefaultPersonalizationCalculationJob.perform(cronJobModel);

		// then
		Assert.assertEquals("Job should be successful", CronJobResult.SUCCESS, performResult.getResult());
		Assert.assertEquals("Job should be finished", CronJobStatus.FINISHED, performResult.getStatus());
		assertDefaultResultStoredInDatabase(CATALOG_ID, CATALOG_ID1);
	}
}
