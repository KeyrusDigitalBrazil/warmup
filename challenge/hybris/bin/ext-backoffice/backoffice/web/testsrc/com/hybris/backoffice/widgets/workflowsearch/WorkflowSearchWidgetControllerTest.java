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
package com.hybris.backoffice.widgets.workflowsearch;

import static com.hybris.backoffice.widgets.workflowsearch.WorkflowSearchWidgetController.COMPONENT_DATE_RANGE_EDITOR;
import static com.hybris.backoffice.widgets.workflowsearch.WorkflowSearchWidgetController.COMPONENT_SEARCH_BUTTON;
import static com.hybris.backoffice.widgets.workflowsearch.WorkflowSearchWidgetController.MODEL_DATE_RANGE;
import static com.hybris.backoffice.widgets.workflowsearch.WorkflowSearchWidgetController.SETTING_PAGE_SIZE;
import static com.hybris.backoffice.widgets.workflowsearch.WorkflowSearchWidgetController.SOCKET_INPUT_REFRESH;
import static com.hybris.backoffice.widgets.workflowsearch.WorkflowSearchWidgetController.SOCKET_INPUT_STATUSES;
import static com.hybris.backoffice.widgets.workflowsearch.WorkflowSearchWidgetController.SOCKET_OUTPUT_RESULTS;
import static com.hybris.cockpitng.components.Editor.ON_VALUE_CHANGED;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.workflow.WorkflowStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;

import com.hybris.backoffice.workflow.WorkflowFacade;
import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;
import com.hybris.cockpitng.util.Range;


@DeclaredInput(value = SOCKET_INPUT_STATUSES, socketType = Collection.class)
@DeclaredInput(value = SOCKET_INPUT_REFRESH, socketType = Object.class)
@DeclaredViewEvent(componentID = COMPONENT_SEARCH_BUTTON, eventName = Events.ON_CLICK)
@DeclaredViewEvent(componentID = COMPONENT_DATE_RANGE_EDITOR, eventName = ON_VALUE_CHANGED)
@ExtensibleWidget(level = ExtensibleWidget.ALL)
@NullSafeWidget
public class WorkflowSearchWidgetControllerTest extends AbstractWidgetUnitTest<WorkflowSearchWidgetController>
{
	@Spy
	@InjectMocks
	private WorkflowSearchWidgetController controller;

	@Mock
	private WorkflowFacade workflowFacade;
	@Mock
	private Editor rangeEditor;

	@Test
	public void testEmptyListOnEmptyStatuses()
	{
		// given
		widgetModel.put(WorkflowSearchWidgetController.MODEL_STATUSES, Collections.emptySet());
		widgetSettings.put(SETTING_PAGE_SIZE, Integer.valueOf(20));

		// when
		executeViewEvent(COMPONENT_SEARCH_BUTTON, Events.ON_CLICK, new Event(Events.ON_CLICK));

		// then
		assertSocketOutput(SOCKET_OUTPUT_RESULTS, (final Pageable value) -> CollectionUtils.isEmpty((value).getAllResults()));
	}

	@Test
	public void controllerShouldUpdateRangeEditorValue()
	{
		// given
		final Range modelRange = mock(Range.class);
		widgetModel.put(MODEL_DATE_RANGE, modelRange);

		// when
		controller.initialize(new Div());

		// then
		verify(rangeEditor).setValue(modelRange);
	}

	@Test
	public void modelShouldBeUpdatedAfterRangeChanged()
	{
		// given
		final Range newRange = mock(Range.class);
		final Object start = new Object();
		final Object end = new Object();
		when(newRange.getStart()).thenReturn(start);
		when(newRange.getEnd()).thenReturn(end);

		// when
		executeViewEvent(COMPONENT_DATE_RANGE_EDITOR, ON_VALUE_CHANGED, new Event(ON_VALUE_CHANGED, rangeEditor, newRange));

		// then
		assertValuePut(MODEL_DATE_RANGE, newRange);
	}

	@Test
	public void shouldNpeNotBeThrownWhenDateRangeIsNull()
	{
		// given
		prepareGivenStatementForNPEDateRangeTests(mock(Range.class));

		// expect
		try
		{
			controller.doSearch();
			Assert.assertTrue(Boolean.TRUE);
		}
		catch (final NullPointerException npe)
		{
			Assert.fail();
		}
	}

	@Test
	public void shouldNpeNotBeThrownWhenStartDateRangeIsNull()
	{
		// given
		final Range range = mock(Range.class);
		given(range.getEnd()).willReturn(new Date());
		prepareGivenStatementForNPEDateRangeTests(range);

		// expect
		try
		{
			controller.doSearch();
			Assert.assertTrue(Boolean.TRUE);
		}
		catch (final NullPointerException npe)
		{
			Assert.fail();
		}
	}

	@Test
	public void shouldNpeNotBeThrownWhenEndDateRangeIsNull()
	{
		// given
		final Range range = mock(Range.class);
		given(range.getStart()).willReturn(new Date());
		prepareGivenStatementForNPEDateRangeTests(range);

		// expect
		try
		{
			controller.doSearch();
			Assert.assertTrue(Boolean.TRUE);
		}
		catch (final NullPointerException npe)
		{
			Assert.fail();
		}
	}

	protected void prepareGivenStatementForNPEDateRangeTests(final Range rangeMock)
	{
		final Editor editor = mock(Editor.class);
		given(editor.getValue()).willReturn(rangeMock);
		doReturn(editor).when(controller).getRangeEditor();
		doReturn(Lists.newArrayList(WorkflowStatus.FINISHED.toString())).when(controller).getValue(controller.MODEL_STATUSES,
				Collection.class);
		doNothing().when(controller).sendSearchResults(any());
		given(workflowFacade.getWorkflows(any())).willReturn(mock(Pageable.class));
	}

	@Override
	protected WorkflowSearchWidgetController getWidgetController()
	{
		return controller;
	}
}
