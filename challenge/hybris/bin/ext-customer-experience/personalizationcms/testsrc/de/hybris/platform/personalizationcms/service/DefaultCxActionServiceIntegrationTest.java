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
package de.hybris.platform.personalizationcms.service;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.personalizationcms.model.CxCmsActionModel;
import de.hybris.platform.personalizationcms.model.CxCmsComponentContainerModel;
import de.hybris.platform.personalizationservices.action.impl.DefaultCxActionService;
import de.hybris.platform.personalizationservices.enums.CxActionType;
import de.hybris.platform.personalizationservices.model.CxAbstractActionModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.enums.ActionType;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;


@IntegrationTest
public class DefaultCxActionServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String CONTAINER_CLEANUP_ENABLED_PROPERTY = "personalizationcms.containers.cleanup.enabled";

	private String containerCleanupEnabled;

	@Resource
	private DefaultCxActionService defaultCxActionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ConfigurationService configurationService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importData(
				new ClasspathImpExResource("/personalizationcms/test/testdata_personalizationcms_multiplecatalogs.impex", "UTF-8"));
		importData(new ClasspathImpExResource("/personalizationcms/test/testdata_personalizationcms_singlecatalog.impex", "UTF-8"));
		containerCleanupEnabled = (String) configurationService.getConfiguration().getProperty(CONTAINER_CLEANUP_ENABLED_PROPERTY);
		configurationService.getConfiguration().setProperty(CONTAINER_CLEANUP_ENABLED_PROPERTY, Boolean.TRUE.toString());
	}

	@After
	public void cleanup()
	{
		configurationService.getConfiguration().setProperty(CONTAINER_CLEANUP_ENABLED_PROPERTY, containerCleanupEnabled);
	}

	@Test
	public void shouldNotDeleteContainerOnLastActionRemovedForMultipleCatalogs()
	{
		//ensure the test data is there in the first place
		final CatalogVersionModel cv1 = catalogVersionService.getCatalogVersion("testCatalog1", "Online");
		final CatalogVersionModel cv2 = catalogVersionService.getCatalogVersion("testCatalog2", "Online");

		final CxCmsComponentContainerModel exampleContainer = new CxCmsComponentContainerModel();
		exampleContainer.setSourceId("container1");
		exampleContainer.setCatalogVersion(cv1);
		final CxCmsComponentContainerModel container = flexibleSearchService.getModelByExample(exampleContainer);
		Assert.assertNotNull(container);

		final CxCmsActionModel exampleAction = new CxCmsActionModel();
		exampleAction.setCode("cmsaction1");
		exampleAction.setCatalogVersion(cv2);
		final CxCmsActionModel action = flexibleSearchService.getModelByExample(exampleAction);
		Assert.assertNotNull(action);

		defaultCxActionService.deleteAction(action);

		assertExceptionThrown(ModelNotFoundException.class, () -> flexibleSearchService.getModelByExample(exampleAction));
		Assert.assertNotNull(flexibleSearchService.getModelByExample(exampleContainer));
	}

	@Test
	public void shouldDeleteContainerOnLastActionRemovedForMultipleCatalogs()
	{
		try
		{
			configurationService.getConfiguration().addProperty(
					"personalizationcms.containers.cleanup.cxcatalogtocmscatalog.testCatalog2.Online", "testCatalog1\\,Online");

			//ensure the test data is there in the first place
			final CatalogVersionModel cv1 = catalogVersionService.getCatalogVersion("testCatalog1", "Online");
			final CatalogVersionModel cv2 = catalogVersionService.getCatalogVersion("testCatalog2", "Online");

			final CxCmsComponentContainerModel exampleContainer = new CxCmsComponentContainerModel();
			exampleContainer.setSourceId("container1");
			exampleContainer.setCatalogVersion(cv1);
			final CxCmsComponentContainerModel container = flexibleSearchService.getModelByExample(exampleContainer);
			Assert.assertNotNull(container);

			final CxCmsActionModel exampleAction = new CxCmsActionModel();
			exampleAction.setCode("cmsaction1");
			exampleAction.setCatalogVersion(cv2);
			final CxCmsActionModel action = flexibleSearchService.getModelByExample(exampleAction);
			Assert.assertNotNull(action);

			defaultCxActionService.deleteAction(action);

			assertExceptionThrown(ModelNotFoundException.class, () -> flexibleSearchService.getModelByExample(exampleAction));
			assertExceptionThrown(ModelNotFoundException.class, () -> flexibleSearchService.getModelByExample(exampleContainer));
		}
		finally
		{
			configurationService.getConfiguration()
					.clearProperty("personalizationcms.containers.cleanup.cxcatalogtocmscatalog.testCatalog2.Online");
		}
	}

	@Test
	public void shouldDeleteContainerOnLastActionRemoved()
	{
		//ensure the test data is there in the first place
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("singleCatalog", "Online");

		final CxCmsComponentContainerModel exampleContainer = new CxCmsComponentContainerModel();
		exampleContainer.setSourceId("container1");
		exampleContainer.setCatalogVersion(cv);
		final CxCmsComponentContainerModel container = flexibleSearchService.getModelByExample(exampleContainer);
		Assert.assertNotNull(container);

		final CxCmsActionModel exampleAction = new CxCmsActionModel();
		exampleAction.setCode("cmsaction1");
		exampleAction.setCatalogVersion(cv);
		final CxCmsActionModel action = flexibleSearchService.getModelByExample(exampleAction);
		Assert.assertNotNull(action);

		defaultCxActionService.deleteAction(action);

		assertExceptionThrown(ModelNotFoundException.class, () -> flexibleSearchService.getModelByExample(exampleAction));
		assertExceptionThrown(ModelNotFoundException.class, () -> flexibleSearchService.getModelByExample(exampleContainer));
	}


	protected void assertExceptionThrown(final Class exceptionClass, final Supplier method)
	{
		Exception thrown = null;
		try
		{
			method.get();
		}
		catch (final Exception e)
		{
			thrown = e;
		}
		Assert.assertNotNull("No exception was thrown.", thrown);
		Assert.assertTrue("Exception thrown but not of the expected type.", exceptionClass.isAssignableFrom(thrown.getClass()));
	}

	protected CxVariationModel getVariation(final String code)
	{
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("singleCatalog", "Online");

		final CxVariationModel example = new CxVariationModel();
		example.setCode(code);
		example.setCatalogVersion(cv);
		return flexibleSearchService.getModelByExample(example);
	}

	protected List<CxVariationModel> getVariations(final String... codes)
	{
		final List<CxVariationModel> variations = new ArrayList();
		for (final String code : codes)
		{
			variations.add(getVariation(code));
		}
		return variations;
	}

	protected CxCmsActionModel buildAction(final String code)
	{
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("singleCatalog", "Online");

		final CxCmsActionModel action = new CxCmsActionModel();
		action.setCode(code);
		action.setComponentId("cxcomponent1");
		action.setContainerId("container1");
		action.setCatalogVersion(cv);

		return action;
	}

	protected void assertActionsAffectedObjectKey(final List<CxAbstractActionModel> actions, final String... codes)
	{
		Assert.assertNotNull(actions);
		final Set<String> actualCodes = actions.stream().map(CxAbstractActionModel::getAffectedObjectKey)
				.collect(Collectors.toSet());
		final Set<String> expectedCodes = Sets.newHashSet(codes);

		Assert.assertEquals(expectedCodes, actualCodes);
	}


	@Test(expected = IllegalArgumentException.class)
	public void actionsForVariationNullTest()
	{
		defaultCxActionService.getActionsForVariations(null);
	}

	@Test
	public void actionsForVariationTest()
	{
		//given
		final List<CxVariationModel> variations = getVariations("variation1", "variation2", "variation3", "variation4",
				"variation5", "variation6", "variation7");

		//when
		final List<CxAbstractActionModel> actions = defaultCxActionService.getActionsForVariations(variations);

		//then
		assertActionsAffectedObjectKey(actions, "container1_cxcomponent1", "container2_cxcomponent1", "container2_cxcomponent2");
		Assert.assertEquals(3, actions.size());
	}

	@Test
	public void shouldReturn1Action()
	{
		//given
		final List<CxVariationModel> variations = getVariations("variation1");

		//when
		final List<CxAbstractActionModel> actions = defaultCxActionService.getActionsForVariations(variations);

		//then
		assertActionsAffectedObjectKey(actions, "container1_cxcomponent1");
		Assert.assertEquals(1, actions.size());
	}

	@Test
	public void shouldReturn2ActionsFor1Variation()
	{
		//given
		final List<CxVariationModel> variations = getVariations("variation2");

		//when
		final List<CxAbstractActionModel> actions = defaultCxActionService.getActionsForVariations(variations);

		//then
		assertActionsAffectedObjectKey(actions, "container2_cxcomponent1", "container2_cxcomponent2");
		Assert.assertEquals(2, actions.size());
	}

	@Test
	public void shouldReturn1ActionFor1Variation()
	{
		//given
		final List<CxVariationModel> variations = getVariations("variation3");

		//when
		final List<CxAbstractActionModel> actions = defaultCxActionService.getActionsForVariations(variations);

		//then
		assertActionsAffectedObjectKey(actions, "container2_cxcomponent1");
		Assert.assertEquals(1, actions.size());
	}

	@Test
	public void shouldReturn2ActionsFor2Variant()
	{
		//given
		final List<CxVariationModel> variations = getVariations("variation4", "variation5");

		//when
		final List<CxAbstractActionModel> actions = defaultCxActionService.getActionsForVariations(variations);

		//then
		assertActionsAffectedObjectKey(actions, "container2_cxcomponent1", "container2_cxcomponent2");
		Assert.assertEquals(2, actions.size());

	}

	@Test
	public void shouldReturn1ActionFor2Variants()
	{
		//given
		final List<CxVariationModel> variations = getVariations("variation5", "variation6");

		//when
		final List<CxAbstractActionModel> actions = defaultCxActionService.getActionsForVariations(variations);

		//then
		assertActionsAffectedObjectKey(actions, "container2_cxcomponent2");
		Assert.assertEquals(1, actions.size());
	}

	@Test
	public void testCreateAction()
	{
		//given
		final CxVariationModel variation = getVariation("variation1");

		final CxCmsActionModel action = buildAction("a1");

		//when
		final CxAbstractActionModel createdAction = defaultCxActionService.createAction(action, variation);

		//then
		Assert.assertEquals("a1", createdAction.getCode());
		Assert.assertEquals(variation.getCatalogVersion(), createdAction.getCatalogVersion());
		Assert.assertEquals(ActionType.PLAIN, createdAction.getType());
		Assert.assertEquals(action.getTarget(), createdAction.getTarget());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateActionWitnNullParam()
	{
		//given
		final CxVariationModel variation = getVariation("variation1");

		//when
		defaultCxActionService.createAction(null, variation);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateActionWithoutCode()
	{
		//given
		final CxVariationModel variation = getVariation("variation1");

		final CxCmsActionModel action = buildAction(null);

		//when
		defaultCxActionService.createAction(action, variation);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateActionWithNullVariation()
	{
		//given
		final CxCmsActionModel action = buildAction("a1");

		//when
		defaultCxActionService.createAction(action, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateActionWithNullCatalogVersion()
	{
		//given
		final CxVariationModel variation = getVariation("variation1");
		variation.setCatalogVersion(null);
		final CxCmsActionModel action = buildAction("a1");
		action.setCatalogVersion(null);

		//when
		defaultCxActionService.createAction(action, variation);
	}

	@Test
	public void testGetCmsActions()
	{
		//given
		final SearchPageData<?> searchPageData = buildPagination(0, 10);
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("singleCatalog", "Online");
		final Map<String, String> searchCriteria = new HashMap<>();

		//when
		final SearchPageData<?> actual = defaultCxActionService.getActions(CxActionType.CXCMSACTION, cv, searchCriteria,
				searchPageData);

		//then
		Assert.assertNotNull(actual);
		Assert.assertNotNull(actual.getResults());
		Assert.assertEquals(8, actual.getResults().size());
	}

	@Test
	public void testGetCmsActionsWithCondition()
	{
		//given
		final SearchPageData<?> searchPageData = buildPagination(0, 10);
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("singleCatalog", "Online");
		final Map<String, String> searchCriteria = new HashMap<>();
		searchCriteria.put("variationName", "2");
		searchCriteria.put("componentId", "cxcomponent1");

		//when
		final SearchPageData<CxCmsActionModel> actual = defaultCxActionService.getActions(CxActionType.CXCMSACTION, cv,
				searchCriteria, searchPageData);

		//then
		Assert.assertNotNull(actual);
		Assert.assertNotNull(actual.getResults());
		Assert.assertEquals(1, actual.getResults().size());
		Assert.assertEquals("cmsaction2", actual.getResults().get(0).getCode());
	}

	@Test
	public void testGetCmsActionsForContainer()
	{
		//given
		final SearchPageData<?> searchPageData = buildPagination(0, 10);
		final CatalogVersionModel cv = catalogVersionService.getCatalogVersion("singleCatalog", "Online");
		final Map<String, String> searchCriteria = new HashMap<>();
		searchCriteria.put("containerId", "container2");

		//when
		final SearchPageData<CxCmsActionModel> actual = defaultCxActionService.getActions(CxActionType.CXCMSACTION, cv,
				searchCriteria, searchPageData);

		//then
		Assert.assertNotNull(actual);
		Assert.assertNotNull(actual.getResults());
		Assert.assertEquals(7, actual.getResults().size());
	}

	private SearchPageData<?> buildPagination(final int page, final int pageSize)
	{
		final SearchPageData<?> searchPageData = new SearchPageData<>();
		searchPageData.setPagination(new PaginationData());
		searchPageData.getPagination().setPageSize(pageSize);
		searchPageData.getPagination().setCurrentPage(page);
		searchPageData.getPagination().setNeedsTotal(true);
		return searchPageData;
	}

}
