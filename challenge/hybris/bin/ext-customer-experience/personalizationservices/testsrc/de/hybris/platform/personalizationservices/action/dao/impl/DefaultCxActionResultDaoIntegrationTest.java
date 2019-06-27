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
package de.hybris.platform.personalizationservices.action.dao.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.personalizationservices.action.dao.CxActionResultDao;
import de.hybris.platform.personalizationservices.model.CxResultsModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class DefaultCxActionResultDaoIntegrationTest extends ServicelayerTransactionalTest
{
	private static String RESULT_KEY = "resultKey";
	private static String SESSION_KEY = "sessionKey";
	private static String NOT_EXISTING_RESULT_KEY = "notExistingResultKey";
	private static String NOT_EXISTING_SESSION_KEY = "notExistingSessionKey";

	@Resource(name = "defaultCxActionResultDao")
	private CxActionResultDao cxActionResultDao;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ModelService modelService;

	@Before
	public void setup() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createCxResults(RESULT_KEY, SESSION_KEY);
		createCxResults("resultkey1", SESSION_KEY);
		createCxResults("resultKey2", SESSION_KEY);
		createCxResults("resultKey3", "sessionKey1");
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

	@Test
	public void testFindResultsByKey()
	{
		//when
		final Optional<CxResultsModel> actionResult = cxActionResultDao.findResultsByKey(RESULT_KEY);

		//then
		Assert.assertTrue("Results should be found", actionResult.isPresent());
		Assert.assertEquals("Result keys should be equal", RESULT_KEY, actionResult.get().getKey());
	}

	@Test
	public void testFindResultsForNotExistingKey()
	{
		//when
		final Optional<CxResultsModel> actionResult = cxActionResultDao.findResultsByKey(NOT_EXISTING_RESULT_KEY);

		//then
		Assert.assertFalse("Results should not be found", actionResult.isPresent());
	}

	@Test
	public void testFindResultsBySessionKey()
	{
		//when
		final List<CxResultsModel> actionResultList = cxActionResultDao.findResultsBySessionKey(SESSION_KEY);

		//then
		Assert.assertNotNull("Results list should not be null", actionResultList);
		Assert.assertEquals("Results list should have 3 elements", 3, actionResultList.size());
	}

	@Test
	public void testFindResultsForNotExistingSessionKey()
	{
		//when
		final List<CxResultsModel> actionResultList = cxActionResultDao.findResultsBySessionKey(NOT_EXISTING_SESSION_KEY);

		//then
		Assert.assertTrue("Results list should be empty", actionResultList.isEmpty());
	}
}
