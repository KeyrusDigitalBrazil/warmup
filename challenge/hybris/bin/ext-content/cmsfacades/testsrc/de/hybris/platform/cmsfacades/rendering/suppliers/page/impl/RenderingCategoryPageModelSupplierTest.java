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
package de.hybris.platform.cmsfacades.rendering.suppliers.page.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.servicelayer.data.CMSDataFactory;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RenderingCategoryPageModelSupplierTest
{
	private String VALID_CATEGORY_CODE = "validCategoryCode";
	private String INVALID_CATEGORY_CODE = "invalidCategoryCode";

	@Mock
	private CategoryPageModel categoryPageModel;
	@Mock
	private RestrictionData restrictionData;
	@Mock
	private CMSPageService cmsPageService;
	@Mock
	private CMSDataFactory cmsDataFactory;

	@InjectMocks
	private RenderingCategoryPageModelSupplier supplier;

	@Test
	public void shouldReturnPageForValidCategoryCode() throws CMSItemNotFoundException
	{
		// GIVEN
		when(cmsPageService.getPageForCategoryCode(VALID_CATEGORY_CODE)).thenReturn(categoryPageModel);

		// WHEN
		Optional<AbstractPageModel> result = supplier.getPageModel(VALID_CATEGORY_CODE);

		// THEN
		assertTrue(result.isPresent());
	}

	@Test
	public void shouldReturnEmptyForInvalidCategoryCode() throws CMSItemNotFoundException
	{
		// GIVEN
		when(cmsPageService.getPageForCategoryCode(INVALID_CATEGORY_CODE)).thenThrow(new CMSItemNotFoundException(""));

		// WHEN
		Optional<AbstractPageModel> result = supplier.getPageModel(INVALID_CATEGORY_CODE);

		// THEN
		assertFalse(result.isPresent());
	}

	@Test
	public void shouldReturnRestrictionDataForValidCategoryCode()
	{
		// GIVEN
		when(cmsDataFactory.createRestrictionData(any(), any(), any())).thenReturn(restrictionData);

		// WHEN
		Optional<RestrictionData> result = supplier.getRestrictionData(VALID_CATEGORY_CODE);

		// THEN
		verify(cmsDataFactory).createRestrictionData(VALID_CATEGORY_CODE, null, null);
		assertTrue(result.isPresent());
	}
}
