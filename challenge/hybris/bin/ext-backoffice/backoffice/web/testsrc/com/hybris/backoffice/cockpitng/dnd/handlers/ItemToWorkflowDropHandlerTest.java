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
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.workflow.model.WorkflowItemAttachmentModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.workflow.WorkflowFacade;
import com.hybris.backoffice.workflow.WorkflowsTypeFacade;
import com.hybris.cockpitng.dnd.DragAndDropContext;
import com.hybris.cockpitng.dnd.DropOperationData;


@RunWith(MockitoJUnitRunner.class)
public class ItemToWorkflowDropHandlerTest
{
	@Spy
	@InjectMocks
	private ItemToWorkflowDropHandler handler;

	@Mock
	private WorkflowFacade workflowFacade;
	@Mock
	private WorkflowsTypeFacade workflowsTypeService;
	@Mock
	private NotificationService notificationService;

	@Mock
	private WorkflowModel workflowModel;
	@Mock
	private DragAndDropContext context;

	@Test
	public void shouldAddAllDraggedItemsToEmptyList()
	{
		// given
		given(workflowModel.getAttachments()).willReturn(Collections.emptyList());
		final List<ItemModel> draggedItems = Arrays.asList(mock(ItemModel.class), mock(ItemModel.class), mock(ItemModel.class));

		// when
		final List<DropOperationData<ItemModel, WorkflowModel, Object>> list = handler.handleDrop(draggedItems, workflowModel,
				context);

		// then
		assertThat(list.size()).isEqualTo(Integer.valueOf(draggedItems.size()));
	}

	@Test
	public void shouldAddOnlyNotExistedDraggedItems()
	{
		// given
		final ItemModel itemModelExist1 = mock(ItemModel.class);
		final ItemModel itemModelExist2 = mock(ItemModel.class);
		final WorkflowItemAttachmentModel workflowItemAttachmentModelExist1 = mock(WorkflowItemAttachmentModel.class);
		final WorkflowItemAttachmentModel workflowItemAttachmentModelExist2 = mock(WorkflowItemAttachmentModel.class);
		given(workflowItemAttachmentModelExist1.getItem()).willReturn(itemModelExist1);
		given(workflowItemAttachmentModelExist2.getItem()).willReturn(itemModelExist2);
		given(workflowModel.getAttachments())
				.willReturn(Arrays.asList(workflowItemAttachmentModelExist1, workflowItemAttachmentModelExist2));
		final List<ItemModel> draggedItems = Arrays.asList(mock(ItemModel.class), itemModelExist1, itemModelExist2);

		// when
		final List<DropOperationData<ItemModel, WorkflowModel, Object>> list = handler.handleDrop(draggedItems, workflowModel,
				context);

		// then
		assertThat(list.size()).isEqualTo(Integer.valueOf(1));
		verify(handler).notifyMultiple(anyCollection());
	}
}
