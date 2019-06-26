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
package de.hybris.platform.cmsfacades.cmsitems.populators;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_PAGE_REPLACE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverType;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RestoreContentPagePopulatorTest
{

	private static final String VALID_PAGE_LABEL = "page-label";

	@InjectMocks
	private RestoreContentPagePopulator populator;

	@Mock
	private Predicate<String> primaryPageWithLabelExistsPredicate;

	@Mock
	private PageVariationResolver<ContentPageModel> pageVariationResolver;

	@Mock
	PageVariationResolverType pageVariationResolverType;

	ContentPageModel contentPage = new ContentPageModel();

	@Test(expected = ConversionException.class)
	public void testWhenItemModelIsNull_should_ThrowException()
	{
		// WHEN
		populator.populate(null, null);
	}

	@Test(expected = ConversionException.class)
	public void testWhenMapIsNull_should_ThrowException()
	{
		// WHEN
		populator.populate(null, new ItemModel());
	}

	@Test
	public void testShouldDoNothingWhenReplacePropertyIsNotProvided()
	{

		// GIVEN
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(ContentPageModel.LABEL, VALID_PAGE_LABEL);

		// WHEN
		populator.populate(sourceMap, new ItemModel());

		// THEN
		verify(pageVariationResolver, never()).findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE);
	}

	@Test
	public void testShouldDoNothingWhenReplacePropertyIsFalse()
	{

		// GIVEN
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(ContentPageModel.LABEL, VALID_PAGE_LABEL);
		sourceMap.put(FIELD_PAGE_REPLACE, Boolean.FALSE);

		// WHEN
		populator.populate(sourceMap, new ItemModel());

		// THEN
		verify(pageVariationResolver, never()).findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE);
	}

	@Test
	public void testShouldDoNothingWhenReplacePropertyIsTrueAndNoPrimaryWithLabelExists()
	{

		// GIVEN
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(ContentPageModel.LABEL, VALID_PAGE_LABEL);
		sourceMap.put(FIELD_PAGE_REPLACE, Boolean.TRUE);

		when(primaryPageWithLabelExistsPredicate.test(VALID_PAGE_LABEL)).thenReturn(Boolean.FALSE);

		// WHEN
		populator.populate(sourceMap, new ItemModel());

		// THEN
		verify(pageVariationResolver, never()).findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE);
	}

	@Test
	public void testShouldFindPrimaryPageAndMarkItDeletedWhenReplacePropertyIsTrueAndAPrimaryWithLabelExists()
	{

		// GIVEN
		contentPage.setLabel(VALID_PAGE_LABEL);
		contentPage.setPageStatus(CmsPageStatus.ACTIVE);
		contentPage.setDefaultPage(Boolean.TRUE);

		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(ContentPageModel.LABEL, VALID_PAGE_LABEL);
		sourceMap.put(FIELD_PAGE_REPLACE, Boolean.TRUE);

		when(primaryPageWithLabelExistsPredicate.test(VALID_PAGE_LABEL)).thenReturn(Boolean.TRUE);
		when(pageVariationResolver.findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE))
				.thenReturn(Arrays.asList(contentPage));

		// WHEN
		populator.populate(sourceMap, new ItemModel());

		// THEN
		verify(pageVariationResolver, times(1)).findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE);
		assertThat(contentPage.getPageStatus(), is(CmsPageStatus.DELETED));
	}

	@Test
	public void givenPageWillReplaceAnExistingHomePage_WhenPopulated_ItMustSwapTheExistingHomepage()
	{
		// GIVEN
		configureContentPage(CmsPageStatus.ACTIVE, /* default page */ Boolean.TRUE, /* homepage */ Boolean.TRUE);
		setCurrentPageAsDuplicate();

		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(ContentPageModel.LABEL, VALID_PAGE_LABEL);
		sourceMap.put(FIELD_PAGE_REPLACE, Boolean.TRUE);

		final ContentPageModel currentContentPage = new ContentPageModel();

		// WHEN
		populator.populate(sourceMap, currentContentPage);

		// THEN
		assertThat(currentContentPage.isHomepage(), is(true));
	}

	@Test
	public void givenPageWillReplaceAnExistingPage_WhenPopulated_ItMustNotSwapTheExistingHomepage()
	{
		// GIVEN
		configureContentPage(CmsPageStatus.ACTIVE, /* default page */ Boolean.TRUE, /* homepage */ Boolean.FALSE);
		setCurrentPageAsDuplicate();

		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(ContentPageModel.LABEL, VALID_PAGE_LABEL);
		sourceMap.put(FIELD_PAGE_REPLACE, Boolean.TRUE);

		final ContentPageModel currentContentPage = new ContentPageModel();

		// WHEN
		populator.populate(sourceMap, currentContentPage);

		// THEN
		assertThat(currentContentPage.isHomepage(), is(false));
	}

	// ---------------------------------------------------------------------------------------------------
	// Helper Methods
	// ---------------------------------------------------------------------------------------------------
	protected void configureContentPage(final CmsPageStatus pageStatus, final boolean isDefaultPage, final boolean isHomepage)
	{
		contentPage.setLabel(VALID_PAGE_LABEL);
		contentPage.setPageStatus(pageStatus);
		contentPage.setDefaultPage(isDefaultPage);
		contentPage.setHomepage(isHomepage);
	}

	protected void setCurrentPageAsDuplicate()
	{
		when(primaryPageWithLabelExistsPredicate.test(VALID_PAGE_LABEL)).thenReturn(Boolean.TRUE);
		when(pageVariationResolver.findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE))
				.thenReturn(Collections.singletonList(contentPage));
	}

}
