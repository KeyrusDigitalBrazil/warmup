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
package de.hybris.platform.cmsfacades.pagetypesrestrictiontypes.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.RestrictionTypeModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.data.PageTypeRestrictionTypeData;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPageTypeRestrictionTypeFacadeTest
{

	private static final String PAGE_TYPE_ID = "ContentPage";
	private static final String RESTRICTION_TYPE_ID_1 = "CMSTimeRestriction";
	private static final String RESTRICTION_TYPE_ID_2 = "CMSUserGroupRestriction";

	@InjectMocks
	private DefaultPageTypeRestrictionTypeFacade pageTypesRestrictionTypesFacade;

	@Mock
	private CMSAdminPageService adminPageService;
	@Mock
	private CMSPageTypeModel pageType;
	@Mock
	private RestrictionTypeModel restrictionType1;
	@Mock
	private RestrictionTypeModel restrictionType2;

	@Before
	public void setUp()
	{
		final List<CMSPageTypeModel> pageTypes = Arrays.asList(pageType);

		when(pageType.getCode()).thenReturn(PAGE_TYPE_ID);
		when(restrictionType1.getCode()).thenReturn(RESTRICTION_TYPE_ID_1);
		when(restrictionType2.getCode()).thenReturn(RESTRICTION_TYPE_ID_2);
		when(adminPageService.getAllPageTypes()).thenReturn(pageTypes);
		when(pageType.getRestrictionTypes()).thenReturn(Arrays.asList(restrictionType1, restrictionType2));
	}

	@Test
	public void shouldGetRestrictionTypesByPageType()
	{
		final List<PageTypeRestrictionTypeData> result = pageTypesRestrictionTypesFacade.getRestrictionTypesForAllPageTypes();
		assertThat(result.size(), is(2));
		assertThat(result.get(0).getPageType(), is(PAGE_TYPE_ID));
		assertThat(result.get(0).getRestrictionType(), is(RESTRICTION_TYPE_ID_1));
		assertThat(result.get(1).getPageType(), is(PAGE_TYPE_ID));
		assertThat(result.get(1).getRestrictionType(), is(RESTRICTION_TYPE_ID_2));
	}

}
