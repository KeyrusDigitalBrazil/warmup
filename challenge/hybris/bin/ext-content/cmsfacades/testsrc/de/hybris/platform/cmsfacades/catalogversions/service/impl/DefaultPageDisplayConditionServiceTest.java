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
package de.hybris.platform.cmsfacades.catalogversions.service.impl;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.data.DisplayConditionData;
import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverType;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPageDisplayConditionServiceTest
{
	private static final String OPTIONS = "options";
	private static final String TYPECODE = "typecode";

	@InjectMocks
	private DefaultPageDisplayConditionService displayConditionService;
	@Mock
	private CMSAdminPageService cmsAdminPageService;;
	@Mock
	private CMSAdminSiteService cmsAdminSiteService;
	@Mock
	private PageVariationResolverTypeRegistry cmsPageVariationResolverTypeRegistry;
	@Mock
	private PageVariationResolverType resolverType;
	@Mock
	private PageVariationResolver<AbstractPageModel> resolver;
	@Mock
	private Map<String, List<String>> cmsItemSearchTypeBlacklistMap;

	@Mock
	private DisplayConditionData displayConditionData;
	@Mock
	private OptionData optionData1;
	@Mock
	private OptionData optionData2;
	@Mock
	private OptionData optionData3;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private CMSPageTypeModel categoryPageType;
	@Mock
	private CMSPageTypeModel contentPageType;
	@Mock
	private CMSPageTypeModel productPageType;

	@Before
	public void setUp()
	{
		when(cmsItemSearchTypeBlacklistMap.get(AbstractPageModel._TYPECODE)).thenReturn(Arrays.asList(CategoryPageModel._TYPECODE));
		when(cmsAdminPageService.getAllPageTypes()).thenReturn(Arrays.asList(categoryPageType, contentPageType, productPageType));
		when(categoryPageType.getCode()).thenReturn(CategoryPageModel._TYPECODE);
		when(contentPageType.getCode()).thenReturn(ContentPageModel._TYPECODE);
		when(productPageType.getCode()).thenReturn(ProductPageModel._TYPECODE);
	}

	@Test
	public void shouldGetDisplayConditions()
	{
		when(cmsPageVariationResolverTypeRegistry.getPageVariationResolverType(ContentPageModel._TYPECODE))
				.thenReturn(Optional.<PageVariationResolverType> of(resolverType));
		when(cmsPageVariationResolverTypeRegistry.getPageVariationResolverType(ProductPageModel._TYPECODE))
				.thenReturn(Optional.<PageVariationResolverType> of(resolverType));
		when(resolverType.getResolver()).thenReturn(resolver);
		when(resolver.findDisplayConditions(ContentPageModel._TYPECODE)).thenReturn(Arrays.asList(optionData1, optionData2));
		when(resolver.findDisplayConditions(ProductPageModel._TYPECODE)).thenReturn(Arrays.asList(optionData3));

		final List<DisplayConditionData> displayConditions = displayConditionService.getDisplayConditions();

		assertThat(displayConditions, hasSize(2));

		assertThat(displayConditions, hasItem(allOf( //
				hasProperty(TYPECODE, equalTo(ContentPageModel._TYPECODE)),
				hasProperty(OPTIONS, contains(optionData1, optionData2)))));
		assertThat(displayConditions, hasItem(allOf( //
				hasProperty(TYPECODE, equalTo(ProductPageModel._TYPECODE)), hasProperty(OPTIONS, contains(optionData3)))));
	}

	@Test
	public void shouldGetDisplayConditionForTypeCode()
	{
		when(cmsPageVariationResolverTypeRegistry.getPageVariationResolverType(ContentPageModel._TYPECODE))
				.thenReturn(Optional.<PageVariationResolverType> of(resolverType));
		when(resolverType.getResolver()).thenReturn(resolver);
		when(resolver.findDisplayConditions(ContentPageModel._TYPECODE)).thenReturn(Arrays.asList(optionData1, optionData2));

		final List<OptionData> options = displayConditionService.getDisplayCondition(ContentPageModel._TYPECODE);

		assertThat(options, hasSize(2));
		assertThat(options, contains(optionData1, optionData2));
	}

	@Test
	public void shouldGetCatalogVersion_NoSupportedPageTypes() throws CMSItemNotFoundException
	{
		when(cmsAdminPageService.getAllPageTypes()).thenReturn(Collections.emptyList());

		final List<DisplayConditionData> displayConditions = displayConditionService.getDisplayConditions();

		assertThat(displayConditions, empty());
	}

}
