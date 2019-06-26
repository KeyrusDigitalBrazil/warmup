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
package de.hybris.platform.personalizationservices.action.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.action.dao.CxActionResultDao;
import de.hybris.platform.personalizationservices.data.CxAbstractActionResult;
import de.hybris.platform.personalizationservices.model.CxResultsModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.event.SerializationService;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCxActionResultServiceIntegrationTest extends ServicelayerTest
{
	@Resource
	private DefaultCxActionResultService defaultCxActionResultService;

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private SessionService sessionService;

	@Resource
	private TimeService timeService;

	@Resource
	private ModelService modelService;

	@Resource
	private CxActionResultDao cxActionResultDao;

	@Resource
	private SerializationService serializationService;

	private TestCxActionResultModel result1;
	private TestCxActionResultModel result2;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importData(new ClasspathImpExResource("/personalizationservices/test/testdata_cxsite.impex", "UTF-8"));
		createSampleResults();
	}

	@After
	public void resetTimeOffset()
	{
		timeService.resetTimeOffset();
	}

	private void createSampleResults()
	{
		result1 = new TestCxActionResultModel();
		result1.setActionCode("test1");
		result1.setCustomizationCode("test1CustCode");
		result1.setVariationCode("test1VarCode");
		result2 = new TestCxActionResultModel();
		result2.setActionCode("test2");
		result2.setCustomizationCode("test2CustCode");
		result2.setVariationCode("test2VarCode");
	}

	@Test
	public void shouldSerializeDeserializeActionResults()
	{
		final UserModel user = userService.getUserForUID("defaultcxcustomer");
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final List<CxAbstractActionResult> inputActionResults = Arrays.asList(result1, result2);

		defaultCxActionResultService.storeActionResults(user, cv, inputActionResults);
		final Optional<CxResultsModel> cxResultsOptional = cxActionResultDao
				.findResultsByKey(defaultCxActionResultService.getResultsKey(user, cv));
		Assert.assertTrue(cxResultsOptional.isPresent());

		defaultCxActionResultService.loadActionResultsInSession(user, Arrays.asList(cv));
		final List<CxAbstractActionResult> outputActionResults = defaultCxActionResultService.getActionResults(user, cv);
		Assert.assertThat(Integer.valueOf(outputActionResults.size()), CoreMatchers.equalTo(Integer.valueOf(2)));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getActionCode(), CoreMatchers.equalTo("test1"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getCustomizationCode(),
				CoreMatchers.equalTo("test1CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getVariationCode(),
				CoreMatchers.equalTo("test1VarCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(1)).getActionCode(), CoreMatchers.equalTo("test2"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(1)).getCustomizationCode(),
				CoreMatchers.equalTo("test2CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(1)).getVariationCode(),
				CoreMatchers.equalTo("test2VarCode"));
	}

	@Test
	public void shouldLoadMoreRecentResultsInSession()
	{
		final UserModel user = userService.getUserForUID("defaultcxcustomer");
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final List<CxAbstractActionResult> results1 = Collections.singletonList(result1);
		final List<CxAbstractActionResult> results2 = Collections.singletonList(result2);

		timeService.setTimeOffset(-1000);

		//results1 are stored in db and then loaded on session
		defaultCxActionResultService.storeActionResults(user, cv, results1);
		defaultCxActionResultService.loadActionResultsInSession(user, Arrays.asList(cv));
		List<CxAbstractActionResult> outputActionResults = defaultCxActionResultService.getActionResults(user, cv);
		Assert.assertThat(Integer.valueOf(outputActionResults.size()), CoreMatchers.equalTo(Integer.valueOf(1)));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getActionCode(), CoreMatchers.equalTo("test1"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getCustomizationCode(),
				CoreMatchers.equalTo("test1CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getVariationCode(),
				CoreMatchers.equalTo("test1VarCode"));

		timeService.resetTimeOffset();

		//results2 are stored in db and then loaded on session
		defaultCxActionResultService.storeActionResults(user, cv, results2);
		defaultCxActionResultService.loadActionResultsInSession(user, Arrays.asList(cv));
		outputActionResults = defaultCxActionResultService.getActionResults(user, cv);
		Assert.assertThat(Integer.valueOf(outputActionResults.size()), CoreMatchers.equalTo(Integer.valueOf(1)));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getActionCode(), CoreMatchers.equalTo("test2"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getCustomizationCode(),
				CoreMatchers.equalTo("test2CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getVariationCode(),
				CoreMatchers.equalTo("test2VarCode"));
	}

	@Test
	public void shouldNotLoadOlderResultsInSession()
	{
		final UserModel user = userService.getUserForUID("defaultcxcustomer");
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final List<CxAbstractActionResult> results1 = Collections.singletonList(result1);
		final List<CxAbstractActionResult> results2 = Collections.singletonList(result2);

		timeService.setTimeOffset(-1000);

		//results1 are stored in db and then loaded on session
		defaultCxActionResultService.storeActionResults(user, cv, results1);
		defaultCxActionResultService.loadActionResultsInSession(user, Arrays.asList(cv));
		List<CxAbstractActionResult> outputActionResults = defaultCxActionResultService.getActionResults(user, cv);
		Assert.assertThat(Integer.valueOf(outputActionResults.size()), CoreMatchers.equalTo(Integer.valueOf(1)));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getActionCode(), CoreMatchers.equalTo("test1"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getCustomizationCode(),
				CoreMatchers.equalTo("test1CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getVariationCode(),
				CoreMatchers.equalTo("test1VarCode"));

		timeService.resetTimeOffset();

		//results2 are stored in db...
		defaultCxActionResultService.storeActionResults(user, cv, results2);
		final Optional<CxResultsModel> storedResultsOptional = cxActionResultDao
				.findResultsByKey(defaultCxActionResultService.getResultsKey(user, cv));
		Assert.assertTrue(storedResultsOptional.isPresent());
		final CxResultsModel storedResults = storedResultsOptional.get();
		final List<CxAbstractActionResult> storedActionList = (List<CxAbstractActionResult>) serializationService
				.deserialize((byte[]) storedResults.getResults());
		Assert.assertTrue(storedActionList.size() == 1);
		Assert.assertEquals("test2", ((TestCxActionResultModel) storedActionList.get(0)).getActionCode());

		//...but we make them older...
		storedResults.setCalculationTime(DateUtils.addDays(storedResults.getCalculationTime(), -1));
		modelService.save(storedResults);

		//...so they should not be loaded in the session
		defaultCxActionResultService.loadActionResultsInSession(user, Arrays.asList(cv));
		outputActionResults = defaultCxActionResultService.getActionResults(user, cv);
		Assert.assertThat(Integer.valueOf(outputActionResults.size()), CoreMatchers.equalTo(Integer.valueOf(1)));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getActionCode(), CoreMatchers.equalTo("test1"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getCustomizationCode(),
				CoreMatchers.equalTo("test1CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getVariationCode(),
				CoreMatchers.equalTo("test1VarCode"));
	}

	@Test
	public void shouldSetAndClearResultsFromSession()
	{
		final UserModel user = userService.getUserForUID("defaultcxcustomer");
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final List<CxAbstractActionResult> inputActionResults = Arrays.asList(result1, result2);

		defaultCxActionResultService.setActionResultsInSession(user, cv, inputActionResults);
		final List<CxAbstractActionResult> outputActionResults = defaultCxActionResultService.getActionResults(user, cv);

		Assert.assertThat(Integer.valueOf(outputActionResults.size()), CoreMatchers.equalTo(Integer.valueOf(2)));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getActionCode(), CoreMatchers.equalTo("test1"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getCustomizationCode(),
				CoreMatchers.equalTo("test1CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(0)).getVariationCode(),
				CoreMatchers.equalTo("test1VarCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(1)).getActionCode(), CoreMatchers.equalTo("test2"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(1)).getCustomizationCode(),
				CoreMatchers.equalTo("test2CustCode"));
		Assert.assertThat(((TestCxActionResultModel) outputActionResults.get(1)).getVariationCode(),
				CoreMatchers.equalTo("test2VarCode"));

		defaultCxActionResultService.clearActionResultsInSession(user, cv);
		Assert.assertTrue(CollectionUtils.isEmpty(defaultCxActionResultService.getActionResults(user, cv)));
	}

	@Test
	public void shouldStoreResultsForAnonymousUser()
	{
		//given
		final UserModel user = userService.getAnonymousUser();
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final List<CxAbstractActionResult> inputActionResults = Arrays.asList(result1);

		//when
		defaultCxActionResultService.storeActionResults(user, cv, inputActionResults);

		//then
		final Optional<CxResultsModel> cxResultsOptional = cxActionResultDao
				.findResultsByKey(defaultCxActionResultService.getResultsKey(user, cv));
		Assert.assertTrue(cxResultsOptional.isPresent());
	}

	@Test
	public void shouldLoadResultsForAnonymousUser()
	{
		//given
		final UserModel user = userService.getAnonymousUser();
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		defaultCxActionResultService.storeActionResults(user, cv, Arrays.asList(result1));
		final Optional<CxResultsModel> cxResultsOptional = cxActionResultDao
				.findResultsByKey(defaultCxActionResultService.getResultsKey(user, cv));
		Assert.assertTrue(cxResultsOptional.isPresent());

		//when
		defaultCxActionResultService.loadActionResultsInSession(user, Arrays.asList(cv));

		//then
		final List<CxAbstractActionResult> outputActionResults = defaultCxActionResultService.getActionResults(user, cv);
		Assert.assertTrue(outputActionResults.size() == 1);
		final TestCxActionResultModel r1 = (TestCxActionResultModel) outputActionResults.get(0);
		Assert.assertEquals("test1", r1.getActionCode());
		Assert.assertEquals("test1CustCode", r1.getCustomizationCode());
		Assert.assertEquals("test1VarCode", r1.getVariationCode());
	}

	@Test
	public void shouldStoreDefaulResults()
	{
		//given
		final UserModel user = userService.getAnonymousUser();
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final List<CxAbstractActionResult> inputActionResults = Arrays.asList(result1);

		//when
		defaultCxActionResultService.storeDefaultActionResults(user, cv, inputActionResults);

		//then
		final Optional<CxResultsModel> cxResultsOptional = cxActionResultDao
				.findResultsByKey(defaultCxActionResultService.getDefaultResultsKey(user, cv));
		Assert.assertTrue(cxResultsOptional.isPresent());
	}

	@Test
	public void shouldLoadDefaultResults()
	{
		//given
		final UserModel user = userService.getAnonymousUser();
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		defaultCxActionResultService.storeDefaultActionResults(user, cv, Arrays.asList(result1));
		final Optional<CxResultsModel> cxResultsOptional = cxActionResultDao
				.findResultsByKey(defaultCxActionResultService.getDefaultResultsKey(user, cv));
		Assert.assertTrue(cxResultsOptional.isPresent());

		//when
		defaultCxActionResultService.loadActionResultsInSession(user, Arrays.asList(cv));

		//then
		final List<CxAbstractActionResult> outputActionResults = defaultCxActionResultService.getActionResults(user, cv);
		Assert.assertTrue(outputActionResults.size() == 1);
		final TestCxActionResultModel r1 = (TestCxActionResultModel) outputActionResults.get(0);
		Assert.assertEquals("test1", r1.getActionCode());
		Assert.assertEquals("test1CustCode", r1.getCustomizationCode());
		Assert.assertEquals("test1VarCode", r1.getVariationCode());
	}

	@Test
	public void shouldLoadNotDefaultResults()
	{
		//given
		final UserModel user = userService.getAnonymousUser();
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("testCatalog", "Online");

		defaultCxActionResultService.storeActionResults(user, cv, Arrays.asList(result1));
		Optional<CxResultsModel> cxResultsOptional = cxActionResultDao
				.findResultsByKey(defaultCxActionResultService.getResultsKey(user, cv));
		Assert.assertTrue(cxResultsOptional.isPresent());

		defaultCxActionResultService.storeDefaultActionResults(user, cv, Arrays.asList(result2));
		cxResultsOptional = cxActionResultDao.findResultsByKey(defaultCxActionResultService.getDefaultResultsKey(user, cv));
		Assert.assertTrue(cxResultsOptional.isPresent());


		//when
		defaultCxActionResultService.loadActionResultsInSession(user, Arrays.asList(cv));

		//then
		final List<CxAbstractActionResult> outputActionResults = defaultCxActionResultService.getActionResults(user, cv);
		Assert.assertTrue(outputActionResults.size() == 1);
		final TestCxActionResultModel r1 = (TestCxActionResultModel) outputActionResults.get(0);
		Assert.assertEquals("test1", r1.getActionCode());
		Assert.assertEquals("test1CustCode", r1.getCustomizationCode());
		Assert.assertEquals("test1VarCode", r1.getVariationCode());
	}

}

class TestCxActionResultModel extends CxAbstractActionResult
{
	//empty
}
