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
import de.hybris.platform.cms2.model.pages.CatalogPageModel;
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
public class RenderingCatalogPageModelSupplierTest
{
	private String VALID_CATALOG_CODE = "validCatalogCode";
	private String INVALID_CATALOG_CODE = "invalidCatalogCode";

	@Mock
	private CatalogPageModel catalogPageModel;
	@Mock
	private RestrictionData restrictionData;
	@Mock
	private CMSPageService cmsPageService;
	@Mock
	private CMSDataFactory cmsDataFactory;

	@InjectMocks
	private RenderingCatalogPageModelSupplier supplier;

	@Test
	public void shouldReturnPageForValidCatalogCode() throws CMSItemNotFoundException
	{
		// GIVEN
		when(cmsPageService.getPageForCatalogId(VALID_CATALOG_CODE)).thenReturn(catalogPageModel);

		// WHEN
		Optional<AbstractPageModel> result = supplier.getPageModel(VALID_CATALOG_CODE);

		// THEN
		assertTrue(result.isPresent());
	}

	@Test
	public void shouldReturnEmptyForInvalidCatalogCode() throws CMSItemNotFoundException
	{
		// GIVEN
		when(cmsPageService.getPageForCatalogId(INVALID_CATALOG_CODE)).thenThrow(new CMSItemNotFoundException(""));

		// WHEN
		Optional<AbstractPageModel> result = supplier.getPageModel(INVALID_CATALOG_CODE);

		// THEN
		assertFalse(result.isPresent());
	}

	@Test
	public void shouldReturnRestrictionDataForValidCatalogCode()
	{
		// GIVEN
		when(cmsDataFactory.createRestrictionData(any(), any(), any())).thenReturn(restrictionData);

		// WHEN
		Optional<RestrictionData> result = supplier.getRestrictionData(VALID_CATALOG_CODE);

		// THEN
		verify(cmsDataFactory).createRestrictionData(null, null, VALID_CATALOG_CODE);
		assertTrue(result.isPresent());
	}
}
