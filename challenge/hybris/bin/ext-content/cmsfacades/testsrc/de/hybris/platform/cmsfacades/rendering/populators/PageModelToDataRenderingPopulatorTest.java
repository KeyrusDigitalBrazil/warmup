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
package de.hybris.platform.cmsfacades.rendering.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.data.PageContentSlotData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PageModelToDataRenderingPopulatorTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String PAGE_UID = "some page uid";
	private final String PAGE_TITLE = "some page title";
	private final String PAGE_TEMPLATE = "page template";
	private final String PAGE_NAME = "some page name";
	private final String PAGE_TYPE = "some page type";

	@Mock
	private ContentSlotData slot1;

	@Mock
	private ContentSlotData slot2;

	@Mock
	private ContentSlotData slot3;

	@Mock
	private ContentSlotData slot4;

	@Mock
	private PageContentSlotData slot1Representation;

	@Mock
	private PageContentSlotData slot2Representation;

	@Mock
	private PageContentSlotData slot3Representation;

	@Mock
	private PageContentSlotData slot4Representation;

	@Mock
	private ContentSlotModel contentSlotModel1;

	@Mock
	private ContentSlotModel contentSlotModel2;

	@Mock
	private ContentSlotModel contentSlotModel3;

	@Mock
	private ContentSlotModel contentSlotModel4;

	@Mock
	private AbstractPageModel pageModel;

	@Mock
	private PageTemplateModel pageTemplate;

	@Mock
	private CMSPageService cmsPageService;

	@Mock
	private Converter<ContentSlotData, PageContentSlotData> contentSlotRenderingConverter;

	@InjectMocks
	private PageModelToDataRenderingPopulator pageRenderingPopulator;

	private AbstractPageData pageData;
	private Date TWO_YEARS_AGO;
	private Date ONE_YEAR_AGO;
	private Date ONE_YEAR_FROM_NOW;

	// --------------------------------------------------------------------------
	// Test Setup
	// --------------------------------------------------------------------------
	@Before
	public void setUp()
	{
		pageData = new AbstractPageData();

		when(pageModel.getDefaultPage()).thenReturn(false);
		when(pageModel.getUid()).thenReturn(PAGE_UID);
		when(pageModel.getName()).thenReturn(PAGE_NAME);
		when(pageModel.getTitle()).thenReturn(PAGE_TITLE);
		when(pageModel.getItemtype()).thenReturn(PAGE_TYPE);
		when(pageModel.getMasterTemplate()).thenReturn(pageTemplate);
		when(pageTemplate.getUid()).thenReturn(PAGE_TEMPLATE);

		// Slots
		when(cmsPageService.getContentSlotsForPage(pageModel)).thenReturn(Arrays.asList(slot1, slot2, slot3, slot4));
		when(slot1.getContentSlot()).thenReturn(contentSlotModel1);
		when(slot2.getContentSlot()).thenReturn(contentSlotModel2);
		when(slot3.getContentSlot()).thenReturn(contentSlotModel3);
		when(slot4.getContentSlot()).thenReturn(contentSlotModel4);

		when(contentSlotModel1.getActive()).thenReturn(true);
		when(contentSlotModel2.getActive()).thenReturn(true);
		when(contentSlotModel3.getActive()).thenReturn(true);
		when(contentSlotModel4.getActive()).thenReturn(true);

		// Converter
		when(contentSlotRenderingConverter.convert(slot1)).thenReturn(slot1Representation);
		when(contentSlotRenderingConverter.convert(slot2)).thenReturn(slot2Representation);
		when(contentSlotRenderingConverter.convert(slot3)).thenReturn(slot3Representation);
		when(contentSlotRenderingConverter.convert(slot4)).thenReturn(slot4Representation);

		// Sample times
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		ONE_YEAR_AGO = cal.getTime();
		cal.add(Calendar.YEAR, -1);
		TWO_YEARS_AGO = cal.getTime();
		cal.add(Calendar.YEAR, 3);
		ONE_YEAR_FROM_NOW= cal.getTime();
	}

	// --------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------
	@Test
	public void whenPopulatorIsCalled_ThenItPopulatesAllTheRequiredProperties()
	{
		// WHEN
		pageRenderingPopulator.populate(pageModel, pageData);

		// THEN
		assertThat(pageData.getUid(), is(PAGE_UID));
		assertThat(pageData.getLocalizedTitle(), is(PAGE_TITLE));
		assertThat(pageData.getTemplate(), is(PAGE_TEMPLATE));
		assertThat(pageData.getName(), is(PAGE_NAME));
		assertThat(pageData.getTypeCode(), is(PAGE_TYPE));
		assertThat(pageData.getDefaultPage(), is(false)); // By default it should be false

		assertThat(pageData.getContentSlots(),
				contains(slot1Representation, slot2Representation, slot3Representation, slot4Representation));
	}

	@Test
	public void givenDefaultPage_WhenPopulatorIsCalled_ThenItPopulatesThePageAsDefault()
	{
		// GIVEN
		makePageDefault();

		// WHEN
		pageRenderingPopulator.populate(pageModel, pageData);

		// THEN
		assertThat(pageData.getDefaultPage(), is(true));
	}

	@Test
	public void givenInactiveSlot_WhenPopulatorIsCalled_ThenItPopulatesTheSlotsWithoutTheInactiveOnes()
	{
		// GIVEN
		setSlotAsInactive(slot2);
		setContentSlotActivePeriod(slot3, TWO_YEARS_AGO, ONE_YEAR_FROM_NOW); // Still valid
		setContentSlotActivePeriod(slot4, TWO_YEARS_AGO, ONE_YEAR_AGO); // Not valid

		// WHEN
		pageRenderingPopulator.populate(pageModel, pageData);

		// THEN
		assertThat(pageData.getContentSlots(), contains(slot1Representation, slot3Representation));
	}

	// --------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------
	protected void makePageDefault()
	{
		when(pageModel.getDefaultPage()).thenReturn(true);
	}

	protected void setSlotAsInactive(ContentSlotData slotData)
	{
		when(slotData.getContentSlot().getActive()).thenReturn(false);
	}

	protected void setContentSlotActivePeriod(ContentSlotData slotData, Date activeFrom, Date activeUntil)
	{
		when(slotData.getContentSlot().getActiveFrom()).thenReturn(activeFrom);
		when(slotData.getContentSlot().getActiveUntil()).thenReturn(activeUntil);
	}
}
