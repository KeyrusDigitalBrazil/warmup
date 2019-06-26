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
package com.hybris.backoffice.widgets.actions.sync;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.google.common.collect.Lists;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;


public class SyncActionTest extends AbstractActionUnitTest<SyncAction>
{

	@Mock
	private ObjectFacade objectFacade;

	@InjectMocks
	SyncAction action = new SyncAction();

	@Override
	public SyncAction getActionInstance()
	{
		return action;
	}

	@Test
	public void testCannotPerformWithNull()
	{
		final ActionContext<Object> ctx = mock(ActionContext.class);
		when(ctx.getData()).thenReturn(null);

		assertThat(action.canPerform(ctx)).isFalse();
		verify(objectFacade, never()).isModified(null);
	}

	@Test
	public void testCannotPerformWithListOfNull()
	{
		final ActionContext<Object> ctx = mock(ActionContext.class);
		final List<Object> items = new ArrayList<>();
		items.add(null);
		when(ctx.getData()).thenReturn(items);

		assertThat(action.canPerform(ctx)).isFalse();
		verify(objectFacade, never()).isModified(null);
	}

	@Test
	public void testCannotPerformWithListOfModifiedItems()
	{
		final ItemModel itemModel = mock(ItemModel.class);
		doReturn(Boolean.TRUE).when(objectFacade).isModified(itemModel);
		final ActionContext<Object> ctx = mock(ActionContext.class);
		when(ctx.getData()).thenReturn(Lists.newArrayList(itemModel));

		assertThat(action.canPerform(ctx)).isFalse();
	}

	@Test
	public void testCannotPerformWithOneModifiedItem()
	{
		final ItemModel itemModel = mock(ItemModel.class);
		doReturn(Boolean.TRUE).when(objectFacade).isModified(itemModel);
		final ActionContext<Object> ctx = mock(ActionContext.class);
		when(ctx.getData()).thenReturn(itemModel);

		assertThat(action.canPerform(ctx)).isFalse();
	}

	@Test
	public void testCanPerformWitListOfItems()
	{
		final ItemModel itemModel = mock(ItemModel.class);
		doReturn(Boolean.FALSE).when(objectFacade).isModified(itemModel);
		final ActionContext<Object> ctx = mock(ActionContext.class);
		when(ctx.getData()).thenReturn(Lists.newArrayList(itemModel));

		assertThat(action.canPerform(ctx)).isTrue();
	}

	@Test
	public void testCanPerformOneItem()
	{
		final ItemModel itemModel = mock(ItemModel.class);
		doReturn(Boolean.FALSE).when(objectFacade).isModified(itemModel);
		final ActionContext<Object> ctx = mock(ActionContext.class);
		when(ctx.getData()).thenReturn(itemModel);

		assertThat(action.canPerform(ctx)).isTrue();
	}
}
