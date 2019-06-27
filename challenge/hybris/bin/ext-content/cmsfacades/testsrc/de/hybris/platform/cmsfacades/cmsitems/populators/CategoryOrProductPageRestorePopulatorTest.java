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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverType;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CategoryOrProductPageRestorePopulatorTest
{

	private static final String VALID_PAGE_UID = "page-uid";
	private static final String EXISTING_PRIMARY_PAGE_UID = "existing-primary-page-uid";

	@InjectMocks
	private CategoryOrProductPageRestorePopulator populator;

	@Mock
	private PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry;

	@Mock
	private Predicate<AbstractPageModel> pageCanOnlyHaveOnePrimaryPredicate;

	@Mock
	private PageVariationResolverType resolverType;

	@Mock
	private PageVariationResolver<AbstractPageModel> resolver;

	AbstractPageModel targetItem = new AbstractPageModel();
	AbstractPageModel existingPrimaryPage = new AbstractPageModel();

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
		populator.populate(null, targetItem);
	}

	@Test
	public void testShouldDoNothingWhenReplacePropertyIsNotProvided()
	{

		// GIVEN
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(AbstractPageModel.TYPECODE, CategoryPageModel._TYPECODE);
		sourceMap.put(AbstractPageModel.UID, VALID_PAGE_UID);

		// WHEN
		populator.populate(sourceMap, targetItem);

		// THEN
		verify(pageCanOnlyHaveOnePrimaryPredicate, never()).test(targetItem);
	}

	@Test
	public void testShouldDoNothingWhenReplacePropertyIsFalse()
	{

		// GIVEN
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(AbstractPageModel.TYPECODE, CategoryPageModel._TYPECODE);
		sourceMap.put(AbstractPageModel.UID, VALID_PAGE_UID);
		sourceMap.put(FIELD_PAGE_REPLACE, Boolean.FALSE);

		// WHEN
		populator.populate(sourceMap, targetItem);

		// THEN
		verify(pageCanOnlyHaveOnePrimaryPredicate, never()).test(targetItem);
	}

	@Test
	public void testShouldDoNothingWhenPageTyeCanHaveMoreThanOnePrimary()
	{

		// GIVEN
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(AbstractPageModel.TYPECODE, CategoryPageModel._TYPECODE);
		sourceMap.put(AbstractPageModel.UID, VALID_PAGE_UID);
		sourceMap.put(FIELD_PAGE_REPLACE, Boolean.TRUE);

		when(pageCanOnlyHaveOnePrimaryPredicate.test(targetItem)).thenReturn(Boolean.FALSE);

		// WHEN
		populator.populate(sourceMap, targetItem);

		// THEN
		verify(pageVariationResolverTypeRegistry, never()).getPageVariationResolverType(CategoryPageModel._TYPECODE);
	}

	@Test
	public void testShouldFindPrimaryPageAndMarkItDeletedWhenPageTypeCanHaveOnlyOnePrimary()
	{

		// GIVEN
		final Map<String, Object> sourceMap = new HashMap<>();
		sourceMap.put(AbstractPageModel.TYPECODE, CategoryPageModel._TYPECODE);
		sourceMap.put(AbstractPageModel.UID, VALID_PAGE_UID);
		sourceMap.put(FIELD_PAGE_REPLACE, Boolean.TRUE);
		sourceMap.put(AbstractPageModel.DEFAULTPAGE, Boolean.TRUE);

		when(pageCanOnlyHaveOnePrimaryPredicate.test(targetItem)).thenReturn(Boolean.TRUE);

		when(pageVariationResolverTypeRegistry.getPageVariationResolverType(CategoryPageModel._TYPECODE))
				.thenReturn(Optional.<PageVariationResolverType> of(resolverType));

		when(resolverType.getResolver()).thenReturn(resolver);

		existingPrimaryPage.setPageStatus(CmsPageStatus.ACTIVE);
		existingPrimaryPage.setUid(EXISTING_PRIMARY_PAGE_UID);
		when(resolver.findPagesByType(CategoryPageModel._TYPECODE, Boolean.TRUE)).thenReturn(Arrays.asList(existingPrimaryPage));

		// WHEN
		populator.populate(sourceMap, targetItem);

		// THEN
		assertThat(existingPrimaryPage.getPageStatus(), is(CmsPageStatus.DELETED));
	}

}
