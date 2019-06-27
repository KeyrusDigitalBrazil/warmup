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
package de.hybris.platform.cmsfacades.pages.service.impl;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminRestrictionService;
import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.cmsfacades.page.DisplayCondition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ContentPageVariationResolverTest
{
	private static final String UID = "testUid";
	private static final String LABEL = "/testLabel";
	private static final String OTHER_LABEL = "/otherLabel";

	@Mock
	private CMSAdminPageService adminPageService;
	@Mock
	private CMSAdminRestrictionService adminRestrictionService;
	@InjectMocks
	@Spy
	private final ContentPageVariationResolver resolver = new ContentPageVariationResolver();

	@Mock
	private CatalogVersionModel catalogVersion;
	private ContentPageModel defaultPageModel;
	private ContentPageModel variationPageModel;
	private ContentPageModel secondVariationPageModel;
	private ContentPageModel otherDefaultPageModel;
	private ContentPageModel otherVariationPageModel;
	private List<ContentPageModel> allContentPages;

	@Before
	public void setUp()
	{
		defaultPageModel = new ContentPageModel();
		defaultPageModel.setUid(UID);
		defaultPageModel.setDefaultPage(Boolean.TRUE);
		defaultPageModel.setLabel(LABEL);

		variationPageModel = new ContentPageModel();
		variationPageModel.setUid(UID);
		variationPageModel.setDefaultPage(Boolean.FALSE);
		variationPageModel.setLabel(LABEL);

		secondVariationPageModel = new ContentPageModel();
		secondVariationPageModel.setUid(UID);
		secondVariationPageModel.setDefaultPage(Boolean.FALSE);
		secondVariationPageModel.setLabel(LABEL);

		otherDefaultPageModel = new ContentPageModel();
		otherDefaultPageModel.setUid(UID);
		otherDefaultPageModel.setDefaultPage(Boolean.TRUE);
		otherDefaultPageModel.setLabel(OTHER_LABEL);

		otherVariationPageModel = new ContentPageModel();
		otherVariationPageModel.setUid(UID);
		otherVariationPageModel.setDefaultPage(Boolean.FALSE);
		otherVariationPageModel.setLabel(OTHER_LABEL);

		allContentPages = Arrays.asList(defaultPageModel, variationPageModel, secondVariationPageModel, otherVariationPageModel,
				otherDefaultPageModel);
		when(adminPageService.getAllContentPages(Arrays.asList(catalogVersion))).thenReturn(allContentPages);
		when(adminPageService.getActiveCatalogVersion()).thenReturn(catalogVersion);
	}


	@Test
	public void shouldFindAllDefaultPages()
	{
		final List<ContentPageModel> results = resolver.findDefaultPages(allContentPages);

		assertThat(results, iterableWithSize(2));
		assertThat(results, containsInAnyOrder(defaultPageModel, otherDefaultPageModel));
	}

	@Test
	public void shouldFindAllDefaultPages_ExistSingleUnrestrictedPage()
	{
		allContentPages = Arrays.asList(defaultPageModel, variationPageModel, secondVariationPageModel, otherVariationPageModel);
		when(adminRestrictionService.getRestrictionsForPage(otherVariationPageModel)).thenReturn(Collections.emptyList());

		final List<ContentPageModel> results = resolver.findDefaultPages(allContentPages);

		assertThat(results, iterableWithSize(2));
		assertThat(results, containsInAnyOrder(defaultPageModel, otherVariationPageModel));
	}

	@Test
	public void shouldNotFindAllDefaultPages_NotExistSingleUnrestrictedPage()
	{
		otherDefaultPageModel.setDefaultPage(Boolean.FALSE);
		when(adminRestrictionService.getRestrictionsForPage(otherDefaultPageModel))
		.thenReturn(Arrays.asList(new AbstractRestrictionModel()));

		final List<ContentPageModel> results = resolver.findDefaultPages(allContentPages);

		assertThat(results, iterableWithSize(1));
		assertThat(results, containsInAnyOrder(defaultPageModel));

	}

	@Test
	public void shouldFindAllVariationPages()
	{
		doReturn(Arrays.asList(defaultPageModel, otherDefaultPageModel)).when(resolver).findDefaultPages(allContentPages);

		final List<ContentPageModel> results = resolver.findPagesByType(ContentPageModel._TYPECODE, Boolean.FALSE);

		assertThat(results, iterableWithSize(3));
		assertThat(results, containsInAnyOrder(variationPageModel, secondVariationPageModel, otherVariationPageModel));
	}

	@Test
	public void shouldNotFindDefaultPagesForDefaultPageModel()
	{
		doReturn(Boolean.TRUE).when(resolver).isDefaultPage(defaultPageModel);

		final List<ContentPageModel> results = resolver.findDefaultPages(defaultPageModel);

		assertThat(results, empty());
	}

	@Test
	public void shouldFindDefaultPagesForVariationPageModel()
	{
		doReturn(Boolean.FALSE).when(resolver).isDefaultPage(variationPageModel);
		when(resolver.findDefaultPages(allContentPages)).thenReturn(Arrays.asList(defaultPageModel));

		final List<ContentPageModel> results = resolver.findDefaultPages(variationPageModel);

		assertThat(results, iterableWithSize(1));
		assertThat(results.get(0), is(defaultPageModel));
	}

	@Test
	public void shouldFindVariationPagesForDefaultPageModel()
	{
		doReturn(Boolean.TRUE).when(resolver).isDefaultPage(defaultPageModel);
		when(resolver.findPagesByType(defaultPageModel.getItemtype(), Boolean.FALSE))
		.thenReturn(Arrays.asList(variationPageModel, secondVariationPageModel, otherVariationPageModel));
		final List<ContentPageModel> results = resolver.findVariationPages(defaultPageModel);

		assertThat(results, iterableWithSize(2));
		assertThat(results, containsInAnyOrder(variationPageModel, secondVariationPageModel));
	}

	@Test
	public void shouldNotFindVariationPagesForVariationPageModel()
	{
		doReturn(Boolean.FALSE).when(resolver).isDefaultPage(variationPageModel);

		final List<ContentPageModel> results = resolver.findVariationPages(variationPageModel);

		assertThat(results, empty());
	}

	@Test
	public void shouldBeDefaultPage()
	{
		final boolean isDefaultPage = resolver.isDefaultPage(defaultPageModel);
		assertTrue(isDefaultPage);
	}

	@Test
	public void shouldBeDefaultPage_UnrestrictedDefaultPage()
	{
		doReturn(Arrays.asList(defaultPageModel, otherVariationPageModel)).when(resolver).findDefaultPages(allContentPages);

		final boolean isDefaultPage = resolver.isDefaultPage(otherVariationPageModel);
		assertTrue(isDefaultPage);
	}

	@Test
	public void shouldNotBeDefaultPage()
	{
		doReturn(Arrays.asList(defaultPageModel, otherDefaultPageModel)).when(resolver).findDefaultPages(allContentPages);

		final boolean isDefaultPage = resolver.isDefaultPage(variationPageModel);
		assertFalse(isDefaultPage);
	}

	@Test
	public void shouldFindDisplayConditions_PrimaryExists()
	{
		doReturn(Arrays.asList(defaultPageModel)).when(resolver).findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE);

		final List<OptionData> options = resolver.findDisplayConditions(ContentPageModel._TYPECODE);

		assertThat(options, iterableWithSize(2));
		assertThat(options.get(0).getId(), is(DisplayCondition.PRIMARY.name()));
		assertThat(options.get(1).getId(), is(DisplayCondition.VARIATION.name()));
	}

	@Test
	public void shouldFindDisplayConditions_NoPrimary()
	{
		doReturn(Collections.emptyList()).when(resolver).findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE);

		final List<OptionData> options = resolver.findDisplayConditions(ContentPageModel._TYPECODE);

		assertThat(options, iterableWithSize(1));
		assertThat(options.get(0).getId(), is(DisplayCondition.PRIMARY.name()));
	}
}
