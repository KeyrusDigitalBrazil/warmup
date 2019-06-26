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
package de.hybris.platform.cmsfacades.pagescontentslots.impl;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.relations.CMSRelationModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.data.PageContentSlotData;
import de.hybris.platform.cmsfacades.pagescontentslots.converter.ContentSlotDataConverter;
import de.hybris.platform.cmsfacades.pagescontentslots.service.PageContentSlotConverterRegistry;
import de.hybris.platform.cmsfacades.pagescontentslots.service.PageContentSlotConverterType;
import de.hybris.platform.cmsfacades.pagescontentslots.service.impl.DefaultPageContentSlotConverterType;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPageContentSlotFacadeTest
{
	private static final String INVALID = "invalid";
	private static final String SLOT_ID = "header-slot";
	private static final String PAGE_ID = "content-page";

	@Mock
	private CMSAdminContentSlotService adminContentSlotService;
	@Mock
	private CMSAdminPageService adminPageService;
	@Mock
	private PageContentSlotConverterRegistry pageContentSlotConverterRegistry;
	@Mock
	private ContentSlotModel headerSlot;
	@Mock
	private AbstractPageModel page;
	@Mock
	private ContentSlotData headerSlotData;
	@Mock
	private ContentSlotData footerSlotData;
	@Mock
	private PageContentSlotData pageContentSlotData;
	@Mock
	private AbstractPopulatingConverter<CMSRelationModel, PageContentSlotData> converter;
	@Mock
	private ContentSlotDataConverter contentSlotDataConverter;

	@InjectMocks
	private DefaultPageContentSlotFacade pageContentSlotFacade;

	private final PageContentSlotConverterType converterType = new DefaultPageContentSlotConverterType();

	@Before
	public void setUp()
	{
		when(adminPageService.getPageForIdFromActiveCatalogVersion(INVALID))
		.thenThrow(new UnknownIdentifierException("invalid page id"));
		when(adminPageService.getPageForIdFromActiveCatalogVersion(PAGE_ID)).thenReturn(page);
		when(adminContentSlotService.getContentSlotForId(INVALID)).thenThrow(new UnknownIdentifierException("invalid slot id"));
		when(adminContentSlotService.getContentSlotForId(SLOT_ID)).thenReturn(headerSlot);
		when(pageContentSlotConverterRegistry.getPageContentSlotConverterType(ContentSlotForPageModel.class))
		.thenReturn(Optional.of(converterType));
		when(pageContentSlotConverterRegistry.getPageContentSlotConverterType(ContentSlotForTemplateModel.class))
		.thenReturn(Optional.of(converterType));
		when(pageContentSlotConverterRegistry.getPageContentSlotConverterType(CustomRelationModel.class))
		.thenReturn(Optional.of(converterType));
		converterType.setConverter(converter);
	}

	@Test
	public void shouldFindContentSlotsForPage() throws CMSItemNotFoundException, ConversionException
	{
		when(adminContentSlotService.getContentSlotsForPage(page)).thenReturn(Arrays.asList(headerSlotData, footerSlotData));
		when(contentSlotDataConverter.convert(headerSlotData)).thenReturn(pageContentSlotData);
		when(contentSlotDataConverter.convert(footerSlotData)).thenReturn(pageContentSlotData);

		final List<PageContentSlotData> contentSlots = pageContentSlotFacade.getContentSlotsByPage(PAGE_ID);

		assertThat(contentSlots.size(), is(2));
		assertThat(contentSlots, hasItem(pageContentSlotData));
	}

	@Test
	public void shouldFindNoContentSlotForPage() throws CMSItemNotFoundException, ConversionException
	{
		when(adminContentSlotService.getContentSlotsForPage(page)).thenReturn(Collections.emptyList());

		final List<PageContentSlotData> contentSlots = pageContentSlotFacade.getContentSlotsByPage(PAGE_ID);

		assertThat(contentSlots, empty());
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldNotFindPageId() throws CMSItemNotFoundException, ConversionException
	{
		pageContentSlotFacade.getContentSlotsByPage(INVALID);
	}

	@Test(expected = ConversionException.class)
	public void shouldFailConversion() throws CMSItemNotFoundException, ConversionException
	{
		when(adminContentSlotService.getContentSlotsForPage(page)).thenReturn(Arrays.asList(headerSlotData, footerSlotData));
		when(contentSlotDataConverter.convert(headerSlotData)).thenThrow(new ConversionException("conversion failure"));

		pageContentSlotFacade.getContentSlotsByPage(PAGE_ID);
	}

	class CustomRelationModel extends ContentSlotForPageModel
	{
		public CustomRelationModel()
		{
			super();
		}
	}
}
