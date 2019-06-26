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
package de.hybris.platform.cmsfacades.cmsitems.predicates;


import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.model.components.CMSTabParagraphContainerModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminItemService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.common.predicate.IsNotDeletedPagePredicate;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSItemNameExistsPredicateTest
{

	@InjectMocks
	private CMSItemNameExistsPredicate predicate;

	@Mock
	private CMSAdminItemService cmsAdminItemService;

	@Mock
	private CMSAdminSiteService cmsAdminSiteService;

	@Mock
	private TypeService typeService;

	@Mock
	private List<Predicate<CMSItemModel>> filters;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private CatalogModel catalog;

	@Mock
	private ComposedTypeModel composedTypeModel;

	@Mock
	private CloneContextSameAsActiveCatalogVersionPredicate cloneContextSameAsActiveCatalogVersionPredicate;

	@Mock
	private IsNotDeletedPagePredicate isNotDeletedPagePredicate;

	private final CMSItemModel cmsItemModel = new CMSItemModel();

	@Mock
	private ComposedTypeModel abstractCmsComponentTypeModel;
	@Mock
	private ComposedTypeModel abstractCmsComponentContainerTypeModel;

	@Before
	public void setup()
	{
		when(typeService.getComposedTypeForCode(CMSItemModel._TYPECODE)).thenReturn(composedTypeModel);
		when(composedTypeModel.getAllSuperTypes()).thenReturn(Collections.emptyList());
		when(filters.stream()).thenReturn(Stream.empty());
		when(cmsAdminSiteService.getActiveCatalogVersion()).thenReturn(catalogVersion);
		when(catalogVersion.getCatalog()).thenReturn(catalog);
		when(cloneContextSameAsActiveCatalogVersionPredicate.test(cmsItemModel)).thenReturn(true);

		cmsItemModel.setName("name");
		cmsItemModel.setUid("uid");
	}

	@Test
	public void whenItemIsNotPresentInSearchShouldReturnFalse()
	{
		when(cmsAdminItemService.findByTypeCodeAndName(any(), any(), any())).thenReturn(new SearchResultImpl<>(asList(), 0, 50, 0));

		final boolean result = predicate.test(cmsItemModel);

		assertThat(result, is(false));
	}

	@Test
	public void whenItemIsPresentInSearchShouldReturnFalseWhenOnlyItselfMatch()
	{
		when(cmsAdminItemService.findByTypeCodeAndName(any(), any(), any())).thenReturn(new SearchResultImpl<>(asList(cmsItemModel), 1, 50, 0));

		final boolean result = predicate.test(cmsItemModel);

		assertThat(result, is(false));
	}

	@Test
	public void whenItemIsPresentInSearchShouldReturnFalseWhenOnlyOtherMatch()
	{
		final CMSItemModel otherCmsItemModel = new CMSItemModel();
		otherCmsItemModel.setName("name");
		otherCmsItemModel.setUid("other-uid");
		when(cmsAdminItemService.findByTypeCodeAndName(any(), any(), any())).thenReturn(new SearchResultImpl<>(asList(otherCmsItemModel), 1, 50, 0));

		final boolean result = predicate.test(cmsItemModel);

		assertThat(result, is(true));
	}

	@Test
	public void whenItemIsPresentInSearchShouldReturnTrueWhenOtherItemMatch()
	{
		final CMSItemModel otherCmsItemModel = new CMSItemModel();
		otherCmsItemModel.setName("name");
		otherCmsItemModel.setUid("other-uid");
		when(cmsAdminItemService.findByTypeCodeAndName(any(), any(), any())).thenReturn(new SearchResultImpl<>(asList(cmsItemModel, otherCmsItemModel), 2, 50, 0));

		final boolean result = predicate.test(cmsItemModel);

		assertThat(result, is(true));
	}

	@Test
	public void whenCloneContextSameAsActiveCatalogVersionPredicateReturnsFalseThenResultIsFalse()
	{
		// GIVEN
		when(cloneContextSameAsActiveCatalogVersionPredicate.test(cmsItemModel)).thenReturn(false);

		// WHEN
		final boolean result = predicate.test(cmsItemModel);

		// THEN
		assertFalse("CMSItemNameExistsPredicateTest should return false whenever clone context does not contain same catalog id and version as active catalog id and version", result);
	}

	@Test
	public void whenTypeHasNoAbstractParentShouldReturnSameTypeCode()
	{
		final String typeCode = predicate.findAbstractParentTypeCode(cmsItemModel);

		assertThat(typeCode, equalTo(cmsItemModel.getItemtype()));
	}

	@Test
	public void whenTypeHasAbstractParentShouldReturnParentTypeCode()
	{
		when(typeService.getComposedTypeForCode(CMSTabParagraphContainerModel._TYPECODE)).thenReturn(composedTypeModel);
		when(composedTypeModel.getAllSuperTypes())
		.thenReturn(Arrays.asList(abstractCmsComponentTypeModel, abstractCmsComponentContainerTypeModel));
		when(abstractCmsComponentTypeModel.getCode()).thenReturn(AbstractCMSComponentModel._TYPECODE);
		when(abstractCmsComponentContainerTypeModel.getCode()).thenReturn(AbstractCMSComponentContainerModel._TYPECODE);

		final String typeCode = predicate.findAbstractParentTypeCode(new CMSTabParagraphContainerModel());

		assertThat(typeCode, equalTo(AbstractCMSComponentModel._TYPECODE));
	}

	@Test
	public void whenFindItemByTypeCodeAndNameWithNoFilterShouldReturnAllResults()
	{
		final CMSParagraphComponentModel paragraphComponentModel = new CMSParagraphComponentModel();
		paragraphComponentModel.setName("name");
		paragraphComponentModel.setUid("other-uid");

		when(cmsAdminItemService.findByTypeCodeAndName(any(), any(), any()))
		.thenReturn(new SearchResultImpl<>(Collections.singletonList(paragraphComponentModel), 1, 50, 0));
		when(filters.stream()).thenReturn(Stream.of(isNotDeletedPagePredicate));
		when(isNotDeletedPagePredicate.test(paragraphComponentModel)).thenReturn(true);

		final List<CMSItemModel> results = predicate.findCMSItemByTypeCodeAndName(cmsItemModel.getName(),
				cmsItemModel.getItemtype());

		assertThat(results, hasItem(paragraphComponentModel));
	}

	@Test
	public void whenFindItemByTypeCodeAndNameWithFiltersShouldReturnEmptyResults()
	{
		final AbstractPageModel pageModel = new AbstractPageModel();
		pageModel.setName("name");
		pageModel.setUid("other-uid");
		pageModel.setPageStatus(CmsPageStatus.DELETED);

		when(cmsAdminItemService.findByTypeCodeAndName(any(), any(), any()))
		.thenReturn(new SearchResultImpl<>(Collections.singletonList(pageModel), 1, 50, 0));
		when(filters.stream()).thenReturn(Stream.of(isNotDeletedPagePredicate));
		when(isNotDeletedPagePredicate.test(pageModel)).thenReturn(false);

		final List<CMSItemModel> results = predicate.findCMSItemByTypeCodeAndName(cmsItemModel.getName(),
				cmsItemModel.getItemtype());

		assertThat(results, empty());
	}

}
