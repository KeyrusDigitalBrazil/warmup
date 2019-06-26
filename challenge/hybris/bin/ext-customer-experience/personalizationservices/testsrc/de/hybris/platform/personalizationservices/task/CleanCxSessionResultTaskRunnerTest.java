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
package de.hybris.platform.personalizationservices.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.personalizationservices.action.dao.CxActionResultDao;
import de.hybris.platform.personalizationservices.model.CxResultsModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class CleanCxSessionResultTaskRunnerTest extends ServicelayerTransactionalTest
{
	private static String RESULT_KEY_1 = "resultKey1";
	private static String SESSION_KEY_1 = "sessionKey1";
	private static String RESULT_KEY_2 = "resultKey2";
	private static String SESSION_KEY_2 = "sessionKey2";

	@Resource
	private CleanCxSessionResultsTaskRunner cleanCxSessionResultsTaskRunner;
	@Resource
	private CxActionResultDao cxActionResultDao;
	@Resource
	private ModelService modelService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private TaskService taskService;

	@Before
	public void setup() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
	}

	@Test
	public void testCleanResultsForSession() throws Exception
	{
		//given
		createCxResults(RESULT_KEY_1, SESSION_KEY_1);
		createCxResults(RESULT_KEY_2, SESSION_KEY_1);
		assertThereAreResults(SESSION_KEY_1);

		//when
		final TaskModel task = modelService.create(TaskModel.class);
		task.setContext(SESSION_KEY_1);
		cleanCxSessionResultsTaskRunner.run(taskService, task);

		//then
		assertThereIsNoResults(SESSION_KEY_1);
	}

	@Test
	public void testThatResulAreRemovedOnlyForSelectedSession() throws Exception
	{
		//given
		createCxResults(RESULT_KEY_1, SESSION_KEY_1);
		createCxResults(RESULT_KEY_2, SESSION_KEY_2);
		assertThereAreResults(SESSION_KEY_1);
		assertThereAreResults(SESSION_KEY_2);

		//when
		final TaskModel task = modelService.create(TaskModel.class);
		task.setContext(SESSION_KEY_1);
		cleanCxSessionResultsTaskRunner.run(taskService, task);

		//then
		assertThereIsNoResults(SESSION_KEY_1);
		assertThereAreResults(SESSION_KEY_2);
	}

	@Test
	public void testCleanWhenThereIsNoResults() throws Exception
	{
		//when
		final TaskModel task = modelService.create(TaskModel.class);
		task.setContext(SESSION_KEY_1);
		cleanCxSessionResultsTaskRunner.run(taskService, task);
	}

	private void createCxResults(final String key, final String sessionKey)
	{
		final CxResultsModel results = new CxResultsModel();
		results.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
		results.setKey(key);
		results.setSessionKey(sessionKey);
		results.setCalculationTime(new Date());
		results.setAnonymous(true);
		results.setResults("testResults");
		modelService.save(results);
	}

	private void assertThereIsNoResults(final String sessionId)
	{
		final List<CxResultsModel> resultList = cxActionResultDao.findResultsBySessionKey(sessionId);
		assertEquals(0, resultList.size());
	}

	private void assertThereAreResults(final String sessionId)
	{
		final List<CxResultsModel> resultList = cxActionResultDao.findResultsBySessionKey(sessionId);
		assertTrue(resultList.size() > 0);
	}
}
