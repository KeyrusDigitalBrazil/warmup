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
package de.hybris.platform.cmsfacades.synchronization.impl;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.common.itemcollector.ItemCollector;
import de.hybris.platform.cmsfacades.common.itemcollector.ItemCollectorRegistry;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.data.ItemSynchronizationData;
import de.hybris.platform.cmsfacades.data.SyncItemStatusConfig;
import de.hybris.platform.cmsfacades.data.SyncItemStatusData;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.data.SynchronizationData;
import de.hybris.platform.cmsfacades.data.SynchronizationItemDetailsData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.synchronization.service.ItemSynchronizationService;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultItemSynchronizationFacadeTest
{
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private ItemCollectorRegistry basicItemCollectorRegistry;
	@Mock
	private ItemCollectorRegistry sharedItemCollectorRegistry;
	@Mock
	private ItemCollectorRegistry dependentItemCollectorRegistry;
	@Mock
	private ItemSynchronizationService synchronizationStatusService;
	@Mock
	private Converter<SynchronizationItemDetailsData, SyncItemStatusData> syncItemStatusConverter;
	@Mock
	private FacadeValidationService facadeValidationService;
	@Mock
	private Validator catalogSynchronizationCompositeValidator;

	@Mock
	private SyncConfig defaultConfig;

	@Spy
	@InjectMocks
	private DefaultItemSynchronizationFacade synchronizationStatusFacade;

	@Captor
	private ArgumentCaptor<List<ItemModel>> itemListCaptor;

	@Mock
	private final SyncRequestData syncRequestData = mock(SyncRequestData.class);
	@Mock
	private SynchronizationData synchronizationData;

	@Mock
	private SynchronizationItemDetailsData syncPageDetailsData;
	@Mock
	private SynchronizationItemDetailsData syncSlot1DetailsData;
	@Mock
	private SynchronizationItemDetailsData syncSlot2DetailsData;
	@Mock
	private SynchronizationItemDetailsData syncSlot3DetailsData;
	@Mock
	private SynchronizationItemDetailsData syncSlot4DetailsData;

	@Mock
	private SyncItemStatusData syncPageStatusData;
	@Mock
	private SyncItemStatusData syncSlot1StatusData;
	@Mock
	private SyncItemStatusData syncSlot2StatusData;
	@Mock
	private SyncItemStatusData syncSlot3StatusData;
	@Mock
	private SyncItemStatusData syncSlot4StatusData;

	@Mock
	private ContentPageModel contentPage;
	@Mock
	private ContentPageModel primaryPage;

	@Mock
	private ContentSlotModel slot1;
	@Mock
	private ContentSlotModel slot2;
	@Mock
	private ContentSlotModel slot3;
	@Mock
	private ContentSlotModel slot4;

	@Mock
	private Errors errors;

	@Mock
	private ItemSynchronizationData pageItemData;
	private final String pageId = "pageId";
	private final String pageType = "pageType";

	@Mock
	private ItemSynchronizationData itemData2;
	private final String itemId2 = "itemId2";
	private final String itemType2 = "itemType2";

	@Mock
	private ItemSynchronizationData itemData3;
	private final String itemId3 = "itemId3";
	private final String itemType3 = "itemType3";

	private final ItemCollector contentPageItemCollector = mock(ItemCollector.class);
	private final Optional<ItemCollector> contentPageItemCollectorOptional = Optional.of(contentPageItemCollector);
	private final ItemCollector dependentPageItemCollector = mock(ItemCollector.class);
	private final Optional<ItemCollector> dependentPageItemCollectorOptional = Optional.of(dependentPageItemCollector);
	private final ItemCollector contentSlotItemCollector = mock(ItemCollector.class);
	private final Optional<ItemCollector> contentSlotItemCollectorOptional = Optional.of(contentSlotItemCollector);

	@Before
	public void setup()
	{
		final SyncItemStatusConfig config = new SyncItemStatusConfig();
		config.setMaxDepth(2);
		synchronizationStatusFacade.setSyncItemStatusConfig(config);

		// synchronizationStatusService mocks
		when(synchronizationStatusService.getSynchronizationItemStatus(syncRequestData, contentPage))
				.thenReturn(syncPageDetailsData);
		when(synchronizationStatusService.getSynchronizationItemStatus(syncRequestData, slot1)).thenReturn(syncSlot1DetailsData);
		when(synchronizationStatusService.getSynchronizationItemStatus(syncRequestData, slot2)).thenReturn(syncSlot2DetailsData);
		when(synchronizationStatusService.getSynchronizationItemStatus(syncRequestData, slot3)).thenReturn(syncSlot3DetailsData);
		when(synchronizationStatusService.getSynchronizationItemStatus(syncRequestData, slot4)).thenReturn(syncSlot4DetailsData);

		// syncItemStatusConverter mocks
		when(syncItemStatusConverter.convert(syncPageDetailsData)).thenReturn(syncPageStatusData);
		when(syncItemStatusConverter.convert(syncSlot1DetailsData)).thenReturn(syncSlot1StatusData);
		when(syncItemStatusConverter.convert(syncSlot2DetailsData)).thenReturn(syncSlot2StatusData);
		when(syncItemStatusConverter.convert(syncSlot3DetailsData)).thenReturn(syncSlot3StatusData);
		when(syncItemStatusConverter.convert(syncSlot4DetailsData)).thenReturn(syncSlot4StatusData);

		// basicItemCollectorRegistry mocks
		when(basicItemCollectorRegistry.getItemCollector(contentPage)).thenReturn(empty());
		when(basicItemCollectorRegistry.getItemCollector(slot1)).thenReturn(empty());
		when(basicItemCollectorRegistry.getItemCollector(slot2)).thenReturn(empty());
		when(basicItemCollectorRegistry.getItemCollector(slot3)).thenReturn(empty());
		when(basicItemCollectorRegistry.getItemCollector(slot4)).thenReturn(empty());

		// sharedItemCollectorRegistry mocks
		when(sharedItemCollectorRegistry.getItemCollector(contentPage)).thenReturn(empty());
		when(sharedItemCollectorRegistry.getItemCollector(slot1)).thenReturn(empty());
		when(sharedItemCollectorRegistry.getItemCollector(slot2)).thenReturn(empty());
		when(sharedItemCollectorRegistry.getItemCollector(slot3)).thenReturn(empty());
		when(sharedItemCollectorRegistry.getItemCollector(slot4)).thenReturn(empty());

		// dependentItemCollectorRegistry mocks
		when(dependentItemCollectorRegistry.getItemCollector(any())).thenReturn(empty());

		// contentPageItemCollector mocks
		when(contentPageItemCollector.collect(contentPage)).thenReturn(Arrays.asList(slot1, slot2, slot3, slot4));

		// contentSlotItemCollector mocks
		when(contentSlotItemCollector.collect(slot1)).thenReturn(Arrays.asList());

		// dependentPageItemCollector mocks
		when(dependentPageItemCollector.collect(contentPage)).thenReturn(Arrays.asList(primaryPage));

		when(synchronizationData.getItems()).thenReturn(asList(itemData2, itemData3));

		when(pageItemData.getItemId()).thenReturn(pageId);
		when(pageItemData.getItemType()).thenReturn(pageType);
		when(itemData2.getItemId()).thenReturn(itemId2);
		when(itemData2.getItemType()).thenReturn(itemType2);
		when(itemData3.getItemId()).thenReturn(itemId3);
		when(itemData3.getItemType()).thenReturn(itemType3);

		when(uniqueItemIdentifierService.getItemModel(any(ItemData.class))).thenAnswer(new Answer<Optional<ItemModel>>()
		{
			@Override
			public Optional<ItemModel> answer(final InvocationOnMock invocation) throws Throwable
			{
				final ItemData itemData = (ItemData) invocation.getArguments()[0];

				if (pageId.equals(itemData.getItemId()))
				{
					return of(contentPage);
				}
				else if (itemId2.equals(itemData.getItemId()))
				{
					return of(slot2);
				}
				else if (itemId3.equals(itemData.getItemId()))
				{
					return of(slot3);
				}
				return empty();
			}
		});
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testWhenDefaultMaxDepthIsNotGreaterThanZero()
	{
		synchronizationStatusFacade.getSyncItemStatusConfig().setMaxDepth(0);
		synchronizationStatusFacade.getSynchronizationItemStatus(syncRequestData, pageItemData);
	}

	@Test
	public void testWhenItemCollectorRegistriesReturnEmpty_shouldNotPopulateMoreThanOneLevel()
	{
		// no item collector for basic collector registry
		final SyncItemStatusData synchronizationItemStatus = synchronizationStatusFacade
				.getSynchronizationItemStatus(syncRequestData, pageItemData);
		verify(synchronizationStatusService).getSynchronizationItemStatus(syncRequestData, contentPage);
		verify(basicItemCollectorRegistry).getItemCollector(contentPage);
		verify(sharedItemCollectorRegistry).getItemCollector(contentPage);
		verify(dependentItemCollectorRegistry).getItemCollector(contentPage);
		verify(synchronizationItemStatus).setSelectedDependencies(newArrayList());
		verify(synchronizationItemStatus).setSharedDependencies(newArrayList());
		verify(synchronizationItemStatus).setUnavailableDependencies(newArrayList());
	}

	@Test
	public void testWhenCollectorReturnsSlots_shouldPopulateSelectedDependencies()
	{
		when(basicItemCollectorRegistry.getItemCollector(contentPage)).thenReturn(contentPageItemCollectorOptional);
		when(basicItemCollectorRegistry.getItemCollector(slot1)).thenReturn(contentSlotItemCollectorOptional);

		final SyncItemStatusData synchronizationItemStatus = synchronizationStatusFacade
				.getSynchronizationItemStatus(syncRequestData, pageItemData);

		verify(synchronizationStatusService).getSynchronizationItemStatus(syncRequestData, contentPage);
		verify(basicItemCollectorRegistry).getItemCollector(contentPage);
		verify(sharedItemCollectorRegistry).getItemCollector(contentPage);
		verify(dependentItemCollectorRegistry).getItemCollector(contentPage);
		verify(synchronizationItemStatus)
				.setSelectedDependencies(asList(syncSlot1StatusData, syncSlot2StatusData, syncSlot3StatusData, syncSlot4StatusData));
		verify(synchronizationItemStatus).setSharedDependencies(newArrayList());
		verify(synchronizationItemStatus).setUnavailableDependencies(newArrayList());
	}


	@Test
	public void testWhenCollectorReturnsSlots_shouldPopulateSharedDependencies()
	{
		when(sharedItemCollectorRegistry.getItemCollector(contentPage)).thenReturn(contentPageItemCollectorOptional);
		when(sharedItemCollectorRegistry.getItemCollector(slot1)).thenReturn(contentSlotItemCollectorOptional);

		final SyncItemStatusData synchronizationItemStatus = synchronizationStatusFacade
				.getSynchronizationItemStatus(syncRequestData, pageItemData);

		verify(synchronizationStatusService).getSynchronizationItemStatus(syncRequestData, contentPage);
		verify(basicItemCollectorRegistry).getItemCollector(contentPage);
		verify(sharedItemCollectorRegistry).getItemCollector(contentPage);
		verify(dependentItemCollectorRegistry).getItemCollector(contentPage);
		verify(synchronizationItemStatus).setSelectedDependencies(asList());
		verify(synchronizationItemStatus).setSharedDependencies(
				newArrayList(syncSlot1StatusData, syncSlot2StatusData, syncSlot3StatusData, syncSlot4StatusData));
		verify(synchronizationItemStatus).setUnavailableDependencies(newArrayList());
	}

	@Test
	public void testWhenCollectorReturnsParentPage_shouldPopulateUnavailableDependencies()
	{
		when(dependentItemCollectorRegistry.getItemCollector(contentPage)).thenReturn(dependentPageItemCollectorOptional);
		doReturn(newArrayList(syncPageStatusData)).when(synchronizationStatusFacade).findUnavailableDependencies(syncRequestData,
				contentPage, 1);
		when(syncPageStatusData.getLastSyncStatus()).thenReturn(null);

		final SyncItemStatusData synchronizationItemStatus = synchronizationStatusFacade
				.getSynchronizationItemStatus(syncRequestData, pageItemData);

		verify(synchronizationStatusService).getSynchronizationItemStatus(syncRequestData, contentPage);
		verify(basicItemCollectorRegistry).getItemCollector(contentPage);
		verify(sharedItemCollectorRegistry).getItemCollector(contentPage);
		verify(synchronizationStatusFacade).findUnavailableDependencies(syncRequestData, contentPage, 1);
		verify(synchronizationItemStatus).setSelectedDependencies(asList());
		verify(synchronizationItemStatus).setSharedDependencies(newArrayList());
		verify(synchronizationItemStatus).setUnavailableDependencies(newArrayList(syncPageStatusData));
	}

	@Test
	public void performItemSynchronizationWithConfigWillDelegateToSyncServiceWithConfig()
	{
		final SyncConfig syncConfig = Mockito.mock(SyncConfig.class);

		synchronizationStatusFacade.performItemSynchronization(syncRequestData, synchronizationData, syncConfig);

		verify(synchronizationStatusService, times(1)).performItemSynchronization(eq(syncRequestData), itemListCaptor.capture(),
				eq(syncConfig));

		assertThat(itemListCaptor.getValue(), equalTo(asList(slot2, slot3)));

	}

	@Test
	public void performItemSynchronizationWithoutConfigWillDelegateToSyncServiceWithDefaultConfig()
	{
		synchronizationStatusFacade.performItemSynchronization(syncRequestData, synchronizationData);

		verify(synchronizationStatusService, times(1)).performItemSynchronization(eq(syncRequestData), itemListCaptor.capture(),
				eq(defaultConfig));

		assertThat(itemListCaptor.getValue(), equalTo(asList(slot2, slot3)));

	}

	@Test(expected = ValidationException.class)
	public void shouldThrowValidationExceptionIfValidationFails() throws ValidationException
	{
		// GIVEN
		doThrow(new ValidationException(errors)).when(facadeValidationService).validate(any(), any());

		// WHEN
		synchronizationStatusFacade.performItemSynchronization(syncRequestData, synchronizationData);
	}

	@Test
	public void testWhenDependentPageNeverSyncWillFindUnavailableDependencies()
	{
		doReturn(newArrayList(syncPageStatusData)).when(synchronizationStatusFacade)
				.collectItemsAndGetSynchronizationItemStatus(any(), any(), any(), any(), any());
		when(syncPageStatusData.getLastSyncStatus()).thenReturn(null);

		final List<SyncItemStatusData> results = synchronizationStatusFacade.findUnavailableDependencies(syncRequestData,
				contentPage, 1);

		assertThat(results, not(empty()));
		assertThat(results, hasItem(syncPageStatusData));
	}

	@Test
	public void testWhenDependentPageSyncedWillFindEmptyUnavailableDependencies()
	{
		doReturn(newArrayList(syncPageStatusData)).when(synchronizationStatusFacade)
				.collectItemsAndGetSynchronizationItemStatus(any(), any(), any(), any(), any());
		when(syncPageStatusData.getLastSyncStatus()).thenReturn(1234567L);

		final List<SyncItemStatusData> results = synchronizationStatusFacade.findUnavailableDependencies(syncRequestData,
				contentPage, 1);

		assertThat(results, emptyIterable());
	}

}
