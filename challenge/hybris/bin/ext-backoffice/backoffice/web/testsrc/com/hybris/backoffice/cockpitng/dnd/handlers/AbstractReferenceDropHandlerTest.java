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
package com.hybris.backoffice.cockpitng.dnd.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.dnd.DragAndDropActionType;
import com.hybris.cockpitng.dnd.DragAndDropContext;
import com.hybris.cockpitng.dnd.DropOperationData;
import com.hybris.cockpitng.mouse.MouseKeys;
import com.hybris.cockpitng.services.dnd.DragAndDropConfigurationService;


@RunWith(MockitoJUnitRunner.class)
public class AbstractReferenceDropHandlerTest
{
	@Spy
	@InjectMocks
	private final AbstractReferenceDropHandler<Object, Object> handler = new DropHandlerStub();

	@Mock
	private DragAndDropConfigurationService configurationService;

	@Mock
	private DragAndDropContext context;

	@Test
	public void testHandleDropObjectWhenActionTypeIsReplace()
	{
		// given
		final List<Object> dragged = Collections.singletonList(new Object());
		final Object target = new Object();

		doReturn(DragAndDropActionType.REPLACE).when(handler).resolveActionType(context);

		// when
		handler.handleDrop(dragged, target, context);

		// then
		verify(handler).resolveActionType(context);
		verify(handler).handleReplace(dragged, target, context);
	}

	@Test
	public void testHandleDropObjectWhenActionTypeIsAppend()
	{
		// given
		final List<Object> dragged = Collections.singletonList(new Object());
		final Object target = new Object();

		doReturn(DragAndDropActionType.APPEND).when(handler).resolveActionType(context);

		// when
		handler.handleDrop(dragged, target, context);

		// then
		verify(handler).resolveActionType(context);
		verify(handler).handleAppend(dragged, target, context);
	}

	@Test
	public void testGetActionTypeWhenDefaultActionIsAppendAndKeysAreNull()
	{
		// given
		when(configurationService.getDefaultActionType()).thenReturn(DragAndDropActionType.APPEND);

		// when
		final DragAndDropActionType actionType = handler.resolveActionType(context);

		// then
		assertThat(actionType).isSameAs(DragAndDropActionType.APPEND);
		verify(configurationService).getDefaultActionType();
	}

	@Test
	public void testGetActionTypeWhenDefaultActionIsReplaceAndKeysAreNull()
	{
		// given
		when(configurationService.getDefaultActionType()).thenReturn(DragAndDropActionType.REPLACE);

		// when
		final DragAndDropActionType actionType = handler.resolveActionType(context);

		// then
		assertThat(actionType).isSameAs(DragAndDropActionType.REPLACE);
		verify(configurationService).getDefaultActionType();
	}

	@Test
	public void testGetActionTypeWhenDefaultActionIsAppendAndShiftIsPressed()
	{
		// given
		when(configurationService.getDefaultActionType()).thenReturn(DragAndDropActionType.APPEND);
		when(context.getKeys()).thenReturn(new HashSet<>(Arrays.asList(MouseKeys.SHIFT_KEY)));

		// when
		final DragAndDropActionType actionType = handler.resolveActionType(context);

		// then
		assertThat(actionType).isSameAs(DragAndDropActionType.APPEND);
		verify(configurationService).getDefaultActionType();
	}

	@Test
	public void testGetActionTypeWhenAltIsPressed()
	{
		// given
		when(context.getKeys()).thenReturn(new HashSet<>(Arrays.asList(MouseKeys.ALT_KEY)));

		// when
		final DragAndDropActionType actionType = handler.resolveActionType(context);

		// then
		assertThat(actionType).isSameAs(DragAndDropActionType.APPEND);
		verifyZeroInteractions(configurationService);
	}

	@Test
	public void testGetActionTypeWhenCrtlAndAltArePressed()
	{
		// given
		when(context.getKeys()).thenReturn(new HashSet<>(Arrays.asList(MouseKeys.ALT_KEY, MouseKeys.CTRL_KEY)));

		// when
		final DragAndDropActionType actionType = handler.resolveActionType(context);

		// then
		assertThat(actionType).isSameAs(DragAndDropActionType.REPLACE);
		verifyZeroInteractions(configurationService);
	}

	public static class DropHandlerStub extends AbstractReferenceDropHandler<Object, Object>
	{

		@Override
		public List<String> findSupportedTypes()
		{
			return null;
		}

		@Override
		protected List<DropOperationData<Object, Object, Object>> handleAppend(final List<Object> dragged, final Object o,
				final DragAndDropContext context)
		{
			return null;
		}

		@Override
		protected List<DropOperationData<Object, Object, Object>> handleReplace(final List<Object> dragged, final Object o,
				final DragAndDropContext context)
		{
			return null;
		}
	}
}
