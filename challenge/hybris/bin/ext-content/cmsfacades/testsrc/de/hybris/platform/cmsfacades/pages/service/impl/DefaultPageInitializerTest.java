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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.ContentSlotNameModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPageInitializerTest
{
	@Captor
	private ArgumentCaptor<String> nameCaptor;

	@Captor
	private ArgumentCaptor<String> positionCaptor;

	@Mock
	private CMSAdminContentSlotService adminContentSlotService;

	@InjectMocks
	private DefaultPageInitializer pageInitializer;

	@Mock
	private AbstractPageModel page;

	@Mock
	private ContentSlotData contentSlotData1;

	@Mock
	private ContentSlotData contentSlotData3;

	@Mock
	private ContentSlotNameModel contentSlotNameModel1;

	@Mock
	private ContentSlotNameModel contentSlotNameModel2;

	@Mock
	private ContentSlotNameModel contentSlotNameModel3;

	@Mock
	private ContentSlotNameModel contentSlotNameModel4;

	@Mock
	private PageTemplateModel masterTemplate;

	@Before
	public void setUp() throws Exception
	{
		when(adminContentSlotService.getContentSlotsForPage(page)).thenReturn(Arrays.asList(contentSlotData1, contentSlotData3));
		when(contentSlotData1.getPosition()).thenReturn("position1");
		when(contentSlotData3.getPosition()).thenReturn("position3");

		when(page.getMasterTemplate()).thenReturn(masterTemplate);
		when(masterTemplate.getAvailableContentSlots()).thenReturn(
				Arrays.asList(contentSlotNameModel1, contentSlotNameModel2, contentSlotNameModel3, contentSlotNameModel4));
		when(contentSlotNameModel1.getName()).thenReturn("position1");
		when(contentSlotNameModel2.getName()).thenReturn("position2");
		when(contentSlotNameModel3.getName()).thenReturn("position3");
		when(contentSlotNameModel4.getName()).thenReturn("position4");
	}

	@Test
	public void initializeWillOnlyCreateAndAssociateSlotsToEmptyPositions()
	{
		pageInitializer.initialize(page);

		verify(adminContentSlotService, times(2)).createContentSlot(eq(page), eq(null), nameCaptor.capture(),
				positionCaptor.capture());

		assertThat(nameCaptor.getAllValues(), CoreMatchers.equalTo(Arrays.asList("position2", "position4")));
		assertThat(positionCaptor.getAllValues(), CoreMatchers.equalTo(Arrays.asList("position2", "position4")));
	}
}

