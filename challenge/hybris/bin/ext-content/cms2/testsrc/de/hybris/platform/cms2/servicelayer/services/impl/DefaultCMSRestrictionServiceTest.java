/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cms2.servicelayer.services.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.RestrictionEvaluationException;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSInverseRestrictionModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluatorRegistry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSRestrictionServiceTest
{
	private final String ITEM_TYPE = "itemType";

	@InjectMocks
	private DefaultCMSRestrictionService cmsRestrictionService;
	@Mock
	private CatalogLevelService cmsCatalogLevelService;
	@Mock
	private Comparator<AbstractPageModel> cmsAbstractPageCatalogLevelComparator;
	@Mock
	private ContentPageModel parentPrimaryPage;
	@Mock
	private ContentPageModel childPrimaryPage;
	@Mock
	private ContentPageModel childVariationPage;
	@Mock
	private CatalogVersionModel rootCatalogVersion;
	@Mock
	private ContentCatalogModel rootContentCatalog;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private ContentCatalogModel contentCatalog;
	@Mock
	private CMSRestrictionEvaluatorRegistry evaluatorRegistry;

	@Mock
	private AbstractPageModel abstractPage1;
	@Mock
	private AbstractPageModel abstractPage2;
	@Mock
	private CatalogModel catalog;
	@Mock
	private AbstractRestrictionModel abstractRestrictionModel;
	@Mock
	private CMSInverseRestrictionModel inverseRestrictionModel;
	@Mock
	private CMSRestrictionEvaluator restrictionEvaluator;


	@Before
	public void setUp()
	{
		when(parentPrimaryPage.getCatalogVersion()).thenReturn(rootCatalogVersion);
		when(childPrimaryPage.getCatalogVersion()).thenReturn(catalogVersion);
		when(childVariationPage.getCatalogVersion()).thenReturn(catalogVersion);
		when(rootCatalogVersion.getCatalog()).thenReturn(rootContentCatalog);
		when(catalogVersion.getCatalog()).thenReturn(contentCatalog);

		when(parentPrimaryPage.getLabelOrId()).thenReturn("parentPrimaryPage");
		when(childPrimaryPage.getLabelOrId()).thenReturn("childPrimaryPage");
		when(childVariationPage.getLabelOrId()).thenReturn("childVariationPage");

		when(cmsAbstractPageCatalogLevelComparator.compare(any(), any())).thenReturn(1);
	}

	@Test
	public void testEvaluateMultiCountryPrimaryPagesNotDefinedInContentCatalog()
	{
		when(abstractPage1.getDefaultPage()).thenReturn(TRUE);
		when(abstractPage2.getDefaultPage()).thenReturn(TRUE);
		when(abstractPage1.getCatalogVersion()).thenReturn(catalogVersion);
		when(abstractPage2.getCatalogVersion()).thenReturn(catalogVersion);
		when(catalogVersion.getCatalog()).thenReturn(catalog);

		final Collection<AbstractPageModel> results = cmsRestrictionService
				.getMultiCountryRestrictedPages(Arrays.asList(abstractPage1, abstractPage2));

		assertThat(results, containsInAnyOrder(abstractPage1, abstractPage2));
	}

	@Test
	public void testEvaluateMultiCountryVariationPages()
	{
		when(abstractPage1.getDefaultPage()).thenReturn(FALSE);
		when(abstractPage2.getDefaultPage()).thenReturn(FALSE);

		final Collection<AbstractPageModel> results = cmsRestrictionService
				.getMultiCountryRestrictedPages(Arrays.asList(abstractPage1, abstractPage2));

		assertThat(results, containsInAnyOrder(abstractPage1, abstractPage2));
	}

	@Test
	public void testEvaluateMultiCountryFindLeafLevelPage()
	{
		when(parentPrimaryPage.getDefaultPage()).thenReturn(TRUE);
		when(childPrimaryPage.getDefaultPage()).thenReturn(TRUE);
		when(childVariationPage.getDefaultPage()).thenReturn(FALSE);

		when(cmsCatalogLevelService.isTopLevel(rootContentCatalog)).thenReturn(TRUE);
		when(cmsCatalogLevelService.isTopLevel(contentCatalog)).thenReturn(FALSE);

		when(cmsCatalogLevelService.isIntermediateLevel(contentCatalog)).thenReturn(FALSE);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog)).thenReturn(1);
		when(cmsCatalogLevelService.isBottomLevel(contentCatalog)).thenReturn(TRUE);

		final Collection<AbstractPageModel> results = cmsRestrictionService
				.getMultiCountryRestrictedPages(Arrays.asList(parentPrimaryPage, childPrimaryPage, childVariationPage));

		assertThat(results, contains(childPrimaryPage));
	}

	@Test
	public void testEvaluateMultiCountryFindIntermediateLevelPage()
	{
		when(parentPrimaryPage.getDefaultPage()).thenReturn(TRUE);
		when(childPrimaryPage.getDefaultPage()).thenReturn(TRUE);
		when(childVariationPage.getDefaultPage()).thenReturn(FALSE);

		when(cmsCatalogLevelService.isTopLevel(rootContentCatalog)).thenReturn(TRUE);
		when(cmsCatalogLevelService.isTopLevel(contentCatalog)).thenReturn(FALSE);

		when(cmsCatalogLevelService.isIntermediateLevel(contentCatalog)).thenReturn(TRUE);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog)).thenReturn(1);
		when(cmsCatalogLevelService.isBottomLevel(contentCatalog)).thenReturn(FALSE);

		final Collection<AbstractPageModel> results = cmsRestrictionService
				.getMultiCountryRestrictedPages(Arrays.asList(parentPrimaryPage, childPrimaryPage, childVariationPage));

		assertThat(results, contains(childPrimaryPage));
	}

	@Test
	public void testEvaluateMultiCountryFindIntermediateSameLevelPage()
	{
		when(parentPrimaryPage.getDefaultPage()).thenReturn(TRUE);
		when(childPrimaryPage.getDefaultPage()).thenReturn(TRUE);
		when(childVariationPage.getDefaultPage()).thenReturn(FALSE);

		when(cmsCatalogLevelService.isTopLevel(rootContentCatalog)).thenReturn(TRUE);
		when(cmsCatalogLevelService.isTopLevel(contentCatalog)).thenReturn(FALSE);

		when(cmsCatalogLevelService.isIntermediateLevel(contentCatalog)).thenReturn(TRUE);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog)).thenReturn(0);
		when(cmsCatalogLevelService.isBottomLevel(contentCatalog)).thenReturn(FALSE);

		final Collection<AbstractPageModel> results = cmsRestrictionService
				.getMultiCountryRestrictedPages(Arrays.asList(parentPrimaryPage, childPrimaryPage, childVariationPage));

		assertThat(results, containsInAnyOrder(parentPrimaryPage, childPrimaryPage, childVariationPage));
	}

	@Test(expected = RestrictionEvaluationException.class)
	public void shouldThrowExceptionWhenNoEvaluatorFound() throws RestrictionEvaluationException
	{
		// GIVEN
		when(abstractRestrictionModel.getItemtype()).thenReturn(ITEM_TYPE);
		when(evaluatorRegistry.getCMSRestrictionEvaluator(any())).thenReturn(null);

		// WHEN
		cmsRestrictionService.evaluate(abstractRestrictionModel, null);
	}

	@Test
	public void shouldReturnTrueWhenRestrictionIsMatched() throws RestrictionEvaluationException
	{
		// GIVEN
		when(abstractRestrictionModel.getItemtype()).thenReturn(ITEM_TYPE);
		when(evaluatorRegistry.getCMSRestrictionEvaluator(any())).thenReturn(restrictionEvaluator);
		when(restrictionEvaluator.evaluate(abstractRestrictionModel, null)).thenReturn(true);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(abstractRestrictionModel, null);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldReturnFalseWhenRestrictionIsNotMatched() throws RestrictionEvaluationException
	{
		// GIVEN
		when(abstractRestrictionModel.getItemtype()).thenReturn(ITEM_TYPE);
		when(evaluatorRegistry.getCMSRestrictionEvaluator(any())).thenReturn(restrictionEvaluator);
		when(restrictionEvaluator.evaluate(abstractRestrictionModel, null)).thenReturn(false);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(abstractRestrictionModel, null);

		// THEN
		assertFalse(result);
	}

	@Test
	public void shouldReturnFalseWhenRestrictionIsMatchedButInverted() throws RestrictionEvaluationException
	{
		// GIVEN
		when(inverseRestrictionModel.getOriginalRestriction()).thenReturn(abstractRestrictionModel);
		when(abstractRestrictionModel.getItemtype()).thenReturn(ITEM_TYPE);
		when(evaluatorRegistry.getCMSRestrictionEvaluator(any())).thenReturn(restrictionEvaluator);
		when(restrictionEvaluator.evaluate(abstractRestrictionModel, null)).thenReturn(true);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(inverseRestrictionModel, null);

		// THEN
		assertFalse(result);
	}

	@Test
	public void shouldReturnTrueWhenRestrictionIsNotMatchedButInverted() throws RestrictionEvaluationException
	{
		// GIVEN
		when(inverseRestrictionModel.getOriginalRestriction()).thenReturn(abstractRestrictionModel);
		when(abstractRestrictionModel.getItemtype()).thenReturn(ITEM_TYPE);
		when(evaluatorRegistry.getCMSRestrictionEvaluator(any())).thenReturn(restrictionEvaluator);
		when(restrictionEvaluator.evaluate(abstractRestrictionModel, null)).thenReturn(false);

		// WHEN
		final boolean result = cmsRestrictionService.evaluate(inverseRestrictionModel, null);

		// THEN
		assertTrue(result);
	}
}
