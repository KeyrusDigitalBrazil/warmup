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
package de.hybris.platform.personalizationservices.job;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.jobs.AbstractMaintenanceJobPerformable;
import de.hybris.platform.personalizationservices.action.dao.CxActionResultDao;
import de.hybris.platform.personalizationservices.model.CxResultsCleaningCronJobModel;
import de.hybris.platform.personalizationservices.model.CxResultsModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class CleanupCxResultsJobTest extends ServicelayerTransactionalTest
{
	private static final String RESULT_KEY_1 = "resultKey1";
	private static final String SESSION_KEY_1 = "sessionKey1";
	private static final String RESULT_KEY_2 = "resultKey2";
	private static final String SESSION_KEY_2 = "sessionKey2";
	private static final boolean ANONYMOUS = true;

	@Resource(name = "cleanupCxResultsJobPerformable")
	private AbstractMaintenanceJobPerformable cleanupCxResultsJob;

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CxActionResultDao cxActionResultDao;

	@Resource
	private TimeService timeService;

	@Before
	public void setup() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
	}

	@After
	public void resetTimeOffset()
	{
		timeService.resetTimeOffset();
	}

	@Test
	public void testNoResultToRemove()
	{
		//when
		final PerformResult result = cleanupCxResultsJob.perform(modelService.create(CxResultsCleaningCronJobModel.class));

		//then
		Assert.assertEquals("Job should be successful", CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals("Job should be finished", CronJobStatus.FINISHED, result.getStatus());
	}

	@Test
	public void testCleanResults() throws Exception
	{
		//given
		timeService.setTimeOffset(-1000);
		final int maxResultsAge = 1;
		createCxResults(RESULT_KEY_1, SESSION_KEY_1, !ANONYMOUS, new Date(timeService.getCurrentTime().getTime() - maxResultsAge * 1000 - 1));
		createCxResults(RESULT_KEY_2, SESSION_KEY_2, !ANONYMOUS, new Date(timeService.getCurrentTime().getTime() - maxResultsAge * 1000 - 1));
		assertThereAreResults(SESSION_KEY_1, 1);
		assertThereAreResults(SESSION_KEY_2, 1);

		timeService.resetTimeOffset();

		//when
		final PerformResult result = cleanupCxResultsJob.perform(createCronJobModel(maxResultsAge, !ANONYMOUS));

		//then
		Assert.assertEquals("Job should be successful", CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals("Job should be finished", CronJobStatus.FINISHED, result.getStatus());
		assertThereIsNoResults(SESSION_KEY_1);
		assertThereIsNoResults(SESSION_KEY_2);
	}

	@Test
	public void testCleanOnlyAnonymousResults() throws Exception
	{
		//given
		timeService.setTimeOffset(-1000);
		final int maxResultsAge = 1;
		createCxResults(RESULT_KEY_1, SESSION_KEY_1, ANONYMOUS, new Date(timeService.getCurrentTime().getTime() - maxResultsAge * 1000 - 1));
		createCxResults(RESULT_KEY_2, SESSION_KEY_1, !ANONYMOUS, new Date(timeService.getCurrentTime().getTime() - maxResultsAge * 1000 - 1));
		assertThereAreResults(SESSION_KEY_1, 2);

		timeService.resetTimeOffset();

		//when
		final PerformResult result = cleanupCxResultsJob.perform(createCronJobModel(maxResultsAge, ANONYMOUS));

		//then
		Assert.assertEquals("Job should be successful", CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals("Job should be finished", CronJobStatus.FINISHED, result.getStatus());
		assertThereAreResults(SESSION_KEY_1, RESULT_KEY_2);
	}

	@Test
	public void testCleanOnlyOldResults() throws Exception
	{
		//given
		timeService.setTimeOffset(-1000);
		final int maxResultsAge = 60;
		createCxResults(RESULT_KEY_1, SESSION_KEY_1, ANONYMOUS, new Date(timeService.getCurrentTime().getTime() - maxResultsAge * 1000 - 1000));
		createCxResults(RESULT_KEY_2, SESSION_KEY_1, ANONYMOUS, new Date(timeService.getCurrentTime().getTime()));
		assertThereAreResults(SESSION_KEY_1, 2);

		timeService.resetTimeOffset();

		//when
		final PerformResult result = cleanupCxResultsJob.perform(createCronJobModel(maxResultsAge, ANONYMOUS));

		//then
		Assert.assertEquals("Job should be successful", CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals("Job should be finished", CronJobStatus.FINISHED, result.getStatus());
		assertThereAreResults(SESSION_KEY_1, RESULT_KEY_2);
	}

	@Test
	public void testCleanOnlyNotDefaultResults() throws Exception
	{
		//given
		timeService.setTimeOffset(-1000);
		final int maxResultsAge = 60;
		createCxResults(RESULT_KEY_1, SESSION_KEY_1, ANONYMOUS,
				new Date(timeService.getCurrentTime().getTime() - maxResultsAge * 1000 - 1));
		createDefaultCxResults(RESULT_KEY_2, SESSION_KEY_1, ANONYMOUS,
				new Date(timeService.getCurrentTime().getTime() - maxResultsAge * 1000 - 1));
		assertThereAreResults(SESSION_KEY_1, 2);

		timeService.resetTimeOffset();

		//when
		final PerformResult result = cleanupCxResultsJob.perform(createCronJobModel(maxResultsAge, ANONYMOUS));

		//then
		Assert.assertEquals("Job should be successful", CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals("Job should be finished", CronJobStatus.FINISHED, result.getStatus());
		assertThereAreResults(SESSION_KEY_1, 1);
		assertThereAreResults(SESSION_KEY_1, RESULT_KEY_2);
	}

	protected CxResultsCleaningCronJobModel createCronJobModel(final int maxResultAge, final boolean anonymous)
	{
		final CxResultsCleaningCronJobModel cronJobModel = modelService.create(CxResultsCleaningCronJobModel.class);
		cronJobModel.setMaxResultsAge(maxResultAge);
		cronJobModel.setAnonymous(anonymous);
		return cronJobModel;
	}

	private void createCxResults(final String key, final String sessionKey, final boolean anonymous, final Date date)
	{
		final CxResultsModel results = createCxResultsModel(key, sessionKey, anonymous, date);
		modelService.save(results);
	}

	private void createDefaultCxResults(final String key, final String sessionKey, final boolean anonymous, final Date date)
	{
		final CxResultsModel results = createCxResultsModel(key, sessionKey, anonymous, date);
		results.setDefault(true);
		modelService.save(results);
	}

	private CxResultsModel createCxResultsModel(final String key, final String sessionKey, final boolean anonymous,
			final Date date)
	{
		final CxResultsModel results = new CxResultsModel();
		results.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
		results.setKey(key);
		results.setSessionKey(sessionKey);
		results.setCalculationTime(date);
		results.setAnonymous(anonymous);
		results.setResults("testResults");
		return results;
	}

	private void assertThereIsNoResults(final String sessionId)
	{
		final List<CxResultsModel> resultList = cxActionResultDao.findResultsBySessionKey(sessionId);
		assertEquals("There should be no results", 0, resultList.size());
	}

	private void assertThereAreResults(final String sessionId, final int count)
	{
		final List<CxResultsModel> resultList = cxActionResultDao.findResultsBySessionKey(sessionId);
		assertEquals("Results count should be : " + count, count, resultList.size());
	}

	private void assertThereAreResults(final String sessionId, final String resultsKey)
	{
		final List<CxResultsModel> resultList = cxActionResultDao.findResultsBySessionKey(sessionId);
		assertEquals("There should be 1 result", 1, resultList.size());
		assertEquals("ResultKey should be equal : " + resultsKey, resultsKey, resultList.get(0).getKey());
	}
}
