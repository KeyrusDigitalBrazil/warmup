/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.widgets.actions.locking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.locking.ItemLockingService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;


public class ToggleItemLockActionTest extends AbstractActionUnitTest<ToggleItemLockAction>
{
	@Spy
	@InjectMocks
	private ToggleItemLockAction toggleItemLockAction;

	@Mock
	private ItemLockingService itemLockingService;
	@Mock
	private NotificationService notificationService;

	@Mock
	private ItemModel lockedModel1;
	@Mock
	private ItemModel lockedModel2;
	@Mock
	private ItemModel unlockedModel1;
	@Mock
	private ItemModel unlockedModel2;

	@Override
	public ToggleItemLockAction getActionInstance()
	{
		return toggleItemLockAction;
	}

	@Before
	public void setUp()
	{
		when(itemLockingService.isLocked(lockedModel1)).thenReturn(Boolean.TRUE);
		when(itemLockingService.isLocked(lockedModel2)).thenReturn(Boolean.TRUE);
		when(itemLockingService.isLocked(unlockedModel1)).thenReturn(Boolean.FALSE);
		when(itemLockingService.isLocked(unlockedModel2)).thenReturn(Boolean.FALSE);
		doNothing().when(toggleItemLockAction).publishGlobalNotificationOnLockingStateChange(any());
		doNothing().when(toggleItemLockAction).showNotifications(any(), (ItemModel) any());
		doNothing().when(toggleItemLockAction).showNotifications(any(), (Collection<?>) any());
	}

	@Test
	public void canPerformWhenContextIsNull()
	{
		// given
		final ActionContext<Object> ctx = null;

		// when
		final boolean result = toggleItemLockAction.canPerform(ctx);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void canPerformWhenDataIsNull()
	{
		// given
		final Object data = null;
		final ActionContext<Object> ctx = createActionContextSpy(data);

		// when
		final boolean result = toggleItemLockAction.canPerform(ctx);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void canPerformWhenDataIsNotModel()
	{
		// given
		final Object data = "not an Item";
		final ActionContext<Object> ctx = createActionContextSpy(data);

		// when
		final boolean result = toggleItemLockAction.canPerform(ctx);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void canPerformWhenDataIsItemModel()
	{
		// given
		final Object data = new ItemModel();
		final ActionContext<Object> ctx = createActionContextSpy(data);

		// when
		final boolean result = toggleItemLockAction.canPerform(ctx);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void canPerformWhenDataIsProductModel()
	{
		// given
		final Object data = new ProductModel();
		final ActionContext<Object> ctx = createActionContextSpy(data);

		// when
		final boolean result = toggleItemLockAction.canPerform(ctx);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void canPerformWhenDataIsEmptyCollection()
	{
		// given
		final List<Object> items = Collections.emptyList();
		final ActionContext<Object> ctx = createActionContextSpy(items);

		// when
		final boolean result = toggleItemLockAction.canPerform(ctx);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void canPerformWhenDataIsCollectionWithModels()
	{
		// given
		final List<Object> items = Arrays.asList(new ProductModel(), new ProductModel());
		final ActionContext<Object> ctx = createActionContextSpy(items);

		// when
		final boolean result = toggleItemLockAction.canPerform(ctx);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void canPerformWhenDataIsCollectionWithModelsAndOtherTypes()
	{
		// given
		final List<Object> items = Arrays.asList(new ProductModel(), new ProductModel(), "not an Item");
		final ActionContext<Object> ctx = createActionContextSpy(items);

		// when
		final boolean result = toggleItemLockAction.canPerform(ctx);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void notificationsForSingleUnlocked()
	{
		// given
		final ActionContext<Object> ctx = createActionContextSpy(unlockedModel1);

		// when
		assertThat(toggleItemLockAction.needsConfirmation(ctx)).isTrue();
		toggleItemLockAction.getConfirmationMessage(ctx);

		// then
		verify(itemLockingService).isLocked(unlockedModel1);
		verifyNoMoreInteractions(itemLockingService);
		verify(ctx).getLabel(ToggleItemLockAction.LOCK_CONFIRMATION_MESSAGE);
	}

	@Test
	public void notificationsForSingleLocked()
	{
		// when
		final ActionContext<Object> ctx = createActionContextSpy(lockedModel1);

		// when
		assertThat(toggleItemLockAction.needsConfirmation(ctx)).isTrue();
		toggleItemLockAction.getConfirmationMessage(ctx);

		// then
		verify(itemLockingService).isLocked(lockedModel1);
		verifyNoMoreInteractions(itemLockingService);
		verify(ctx).getLabel(ToggleItemLockAction.UNLOCK_CONFIRMATION_MESSAGE);
	}

	@Test
	public void notificationsForCollectionWhenAllAreUnlocked()
	{
		// given
		final Collection<ItemModel> items = Arrays.asList(unlockedModel1, unlockedModel2);
		final ActionContext<Object> ctx = createActionContextSpy(items);

		// when
		assertThat(toggleItemLockAction.needsConfirmation(ctx)).isTrue();
		toggleItemLockAction.getConfirmationMessage(ctx);

		// then
		verify(itemLockingService).isLocked(unlockedModel1);
		verifyNoMoreInteractions(itemLockingService);
		verify(ctx).getLabel(eq(ToggleItemLockAction.LOCK_CONFIRMATION_MESSAGE_MULTI), any(Object[].class));
	}

	@Test
	public void notificationsForCollectionWhenAllAreLocked()
	{
		// given
		final Collection<ItemModel> items = Arrays.asList(lockedModel1, lockedModel2);
		final ActionContext<Object> ctx = createActionContextSpy(items);

		// when
		assertThat(toggleItemLockAction.needsConfirmation(ctx)).isTrue();
		toggleItemLockAction.getConfirmationMessage(ctx);

		// then
		verify(itemLockingService).isLocked(lockedModel1);
		verify(itemLockingService).isLocked(lockedModel2);
		verifyNoMoreInteractions(itemLockingService);
		verify(ctx).getLabel(eq(ToggleItemLockAction.UNLOCK_CONFIRMATION_MESSAGE_MULTI), any(Object[].class));
	}

	@Test
	public void notificationsForCollectionWhenAtLeastOneIsUnlocked()
	{
		// given
		final Collection<ItemModel> items = Arrays.asList(lockedModel1, unlockedModel1, lockedModel2);
		final ActionContext<Object> ctx = createActionContextSpy(items);

		// when
		assertThat(toggleItemLockAction.needsConfirmation(ctx)).isTrue();
		toggleItemLockAction.getConfirmationMessage(ctx);

		// then
		verify(itemLockingService).isLocked(lockedModel1);
		verify(itemLockingService).isLocked(unlockedModel1);
		verifyNoMoreInteractions(itemLockingService);
		verify(ctx).getLabel(eq(ToggleItemLockAction.LOCK_CONFIRMATION_MESSAGE_MULTI), any(Object[].class));
	}

	@Test
	public void performWhenItemIsLocked()
	{
		// given
		final ActionContext<Object> ctx = createActionContextSpy(lockedModel1);

		// when
		toggleItemLockAction.perform(ctx);

		// then
		verify(itemLockingService).unlock(lockedModel1);
		verify(toggleItemLockAction).publishGlobalNotificationOnLockingStateChange(Collections.singletonList(lockedModel1));
	}

	@Test
	public void performWhenItemIsUnlocked()
	{
		// given
		final ActionContext<Object> ctx = createActionContextSpy(unlockedModel1);

		// when
		toggleItemLockAction.perform(ctx);

		// then
		verify(itemLockingService).lock(unlockedModel1);
		verify(toggleItemLockAction).publishGlobalNotificationOnLockingStateChange(Collections.singletonList(unlockedModel1));
	}

	@Test
	public void performForCollectionWhenAllAreLocked()
	{
		// given
		final Collection<ItemModel> items = Arrays.asList(lockedModel1, lockedModel2);
		final ActionContext<Object> ctx = createActionContextSpy(items);

		// when
		toggleItemLockAction.perform(ctx);

		// then
		verify(itemLockingService).unlock(lockedModel1);
		verify(itemLockingService).unlock(lockedModel2);
		verify(toggleItemLockAction).publishGlobalNotificationOnLockingStateChange(items);
	}

	@Test
	public void performForCollectionWhenAllAreUnlocked()
	{
		// given
		final Collection<ItemModel> items = Arrays.asList(unlockedModel1, unlockedModel2);
		final ActionContext<Object> ctx = createActionContextSpy(items);

		// when
		toggleItemLockAction.perform(ctx);

		// then
		verify(itemLockingService).lock(unlockedModel1);
		verify(itemLockingService).lock(unlockedModel2);
		verify(toggleItemLockAction).publishGlobalNotificationOnLockingStateChange(items);
	}

	@Test
	public void performForCollectionWhenAtLeastOneIsUnlocked()
	{
		// given
		final Collection<ItemModel> items = Arrays.asList(lockedModel1, unlockedModel1, lockedModel2);
		final ActionContext<Object> ctx = createActionContextSpy(items);

		// when
		toggleItemLockAction.perform(ctx);

		// then
		verify(itemLockingService).lock(lockedModel1);
		verify(itemLockingService).lock(unlockedModel1);
		verify(itemLockingService).lock(lockedModel1);
		verify(toggleItemLockAction).publishGlobalNotificationOnLockingStateChange(items);
	}

	private static ActionContext<Object> createActionContextSpy(final Object data)
	{
		return spy(new ActionContext<>(data, null, Collections.emptyMap(), Collections.emptyMap()));
	}
}
