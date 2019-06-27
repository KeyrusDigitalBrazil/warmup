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
package de.hybris.platform.cmsfacades.synchronization.itemcollector;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.core.model.ItemModel;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SharedAbstractPageItemCollectorTest
{

	private static final String CONTENT_SLOT = "CONTENT_SLOT";
	private static final String SHARED_CONTENT_SLOT = "SHARED_CONTENT_SLOT";
	@Mock
	private CMSAdminContentSlotService contentSlotService;
	@InjectMocks
	private SharedAbstractPageItemCollector itemCollector;
	
	// attributes of this test
	@Mock
	private AbstractPageModel pageModel;
	@Mock
	private ContentSlotData contentSlotData;
	@Mock
	private ContentSlotData sharedContentSlotData;
	@Mock
	private ContentSlotModel contentSlot;
	@Mock
	private ContentSlotModel sharedContentSlot;
	@Mock
	private Predicate<String> contentSlotExistsPredicate;

	@Before
	public void setup()
	{
		// sharedContentSlotData mocks
		when(sharedContentSlotData.getUid()).thenReturn(SHARED_CONTENT_SLOT);
		// contentSlotData mocks
		when(contentSlotData.getUid()).thenReturn(CONTENT_SLOT);
		// contentSlotService mocks
		when(contentSlotService.getContentSlotsForPage(pageModel)).thenReturn(Arrays.asList(contentSlotData, sharedContentSlotData));

		when(contentSlotData.isFromMaster()).thenReturn(false);
		when(sharedContentSlotData.isFromMaster()).thenReturn(true);

		when(contentSlotService.getContentSlotForId(CONTENT_SLOT)).thenReturn(contentSlot);
		when(contentSlotService.getContentSlotForId(SHARED_CONTENT_SLOT)).thenReturn(sharedContentSlot);

		when(contentSlotExistsPredicate.test(anyString())).thenReturn(true);

		itemCollector.setContentSlotExistsPredicate(contentSlotExistsPredicate);
	}
	
	@Test
	public void testWhenPageHasManyContentSlots_shouldReturnOnlyOneContentSlot()
	{
		final List<ItemModel> collected = itemCollector.collect(pageModel);
		assertThat(collected, Matchers.notNullValue());
		assertThat(collected, Matchers.contains(sharedContentSlot));
	}

	@Test
	public void shouldNotReturnContentSlotFromParentCatalogVersion()
	{
		// GIVEN
		when(contentSlotExistsPredicate.test(SHARED_CONTENT_SLOT)).thenReturn(false);

		// WHEN
		final List<ItemModel> collected = itemCollector.collect(pageModel);

		// THEN
		assertThat(collected, Matchers.emptyIterable());
	}
	
}
