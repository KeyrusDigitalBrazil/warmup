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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminRestrictionService;
import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.cmsfacades.page.DisplayCondition;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPageVariationResolverTest
{
	private static final String TYPE_CODE = "testTypeCode";
	private static final String UID = "testUid";

	@Mock
	private CMSAdminPageService adminPageService;
	@Mock
	private CMSAdminRestrictionService adminRestrictionService;
	@Mock
	private TypeService typeService;
	@InjectMocks
	@Spy
	private final DefaultPageVariationResolver resolver = new DefaultPageVariationResolver();

	@Mock
	private ComposedTypeModel composedType;
	@Mock
	private AbstractPageModel variationPageModel;
	@Mock
	private AbstractPageModel defaultPageModel;

	@Before
	public void setUp()
	{
		when(typeService.getComposedTypeForCode(TYPE_CODE)).thenReturn(composedType);

		when(defaultPageModel.getUid()).thenReturn(UID);
		when(defaultPageModel.getItemtype()).thenReturn(TYPE_CODE);
		when(defaultPageModel.getDefaultPage()).thenReturn(Boolean.TRUE);

		when(variationPageModel.getUid()).thenReturn(UID);
		when(variationPageModel.getItemtype()).thenReturn(TYPE_CODE);
		when(variationPageModel.getDefaultPage()).thenReturn(Boolean.FALSE);
	}

	@Test
	public void shouldFindAllDefaultPages()
	{
		when(adminPageService.findPagesByType(composedType, true)).thenReturn(Arrays.asList(defaultPageModel));

		final List<AbstractPageModel> results = resolver.findPagesByType(TYPE_CODE, true);

		verifyZeroInteractions(adminRestrictionService);
		verify(adminPageService, times(0)).findPagesByType(composedType, false);
		assertThat(results, iterableWithSize(1));
		assertThat(results, contains(defaultPageModel));
	}

	@Test
	public void shouldFindAllDefaultPages_ExistSingleUnrestrictedPage()
	{
		final AbstractPageModel unrestrictedPage = new AbstractPageModel();
		when(adminPageService.findPagesByType(composedType, true)).thenReturn(Collections.emptyList());
		when(adminPageService.findPagesByType(composedType, false)).thenReturn(Arrays.asList(unrestrictedPage));
		when(adminRestrictionService.getRestrictionsForPage(unrestrictedPage)).thenReturn(Collections.emptyList());

		final List<AbstractPageModel> results = resolver.findPagesByType(TYPE_CODE, true);
		assertThat(results, iterableWithSize(1));
		assertThat(results, contains(unrestrictedPage));
	}

	@Test
	public void shouldNotFindAllDefaultPages_NotExistSingleUnrestrictedPage()
	{
		final AbstractPageModel restrictedPage = new AbstractPageModel();
		when(adminPageService.findPagesByType(composedType, true)).thenReturn(Collections.emptyList());
		when(adminPageService.findPagesByType(composedType, false)).thenReturn(Arrays.asList(restrictedPage));
		when(adminRestrictionService.getRestrictionsForPage(restrictedPage))
		.thenReturn(Arrays.asList(new AbstractRestrictionModel()));

		final List<AbstractPageModel> results = resolver.findPagesByType(TYPE_CODE, true);
		assertThat(results, empty());
	}

	@Test
	public void shouldFindAllVariationPages()
	{
		final AbstractPageModel variationPage1 = new AbstractPageModel();
		final AbstractPageModel variationPage2 = new AbstractPageModel();
		when(adminPageService.findPagesByType(composedType, false))
			.thenReturn(Arrays.asList(variationPage1, variationPage2));

		final List<AbstractPageModel> results = resolver.findPagesByType(TYPE_CODE, false);

		verifyZeroInteractions(adminRestrictionService);
		verify(adminPageService).findPagesByType(composedType, false);
		assertThat(results, iterableWithSize(2));
		assertThat(results, containsInAnyOrder(variationPage1, variationPage2));
	}

	@Test
	public void shouldNotFindDefaultPagesForDefaultPageModel()
	{
		doReturn(Boolean.TRUE).when(resolver).isDefaultPage(defaultPageModel);

		final List<AbstractPageModel> results = resolver.findDefaultPages(defaultPageModel);

		assertThat(results, empty());
	}

	@Test
	public void shouldFindDefaultPagesForVariationPageModel()
	{
		doReturn(Boolean.FALSE).when(resolver).isDefaultPage(variationPageModel);
		when(resolver.findPagesByType(variationPageModel.getItemtype(), Boolean.TRUE)).thenReturn(Arrays.asList(defaultPageModel));

		final List<AbstractPageModel> results = resolver.findDefaultPages(variationPageModel);

		assertThat(results, iterableWithSize(1));
		assertThat(results.get(0), is(defaultPageModel));
	}

	@Test
	public void shouldFindVariationPagesForDefaultPageModel()
	{
		doReturn(Boolean.TRUE).when(resolver).isDefaultPage(defaultPageModel);
		when(resolver.findPagesByType(defaultPageModel.getItemtype(), Boolean.FALSE)).thenReturn(Arrays.asList(variationPageModel));

		final List<AbstractPageModel> results = resolver.findVariationPages(defaultPageModel);

		assertThat(results, iterableWithSize(1));
		assertThat(results.get(0), is(variationPageModel));
	}

	@Test
	public void shouldNotFindVariationPagesForVariationPageModel()
	{
		doReturn(Boolean.FALSE).when(resolver).isDefaultPage(variationPageModel);

		final List<AbstractPageModel> results = resolver.findVariationPages(variationPageModel);

		assertThat(results, empty());
	}

	@Test
	public void shouldBeDefaultPage()
	{
		final boolean isDefaultPage = resolver.isDefaultPage(defaultPageModel);
		assertTrue(isDefaultPage);
	}

	@Test
	public void shouldBeDefaultPage_NoDefaultPagesExist()
	{
		when(adminPageService.findPagesByType(composedType, true)).thenReturn(Collections.emptyList());

		final boolean isDefaultPage = resolver.isDefaultPage(defaultPageModel);
		assertTrue(isDefaultPage);
	}

	@Test
	public void shouldBeDefaultPage_MatchingUid()
	{
		final AbstractPageModel defaultPage = new AbstractPageModel();
		defaultPage.setUid(UID);

		when(adminPageService.findPagesByType(composedType, true)).thenReturn(Arrays.asList(defaultPage));

		final boolean isDefaultPage = resolver.isDefaultPage(defaultPageModel);
		assertTrue(isDefaultPage);
	}

	@Test
	public void shouldNotBeDefaultPage_NotMatchingUid()
	{
		final AbstractPageModel defaultPage = Mockito.mock(AbstractPageModel.class);
		when(defaultPage.getUid()).thenReturn("other-uid");
		when(defaultPage.getItemtype()).thenReturn(TYPE_CODE);
		when(defaultPageModel.getDefaultPage()).thenReturn(Boolean.FALSE);
		when(adminPageService.findPagesByType(composedType, true)).thenReturn(Arrays.asList(defaultPage));

		final boolean isDefaultPage = resolver.isDefaultPage(defaultPageModel);
		assertFalse(isDefaultPage);
	}

	@Test
	public void shouldNotBeDefaultPage()
	{
		final AbstractPageModel restrictedPage = Mockito.mock(AbstractPageModel.class);
		when(restrictedPage.getDefaultPage()).thenReturn(Boolean.FALSE);
		when(restrictedPage.getItemtype()).thenReturn(TYPE_CODE);
		when(adminPageService.findPagesByType(composedType, true)).thenReturn(Arrays.asList(defaultPageModel));

		final boolean isDefaultPage = resolver.isDefaultPage(restrictedPage);
		assertFalse(isDefaultPage);
	}

	@Test
	public void shouldFindDisplayConditions_PrimaryExists()
	{
		doReturn(Arrays.asList(defaultPageModel)).when(resolver).findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE);

		final List<OptionData> options = resolver.findDisplayConditions(ContentPageModel._TYPECODE);

		assertThat(options, iterableWithSize(1));
		assertThat(options.get(0).getId(), is(DisplayCondition.VARIATION.name()));
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
