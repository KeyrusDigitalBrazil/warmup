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
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RenderingContentPageModelSupplierTest
{
	private String LABEL_OR_ID = "LabelOrId";

	@Mock
	private CMSPageService cmsPageService;
	@Mock
	private CMSSiteService cmsSiteService;
	@Mock
	private ContentPageModel contentPageModel;
	@Mock
	private ContentPageModel homePageModel;
	@Mock
	private ContentPageModel defaultPageModel;
	@Mock
	private CMSSiteModel cmsSiteModel;

	@InjectMocks
	private RenderingContentPageModelSupplier supplier;

	@Test
	public void shouldReturnPageByLabelOrId() throws CMSItemNotFoundException
	{
		// GIVEN
		when(cmsPageService.getPageForLabelOrId(LABEL_OR_ID)).thenReturn(contentPageModel);

		// WHEN
		Optional<AbstractPageModel> result = supplier.getPageModel(LABEL_OR_ID);

		// THEN
		assertTrue(result.isPresent());
		assertThat(result.get(), is(contentPageModel));
	}

	@Test
	public void shouldReturnHomePageIfLabelOrIdNotFound() throws CMSItemNotFoundException
	{
		// GIVEN
		when(cmsPageService.getPageForLabelOrId(LABEL_OR_ID)).thenThrow(new CMSItemNotFoundException(""))
				.thenThrow(new CMSItemNotFoundException(""));
		when(cmsPageService.getHomepage()).thenReturn(homePageModel);

		// WHEN
		Optional<AbstractPageModel> result = supplier.getPageModel(LABEL_OR_ID);

		// THEN
		assertTrue(result.isPresent());
		assertThat(result.get(), is(homePageModel));
	}

	@Test
	public void shouldReturnPageByDefaultLabelOrIdLabelOrIdNotFoundAndIfHomePageNotFound() throws CMSItemNotFoundException
	{
		// GIVEN
		when(cmsPageService.getPageForLabelOrId(LABEL_OR_ID)).thenThrow(new CMSItemNotFoundException(""))
				.thenReturn(defaultPageModel);
		when(cmsPageService.getHomepage()).thenReturn(null);
		when(cmsSiteService.getCurrentSite()).thenReturn(cmsSiteModel);
		when(cmsSiteService.getStartPageLabelOrId(cmsSiteModel)).thenReturn(LABEL_OR_ID);

		// WHEN
		Optional<AbstractPageModel> result = supplier.getPageModel(LABEL_OR_ID);

		// THEN
		assertTrue(result.isPresent());
		assertThat(result.get(), is(defaultPageModel));
	}

	@Test
	public void shouldReturnEmptyIfPageDoesNotExists() throws CMSItemNotFoundException
	{
		// GIVEN
		when(cmsPageService.getPageForLabelOrId(any())).thenThrow(new CMSItemNotFoundException(""));
		when(cmsPageService.getHomepage()).thenReturn(null);

		// WHEN
		Optional<AbstractPageModel> result = supplier.getPageModel(LABEL_OR_ID);

		// THEN
		assertFalse(result.isPresent());
	}
}
