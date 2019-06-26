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
package de.hybris.platform.cms2.servicelayer.services.admin.impl;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.RestrictionTypeModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSRestrictionDao;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;
import java.util.Collections;

import org.fest.assertions.Assertions;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultCMSAdminRestrictionServiceTest
{

	private static final String CMS_Page_Type = "CategoryPage";

	@Spy
	@InjectMocks
	private final DefaultCMSAdminRestrictionService restrictionService = new DefaultCMSAdminRestrictionService();

	@Mock
	private TypeService typeService;
	@Mock
	private CMSRestrictionDao cmsRestrictionDao;

	@Mock
	private CMSPageTypeModel cmsPageType;
	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private RestrictionTypeModel restrictionTypeModel1;
	@Mock
	private RestrictionTypeModel restrictionTypeModel2;
	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private AbstractRestrictionModel restriction1;
	@Mock
	private AbstractRestrictionModel restriction2;
	@Mock
	private AbstractRestrictionModel restriction3;

	private CategoryPageModel categoryPage;

	@Before
	public void setUp()
	{
		categoryPage = new CategoryPageModel();

		doReturn(catalogVersion).when(restrictionService).getActiveCatalogVersion();

		given(typeService.getComposedTypeForClass(CategoryPageModel.class)).willReturn(cmsPageType);
		given(cmsPageType.getCode()).willReturn(CMS_Page_Type);
		given(cmsPageType.getRestrictionTypes()).willReturn(Collections.singletonList(restrictionTypeModel1));

		when(typeService.getComposedTypeForClass(AbstractRestrictionModel.class)).thenReturn(composedTypeModel);
		when(composedTypeModel.getAllSubTypes()).thenReturn(Lists.newArrayList(restrictionTypeModel1, restrictionTypeModel2));
	}

	@Test
	public void shouldGetAllowedRestrictionTypesForPage()
	{
		final Collection<RestrictionTypeModel> result = restrictionService.getAllowedRestrictionTypesForPage(categoryPage);

		Assertions.assertThat(result).containsOnly(restrictionTypeModel1);
		Assertions.assertThat(result).isNotEmpty();
		Assertions.assertThat(result).isNotNull();
	}


	@Test(expected = IllegalArgumentException.class)
	public void testForNullPage()
	{
		restrictionService.getAllowedRestrictionTypesForPage((AbstractPageModel) null);
	}

	@Test
	public void shouldGetAllowedRestrictionsForPage_MultipleAllowedTypes() throws CMSItemNotFoundException
	{
		given(cmsPageType.getRestrictionTypes()).willReturn(Lists.newArrayList(restrictionTypeModel1, restrictionTypeModel2));
		given(cmsRestrictionDao.findRestrictionsForPage(restrictionTypeModel1, categoryPage, catalogVersion))
				.willReturn(Lists.newArrayList(restriction1, restriction2));
		given(cmsRestrictionDao.findRestrictionsForPage(restrictionTypeModel2, categoryPage, catalogVersion))
				.willReturn(Lists.newArrayList(restriction3));

		final Collection<AbstractRestrictionModel> restrictions = restrictionService.getRestrictionsForPage(categoryPage);
		assertThat(restrictions.size(), is(3));
		assertThat(restrictions, Matchers.containsInAnyOrder(restriction1, restriction2, restriction3));
	}

	@Test
	public void shouldGetAllowedRestrictionsForPage_SingleAllowedType() throws CMSItemNotFoundException
	{
		given(cmsPageType.getRestrictionTypes()).willReturn(Lists.newArrayList(restrictionTypeModel1));
		given(cmsRestrictionDao.findRestrictionsForPage(restrictionTypeModel1, categoryPage, catalogVersion))
				.willReturn(Lists.newArrayList(restriction1, restriction2));

		final Collection<AbstractRestrictionModel> restrictions = restrictionService.getRestrictionsForPage(categoryPage);
		assertThat(restrictions.size(), is(2));
		assertThat(restrictions, Matchers.containsInAnyOrder(restriction1, restriction2));
	}

	@Test
	public void shouldGetAllowedRestrictionsForPage_NoAllowedType() throws CMSItemNotFoundException
	{
		given(cmsPageType.getRestrictionTypes()).willReturn(Collections.emptyList());

		final Collection<AbstractRestrictionModel> restrictions = restrictionService.getRestrictionsForPage(categoryPage);
		assertThat(restrictions, empty());
	}

	@Test
	public void shouldGetAllRestrictionTypes()
	{
		final Collection<RestrictionTypeModel> restrictionTypeModels = restrictionService.getAllRestrictionTypes();
		assertThat(restrictionTypeModels, Matchers.containsInAnyOrder(restrictionTypeModel1, restrictionTypeModel2));
	}
}
