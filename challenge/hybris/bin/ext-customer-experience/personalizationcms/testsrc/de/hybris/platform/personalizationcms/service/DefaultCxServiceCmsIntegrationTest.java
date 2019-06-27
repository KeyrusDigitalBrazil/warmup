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
package de.hybris.platform.personalizationcms.service;


import static de.hybris.platform.personalizationservices.enums.CxItemStatus.ENABLED;
import static de.hybris.platform.servicelayer.enums.ActionType.PLAIN;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationcms.data.CxCmsActionResult;
import de.hybris.platform.personalizationcms.model.CxCmsActionModel;
import de.hybris.platform.personalizationservices.action.dao.CxActionResultDao;
import de.hybris.platform.personalizationservices.customization.CxCustomizationService;
import de.hybris.platform.personalizationservices.data.CxAbstractActionResult;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.model.CxDefaultTriggerModel;
import de.hybris.platform.personalizationservices.model.CxResultsModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessModel;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.personalizationservices.variation.CxVariationService;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionTokenService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.task.utils.NeedsTaskEngine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.awaitility.Awaitility;


@IntegrationTest
@NeedsTaskEngine
public class DefaultCxServiceCmsIntegrationTest extends ServicelayerTest
{
	private static final String USER_ID = "customer1@hybris.com";

	@Resource
	private CxService cxService;
	@Resource
	private UserService userService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private ModelService modelService;
	@Resource
	private CxVariationService cxVariationService;
	@Resource
	private CxCustomizationService cxCustomizationService;
	@Resource
	private ConfigurationService configurationService;
	@Resource
	private CxActionResultDao cxActionResultDao;
	@Resource
	private DefaultSessionTokenService defaultSessionTokenService;
	@Resource
	FlexibleSearchService flexibleSearchService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/personalizationcms/test/testdata_personalizationcms.impex", "utf-8");
	}


	@Test
	public void testPersonalizationCalculationSequence()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final UserModel user = userService.getUserForUID(USER_ID);

		//initial state : no results in session, no results in database
		List<CxAbstractActionResult> resultsFromSession = cxService.getActionResultsFromSession(user, catalogVersion);
		Assert.assertTrue(CollectionUtils.isEmpty(resultsFromSession));
		assertNoResultsInDatabase();

		//step 1 : calculation should be stored in database, but not in session
		cxService.calculateAndStorePersonalization(user, catalogVersion);
		resultsFromSession = cxService.getActionResultsFromSession(user, catalogVersion);
		Assert.assertTrue(CollectionUtils.isEmpty(resultsFromSession));
		assertResultStoredInDatabase();

		//step 2 : load the results in session
		cxService.loadPersonalizationInSession(user, Arrays.asList(catalogVersion));
		resultsFromSession = cxService.getActionResultsFromSession(user, catalogVersion);
		Assert.assertEquals(4, resultsFromSession.size());
		assertCxCmsActionResult("cxcomponent1", "container1", (CxCmsActionResult) resultsFromSession.get(0));
		assertCxCmsActionResult("cxcomponent2", "container2", (CxCmsActionResult) resultsFromSession.get(1));
		assertCxCmsActionResult("cxcomponent3", "container3", (CxCmsActionResult) resultsFromSession.get(2));
		assertCxCmsActionResult("cxcomponent4", "container4", (CxCmsActionResult) resultsFromSession.get(3));
		assertResultStoredInDatabase();

		//step 3 : clear session
		cxService.clearPersonalizationInSession(user, catalogVersion);
		resultsFromSession = cxService.getActionResultsFromSession(user, catalogVersion);
		Assert.assertTrue(CollectionUtils.isEmpty(resultsFromSession));
		assertResultStoredInDatabase();
	}

	@Test
	public void testPersonalizationCalculationSequenceSessionOnly()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final UserModel user = userService.getUserForUID(USER_ID);

		//initial state : no results in session, no results on user
		List<CxAbstractActionResult> resultsFromSession = cxService.getActionResultsFromSession(user, catalogVersion);
		Assert.assertTrue(CollectionUtils.isEmpty(resultsFromSession));
		assertNoResultsInDatabase();

		//calculate and load in session, no result is stored in database
		cxService.calculateAndLoadPersonalizationInSession(user, catalogVersion);
		resultsFromSession = cxService.getActionResultsFromSession(user, catalogVersion);
		Assert.assertEquals(4, resultsFromSession.size());
		assertCxCmsActionResult("cxcomponent1", "container1", (CxCmsActionResult) resultsFromSession.get(0));
		assertCxCmsActionResult("cxcomponent2", "container2", (CxCmsActionResult) resultsFromSession.get(1));
		assertCxCmsActionResult("cxcomponent3", "container3", (CxCmsActionResult) resultsFromSession.get(2));
		assertCxCmsActionResult("cxcomponent4", "container4", (CxCmsActionResult) resultsFromSession.get(3));

		assertNoResultsInDatabase();
	}

	@Test
	public void testPreviewPersonalizationCalculationSequence()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		final CxCustomizationModel cxCustomization = cxCustomizationService.getCustomization("customization1", catalogVersion)
				.get();
		final CxVariationModel cxVariation = cxVariationService.getVariation("variation1", cxCustomization).get();
		final UserModel user = userService.getUserForUID(USER_ID);

		//initial state : no results in session, no results on user
		List<CxAbstractActionResult> resultsFromSession = cxService.getActionResultsFromSession(user, catalogVersion);
		Assert.assertTrue(CollectionUtils.isEmpty(resultsFromSession));
		assertNoResultsInDatabase();

		//step 1 : preview only loads results in session but no result in database
		cxService.calculateAndLoadPersonalizationInSession(user, catalogVersion, Arrays.asList(cxVariation));
		resultsFromSession = cxService.getActionResultsFromSession(user, catalogVersion);
		Assert.assertTrue(CollectionUtils.isEmpty(user.getCxResults()));
		Assert.assertEquals(1, resultsFromSession.size());
		assertCxCmsActionResult("cxcomponent1", "container1", (CxCmsActionResult) resultsFromSession.get(0));
		assertNoResultsInDatabase();

		//step 2 : clear session
		cxService.clearPersonalizationInSession(user, catalogVersion);
		resultsFromSession = cxService.getActionResultsFromSession(user, catalogVersion);
		Assert.assertTrue(CollectionUtils.isEmpty(resultsFromSession));
	}



	protected void assertCxCmsActionResult(final String componentId, final String containerId,
			final CxCmsActionResult cxCmsActionResult)
	{
		Assert.assertEquals(componentId, cxCmsActionResult.getComponentId());
		Assert.assertEquals(containerId, cxCmsActionResult.getContainerId());
		Assert.assertNotNull(cxCmsActionResult.getCustomizationCode());
		Assert.assertNotNull(cxCmsActionResult.getVariationCode());
	}


	protected void assertResultStoredInDatabase()
	{
		final List<CxResultsModel> resultsList = cxActionResultDao
				.findResultsBySessionKey(defaultSessionTokenService.getOrCreateSessionToken());
		Assert.assertTrue(resultsList.size() == 1);
		final CxResultsModel cxResult = resultsList.iterator().next();
		Assert.assertThat(cxResult.getCatalogVersion().getVersion(), CoreMatchers.equalTo("Online"));
		Assert.assertThat(cxResult.getCatalogVersion().getCatalog().getId(), CoreMatchers.equalTo("testCatalog"));
		Assert.assertFalse(cxResult.isDefault());
		Assert.assertNotNull(cxResult.getResults());
	}

	protected void assertNoResultsInDatabase()
	{
		final List<CxResultsModel> resultsList = cxActionResultDao
				.findResultsBySessionKey(defaultSessionTokenService.getOrCreateSessionToken());
		Assert.assertTrue(resultsList.size() == 0);
	}

	@Test
	public void testUpdateCustomerExperienceAsync() throws InterruptedException
	{
		//given
		final UserModel user = userService.getUserForUID(USER_ID);
		configurationService.getConfiguration().setProperty("personalizationservices.calculation.process",
				"defaultPersonalizationCalculationProcess");
		assertNoResultsInDatabase();
		final List<CatalogVersionModel> catalogVersions = Arrays
				.asList(catalogVersionService.getCatalogVersion("testCatalog", "Online"));


		//when
		final List<CxPersonalizationProcessModel> processes = cxService.startPersonalizationCalculationProcesses(user,
				catalogVersions);

		//then
		Assert.assertNotNull(processes);
		Assert.assertEquals(1, processes.size());

		final CxPersonalizationProcessModel process = processes.get(0);
		final String processCode = process.getCode();

		//then
		Assert.assertNotNull(process);
		Awaitility.await().atMost(60, TimeUnit.SECONDS).until(new Callable<Boolean>()
		{
			@Override
			public Boolean call() throws Exception
			{
				Registry.setCurrentTenantByID("junit");
				final BusinessProcessService businessProcessService = Registry.getApplicationContext()
						.getBean("businessProcessService", BusinessProcessService.class);
				final ModelService modelService = Registry.getApplicationContext().getBean("modelService", ModelService.class);
				final BusinessProcessModel localProcess = businessProcessService.getProcess(processCode);
				modelService.refresh(localProcess);
				return Boolean.valueOf(ProcessState.SUCCEEDED.equals(localProcess.getState()));
			}
		});

		modelService.refresh(process);
		Assert.assertEquals(ProcessState.SUCCEEDED, process.getState());
		assertResultStoredInDatabase();
	}

	@Test
	public void testUpdateCustomerExperienceSync() throws InterruptedException
	{
		//given
		final UserModel user = userService.getUserForUID(USER_ID);
		assertNoResultsInDatabase();

		//when
		cxService.calculateAndStorePersonalization(user, catalogVersionService.getCatalogVersion("testCatalog", "Online"));
		cxService.loadPersonalizationInSession(user,
				Collections.singleton(catalogVersionService.getCatalogVersion("testCatalog", "Online")));

		//then
		assertResultStoredInDatabase();
	}

	@Test
	public void testCalculateAndStoreDefaultPersonalization()
	{
		//given
		createDefaultVariation();
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");

		//when
		cxService.calculateAndStoreDefaultPersonalization(Collections.singleton(catalogVersion));

		//then
		assertDefaultResultStoredInDatabase();
	}

	@Test
	public void testCalculateAndStoreDefaultPersonalizationWhenNoDefaultVariation()
	{
		//given
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");

		//when
		cxService.calculateAndStoreDefaultPersonalization(Collections.singleton(catalogVersion));

		//then
		assertDefaultResultStoredInDatabase();
	}

	@Test
	public void testLoadDefaultPersonalizationInSessionForAnonymous()
	{
		//given
		createDefaultVariation();
		final UserModel anonymousUser = userService.getAnonymousUser();
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		cxService.calculateAndStoreDefaultPersonalization(Collections.singleton(catalogVersion));
		assertDefaultResultStoredInDatabase();

		//when
		cxService.loadPersonalizationInSession(anonymousUser);

		//then
		final List<CxAbstractActionResult> resultsFromSession = cxService.getActionResultsFromSession(anonymousUser,
				catalogVersion);
		Assert.assertEquals(1, resultsFromSession.size());
		assertCxCmsActionResult("defaultComponent", "defaultContainer", (CxCmsActionResult) resultsFromSession.get(0));

	}

	@Test
	public void testLoadDefaultPersonalizationInSession()
	{
		//given
		createDefaultVariation();
		final UserModel user = userService.getUserForUID(USER_ID);
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		cxService.calculateAndStoreDefaultPersonalization(Collections.singleton(catalogVersion));
		assertDefaultResultStoredInDatabase();

		//when
		cxService.loadPersonalizationInSession(user);

		//then
		final List<CxAbstractActionResult> resultsFromSession = cxService.getActionResultsFromSession(user, catalogVersion);
		Assert.assertEquals(1, resultsFromSession.size());
		assertCxCmsActionResult("defaultComponent", "defaultContainer", (CxCmsActionResult) resultsFromSession.get(0));

	}

	@Test
	public void testNotLoadDefaultPersonalizationInSession()
	{
		//given
		createDefaultVariation();
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		cxService.calculateAndStoreDefaultPersonalization(Collections.singleton(catalogVersion));
		assertDefaultResultStoredInDatabase();

		final UserModel user = userService.getUserForUID(USER_ID);
		cxService.calculateAndStorePersonalization(user, catalogVersion);

		//when
		cxService.loadPersonalizationInSession(user);

		//then
		final List<CxAbstractActionResult> resultsFromSession = cxService.getActionResultsFromSession(user, catalogVersion);
		Assert.assertEquals(4, resultsFromSession.size());
		assertCxCmsActionResult("cxcomponent1", "container1", (CxCmsActionResult) resultsFromSession.get(0));
		assertCxCmsActionResult("cxcomponent2", "container2", (CxCmsActionResult) resultsFromSession.get(1));
		assertCxCmsActionResult("cxcomponent3", "container3", (CxCmsActionResult) resultsFromSession.get(2));
		assertCxCmsActionResult("cxcomponent4", "container4", (CxCmsActionResult) resultsFromSession.get(3));

	}

	protected void createDefaultVariation()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");

		CxCustomizationModel customization = new CxCustomizationModel();
		customization.setCode("customization1");
		customization = flexibleSearchService.getModelByExample(customization);

		final CxVariationModel defaultVariation = new CxVariationModel();
		defaultVariation.setCode("defaultVariation");
		defaultVariation.setName("defaultVariation");
		defaultVariation.setCatalogVersion(catalogVersion);
		defaultVariation.setCustomization(customization);
		defaultVariation.setStatus(ENABLED);
		modelService.save(defaultVariation);

		final CxDefaultTriggerModel trigger = new CxDefaultTriggerModel();
		trigger.setVariation(defaultVariation);
		trigger.setCode("defaultTrigger");
		trigger.setCatalogVersion(catalogVersion);
		defaultVariation.setTriggers(Collections.singleton(trigger));

		final CxCmsActionModel action = new CxCmsActionModel();
		action.setCode("defaultAction");
		action.setCatalogVersion(catalogVersion);
		action.setComponentId("defaultComponent");
		action.setContainerId("defaultContainer");
		action.setVariation(defaultVariation);
		action.setTarget("cxCmsActionPerformable");
		action.setType(PLAIN);

		defaultVariation.setActions(Collections.singletonList(action));

		modelService.saveAll(defaultVariation);
	}

	protected void assertDefaultResultStoredInDatabase()
	{
		final List<CxResultsModel> resultsList = cxActionResultDao
				.findResultsBySessionKey(defaultSessionTokenService.getOrCreateSessionToken());
		Assert.assertTrue(resultsList.size() == 1);
		final CxResultsModel cxResult = resultsList.iterator().next();
		Assert.assertThat(cxResult.getCatalogVersion().getVersion(), CoreMatchers.equalTo("Online"));
		Assert.assertThat(cxResult.getCatalogVersion().getCatalog().getId(), CoreMatchers.equalTo("testCatalog"));
		Assert.assertTrue(cxResult.isDefault());
		Assert.assertNotNull(cxResult.getResults());
	}


	public CxService getCxService()
	{
		return cxService;
	}

	public void setCxService(final CxService cxService)
	{
		this.cxService = cxService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}
