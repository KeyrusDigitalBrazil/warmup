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
package com.hybris.backoffice.cockpitng.dnd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Div;

import com.hybris.backoffice.cockpitng.dataaccess.facades.object.validation.BackofficeValidationService;
import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.core.context.impl.DefaultCockpitContext;
import com.hybris.cockpitng.dataaccess.context.Context;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacadeOperationResult;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectAccessException;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.dnd.DefaultDragAndDropContext;
import com.hybris.cockpitng.dnd.DragAndDropContext;
import com.hybris.cockpitng.dnd.DragAndDropStrategy;
import com.hybris.cockpitng.dnd.DropHandler;
import com.hybris.cockpitng.dnd.DropOperationData;
import com.hybris.cockpitng.dnd.DropOperationValidationData;
import com.hybris.cockpitng.dnd.SelectionSupplier;
import com.hybris.cockpitng.mouse.MouseKeys;
import com.hybris.cockpitng.validation.ValidationContext;


@RunWith(MockitoJUnitRunner.class)
public class DefaultDragAndDropStrategyTest
{
	private static final String STRING_TYPE = "string";
	private static final String TARGET_OBJECT = "target";
	private static final String DRAGGED_OBJECT = "dragged";

	@Spy
	@InjectMocks
	private DefaultDragAndDropStrategy strategy;

	@Mock
	private TypeFacade typeFacade;
	@Mock
	private BackofficeValidationService validationService;
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private ModelService modelService;
	@Mock
	private NotificationService notificationService;

	@Before
	public void init() throws TypeNotFoundException
	{
		when(typeFacade.load("A"))
				.thenReturn(new DataType.Builder("A").subtype("B").subtype("C").type(DataType.Type.COMPOUND).build());
		when(typeFacade.load("B")).thenReturn(new DataType.Builder("B").supertype("A").type(DataType.Type.COMPOUND).build());
		when(typeFacade.load("C")).thenReturn(new DataType.Builder("C").supertype("A").subtype("D").subtype("E")
				.type(DataType.Type.COMPOUND).abstractType(true).build());
		when(typeFacade.load("D")).thenReturn(new DataType.Builder("D").supertype("C").type(DataType.Type.COMPOUND).build());
		when(typeFacade.load("E")).thenReturn(new DataType.Builder("E").supertype("C").type(DataType.Type.COMPOUND).build());
	}

	@Test
	public void testResolveDroppablesExactType()
	{
		// given
		final DropHandler handler = mock(DropHandler.class);
		when(handler.findSupportedTypes()).thenReturn(Collections.singletonList("A!"));

		// when
		final String droppables = strategy.resolveDroppables(handler);

		// then
		assertThat(droppables).isEqualTo("A");
	}

	@Test
	public void testResolveDroppablesHierarchy()
	{
		final DropHandler handler = mock(DropHandler.class);
		when(handler.findSupportedTypes()).thenReturn(Collections.singletonList("A"));
		final String droppables = strategy.resolveDroppables(handler);
		assertThat(droppables).isEqualTo("A,B,D,E");

		when(handler.findSupportedTypes()).thenReturn(Collections.singletonList("C"));
		final String droppablesC = strategy.resolveDroppables(handler);
		assertThat(droppablesC).isEqualTo("D,E");

		when(handler.findSupportedTypes()).thenReturn(Arrays.asList("D", "E"));
		final String droppablesDE = strategy.resolveDroppables(handler);
		assertThat(droppablesDE).isEqualTo("D,E");

		when(handler.findSupportedTypes()).thenReturn(Arrays.asList("B", "C"));
		final String droppablesBC = strategy.resolveDroppables(handler);
		assertThat(droppablesBC).isEqualTo("B,D,E");
	}

	@Test
	public void testResolveDroppablesHierarchyLimited()
	{
		// given
		final DropHandler handler = mock(DropHandler.class);
		when(handler.findSupportedTypes()).thenReturn(Collections.singletonList("A"));
		strategy.setSubtypeLimit(3);

		// when
		final String droppables = strategy.resolveDroppables(handler);

		// then
		assertThat(droppables).isEqualTo("A,B,D");
	}

	@Test
	public void testResolveDroppablesGarbageIn()
	{
		final DropHandler handler = mock(DropHandler.class);
		when(handler.findSupportedTypes()).thenReturn(Collections.emptyList());
		final String droppablesEmpty = strategy.resolveDroppables(handler);
		assertThat(droppablesEmpty).isEqualTo("true");

		when(handler.findSupportedTypes()).thenReturn(null);
		final String droppablesNull = strategy.resolveDroppables(handler);
		assertThat(droppablesNull).isEqualTo("true");

		when(handler.findSupportedTypes()).thenReturn(Collections.singletonList("X"));
		final String droppablesX = strategy.resolveDroppables(handler);
		assertThat(droppablesX).isEqualTo("true");
	}

	@Test
	public void testHandleDropSingleSelection()
	{
		// given
		final DropEvent dropEvent = createDropEvent(null, 0);
		final DropHandler handler = mock(DropHandler.class);
		when(handler.findSupportedTypes()).thenReturn(Collections.singletonList("A"));
		final DragAndDropContext context = new DefaultDragAndDropContext.Builder().build();

		// when
		strategy.handleDrop(dropEvent, handler, context);

		// then
		verify(handler).handleDrop(anyListOf(DRAGGED_OBJECT.getClass()), eq(TARGET_OBJECT), any(DragAndDropContext.class));
	}

	@Test
	public void testHandleDropMultiSelection()
	{
		// given
		final DropHandler handler = mock(DropHandler.class);
		when(handler.findSupportedTypes()).thenReturn(Collections.singletonList("A"));
		final DragAndDropContext context = new DefaultDragAndDropContext.Builder().build();
		final List<String> multiSelection = Arrays.asList("dragged1", "dragged2");
		final DropEvent dropEvent = createDropEvent(multiSelection, 0);

		// when
		strategy.handleDrop(dropEvent, handler, context);

		// then
		verify(handler).handleDrop(anyListOf(DRAGGED_OBJECT.getClass()), eq(TARGET_OBJECT), any(DragAndDropContext.class));
	}

	private static DropEvent createDropEvent(final List multiSelection, final int keys)
	{
		final Div draggedDiv = mock(Div.class);
		when(draggedDiv.getAttribute(DragAndDropStrategy.ATTRIBUTE_DND_DATA, true)).thenReturn(DRAGGED_OBJECT);
		final Div targetDiv = mock(Div.class);
		when(targetDiv.getAttribute(DragAndDropStrategy.ATTRIBUTE_DND_DATA, true)).thenReturn(TARGET_OBJECT);

		if (multiSelection != null)
		{
			when(draggedDiv.getAttribute(DragAndDropStrategy.ATTRIBUTE_DND_SELECTION_SUPPLIER, true))
					.thenReturn((SelectionSupplier) () -> multiSelection);
		}

		return new DropEvent("event", targetDiv, draggedDiv, 0, 0, 0, 0, keys);
	}

	@Test
	public void testMakeDroppable()
	{
		// given
		when(typeFacade.getType(TARGET_OBJECT)).thenReturn(STRING_TYPE);

		final Div targetDiv = mock(Div.class);
		when(targetDiv.getAttribute(DragAndDropStrategy.ATTRIBUTE_DND_DATA, true)).thenReturn(TARGET_OBJECT);

		final DropHandler handler = mock(DropHandler.class);
		when(handler.findSupportedTypes()).thenReturn(Collections.singletonList("A!"));

		final Map<String, DropHandler> map = new HashMap<>();
		map.put(STRING_TYPE, handler);
		strategy.setHandlerMap(map);

		// when
		strategy.makeDroppable(targetDiv, TARGET_OBJECT, new DefaultCockpitContext());

		// then
		verify(targetDiv).setAttribute(DragAndDropStrategy.ATTRIBUTE_DND_DATA, TARGET_OBJECT);
		verify(handler).findSupportedTypes();
		verify(targetDiv).addEventListener(eq(Events.ON_DROP), any());
		verify(targetDiv).setDroppable("A");
		verifyNoMoreInteractions(targetDiv);
	}

	@Test
	public void testDoNotMakeDroppableIfHandlersListIsEmpty()
	{
		// given
		when(typeFacade.getType(TARGET_OBJECT)).thenReturn(STRING_TYPE);

		final Div targetDiv = mock(Div.class);
		when(targetDiv.getAttribute(DragAndDropStrategy.ATTRIBUTE_DND_DATA, true)).thenReturn(TARGET_OBJECT);

		strategy.setHandlerMap(Collections.emptyMap());

		// when
		strategy.makeDroppable(targetDiv, TARGET_OBJECT, new DefaultCockpitContext());

		// then
		verify(targetDiv, never()).setAttribute(DragAndDropStrategy.ATTRIBUTE_DND_DATA, TARGET_OBJECT);
		verify(targetDiv, never()).addEventListener(eq(Events.ON_DROP), any());
		verify(targetDiv, never()).setDroppable("A");
		verifyNoMoreInteractions(targetDiv);
	}

	@Test
	public void testMakeDraggable()
	{
		// given
		when(typeFacade.getType(DRAGGED_OBJECT)).thenReturn(STRING_TYPE);

		final Div draggedDiv = mock(Div.class);
		final DefaultCockpitContext dragContext = new DefaultCockpitContext();
		final SelectionSupplier selectionSupplier = Collections::emptyList;

		// when
		strategy.makeDraggable(draggedDiv, DRAGGED_OBJECT, dragContext, selectionSupplier);

		// then
		verify(draggedDiv).setAttribute(DragAndDropStrategy.ATTRIBUTE_DND_DATA, DRAGGED_OBJECT);
		verify(draggedDiv).setAttribute(DragAndDropStrategy.ATTRIBUTE_DND_SELECTION_SUPPLIER, selectionSupplier);
		verify(draggedDiv).setAttribute(DefaultDragAndDropStrategy.ATTRIBUTE_DND_DRAG_CONTEXT, dragContext);
		verify(draggedDiv).setDraggable(STRING_TYPE);
		verifyNoMoreInteractions(draggedDiv);
	}

	@Test
	public void testResolveHandledSubtypes()
	{
		final DropHandler handler = mock(DropHandler.class);

		Map<String, DropHandler> map = new HashMap<>();
		map.put("A", handler);
		strategy.setHandlerMap(map);

		Map<String, DropHandler> resolvedMap = strategy.resolveHandledSubtypes();

		assertThat(resolvedMap.get("A")).isEqualTo(handler);
		assertThat(resolvedMap.get("B")).isEqualTo(handler);
		assertThat(resolvedMap.get("D")).isEqualTo(handler);
		assertThat(resolvedMap.get("E")).isEqualTo(handler);

		map = new HashMap<>();
		map.put("A!", handler);
		strategy.setHandlerMap(map);

		resolvedMap = strategy.resolveHandledSubtypes();

		assertThat(resolvedMap.get("A")).isEqualTo(handler);
		assertThat(resolvedMap.containsKey("B")).isFalse();
		assertThat(resolvedMap.containsKey("D")).isFalse();
		assertThat(resolvedMap.containsKey("E")).isFalse();
	}

	@Test
	public void testContextParamsPassed()
	{
		// given
		final DropEvent dropEvent = createDropEvent(null, MouseEvent.ALT_KEY | MouseEvent.LEFT_CLICK);

		final DropHandler handler = mock(DropHandler.class);
		when(handler.findSupportedTypes()).thenReturn(Collections.singletonList("A"));

		final DragAndDropContext context = new DefaultDragAndDropContext.Builder().build();
		context.setParameter("x", "y");

		// when
		strategy.handleDrop(dropEvent, handler, context);

		// then
		final ArgumentCaptor<DragAndDropContext> captor = ArgumentCaptor.forClass(DragAndDropContext.class);
		verify(handler).handleDrop(anyList(), eq(TARGET_OBJECT), captor.capture());

		assertThat(captor.getValue().getTargetContext().getParameter("x")).isEqualTo("y");
		assertThat(captor.getValue().getKeys()).contains(MouseKeys.ALT_KEY, MouseKeys.LEFT_CLICK);
	}

	@Test
	public void testValidation()
	{
		// given
		final DropEvent dropEvent = createDropEvent(null, MouseEvent.ALT_KEY | MouseEvent.LEFT_CLICK);
		final DropHandler handler = mock(DropHandler.class);

		final List<DropOperationData> operationsDatas = Arrays.asList(new DropOperationData(DRAGGED_OBJECT, TARGET_OBJECT,
				DRAGGED_OBJECT, new DefaultDragAndDropContext.Builder().build(), "success_key"));

		final ObjectFacadeOperationResult objectFacadeOperationResult = new ObjectFacadeOperationResult();
		objectFacadeOperationResult.addSuccessfulObject(DRAGGED_OBJECT);

		when(handler.handleDrop(anyList(), eq(TARGET_OBJECT), any(DragAndDropContext.class))).thenReturn(operationsDatas);
		when(objectFacade.save(anyCollection(), any(Context.class))).thenReturn(objectFacadeOperationResult);

		// when
		strategy.handleDrop(dropEvent, handler, new DefaultDragAndDropContext.Builder().build());

		// then
		verify(validationService).validate(eq(DRAGGED_OBJECT), any(ValidationContext.class));
	}

	@Test
	public void testSystemValidationNotRun()
	{
		// given
		final DropEvent dropEvent = createDropEvent(null, MouseEvent.ALT_KEY | MouseEvent.LEFT_CLICK);
		final DropHandler handler = mock(DropHandler.class);

		final List<DropOperationData> operationsDatas = Arrays.asList(new DropOperationData(DRAGGED_OBJECT, TARGET_OBJECT,
				DRAGGED_OBJECT, new DefaultDragAndDropContext.Builder().build(), "success_key"));

		final ObjectFacadeOperationResult objectFacadeOperationResult = new ObjectFacadeOperationResult();
		objectFacadeOperationResult.addSuccessfulObject(DRAGGED_OBJECT);

		when(handler.handleDrop(anyList(), eq(TARGET_OBJECT), any(DragAndDropContext.class))).thenReturn(operationsDatas);
		when(objectFacade.save(anyCollection(), any(Context.class))).thenReturn(objectFacadeOperationResult);

		strategy.setPerformSystemValidation(false);

		// when
		strategy.handleDrop(dropEvent, handler, new DefaultDragAndDropContext.Builder().build());

		// then
		verify(validationService, never()).validate(eq(DRAGGED_OBJECT), any(ValidationContext.class));
	}

	@Test
	public void shouldFindElementsWithoutErrorsAndWarnings()
	{
		// given
		final String elementWithoutErrors = "elementWithoutErrors";
		final String elementWithError = "elementWithError";
		final String elementWithWarnings = "elementWithWarning";
		final DropOperationData elementWithoutErrorsData = prepareDropOperationData(elementWithoutErrors);
		final DropOperationData elementWithErrorData = prepareDropOperationData(elementWithError);
		final DropOperationData elementWithWarningsData = prepareDropOperationData(elementWithWarnings);
		final List<DropOperationData> allElements = Arrays.asList(elementWithoutErrorsData, elementWithErrorData,
				elementWithWarningsData);
		final List<DropOperationValidationData> validationData = Arrays.asList(prepareOperationValidationData(elementWithErrorData),
				prepareOperationValidationData(elementWithWarningsData));

		// when
		final List<DropOperationData> itemsWithoutErrorsAndWarnings = strategy.findItemsWithoutErrorsAndWarnings(allElements,
				validationData);

		// then
		assertThat(itemsWithoutErrorsAndWarnings).hasSize(1);
		assertThat(itemsWithoutErrorsAndWarnings).contains(elementWithoutErrorsData);
	}

	@Test
	public void applyModificationsWhenCollecitonIsEmpty()
	{
		// given
		final DefaultDragAndDropContext context = mock(DefaultDragAndDropContext.class);
		final Collection<DropOperationData> data = new ArrayList<>();

		// when
		strategy.applyModifications(context, data);

		// then
		verify(strategy).applyModifications(context, data);
		verifyNoMoreInteractions(strategy);
	}

	@Test
	public void applyModificationsWhenCollectionIsNotEmpty()
	{
		// given
		final DropOperationData operationData1 = mock(DropOperationData.class);
		final DropOperationData operationData2 = mock(DropOperationData.class);
		final DefaultDragAndDropContext context = mock(DefaultDragAndDropContext.class);

		final ObjectFacadeOperationResult<Object> result = new ObjectFacadeOperationResult<>();

		doReturn(result).when(strategy).save(any(), any());
		doNothing().when(strategy).refreshFailedModels(any(), any());
		doNothing().when(strategy).notifyUser(any(), any(), any());

		final Collection<DropOperationData> data = Arrays.asList(operationData1, operationData2);

		// when
		strategy.applyModifications(context, data);

		// then
		verify(strategy).applyModifications(context, data);
		verify(strategy).saveAndNotify(context, data);
		final ArgumentCaptor<List<DropOperationData>> captor = ArgumentCaptor.forClass((Class) List.class);
		verify(strategy).save(captor.capture(), eq(context));
		final List<DropOperationData> dataList = captor.getValue();
		assertThat(dataList).containsExactly(operationData1, operationData2);
		verify(strategy).refreshFailedModels(dataList, result);
		verify(strategy).notifyUser(result, dataList, context);
		verifyNoMoreInteractions(strategy);
	}

	@Test
	public void saveWhenCollectionIsEmpty()
	{
		// given
		final List<DropOperationData> data = new ArrayList<>();
		final DefaultDragAndDropContext context = mock(DefaultDragAndDropContext.class);

		// when
		final ObjectFacadeOperationResult result = strategy.save(data, context);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getSuccessfulObjects()).isEmpty();
		assertThat(result.getFailedObjects()).isEmpty();
	}

	@Test
	public void saveWhenCollectionOneModifiedEqualToNull()
	{
		// given
		final ItemModel item1 = mock(ItemModel.class);
		final DropOperationData operationData1 = prepareDropOperationData(item1);
		final DropOperationData operationData = prepareDropOperationData(null);
		final ItemModel item3 = mock(ItemModel.class);
		final DropOperationData operationData3 = prepareDropOperationData(item3);
		final List<DropOperationData> data = Arrays.asList(operationData1, operationData, operationData3);
		final DefaultDragAndDropContext context = spy(new DefaultDragAndDropContext.Builder().build());

		final Boolean reloadUiAfterSave = Boolean.TRUE;
		strategy.setReloadUiAfterSave(reloadUiAfterSave);

		final ObjectFacadeOperationResult operationResult = mock(ObjectFacadeOperationResult.class);
		when(objectFacade.save(anyCollection(), any(Context.class))).thenReturn(operationResult);

		// when
		final ObjectFacadeOperationResult result = strategy.save(data, context);

		// then
		assertThat(result).isSameAs(operationResult);
		verify(context).setParameter("shouldReloadAfterUpdate", reloadUiAfterSave);

		final ArgumentCaptor<List> objectsCaptor = ArgumentCaptor.forClass(List.class);
		final ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
		verify(objectFacade).save(objectsCaptor.capture(), contextCaptor.capture());
		assertThat(objectsCaptor.getValue()).containsExactly(item1, item3);
		assertThat(contextCaptor.getValue().getAttribute("shouldReloadAfterUpdate")).isSameAs(reloadUiAfterSave);
	}

	@Test
	public void refreshFailedModelsWhenAllAreSavedSuccessfully()
	{
		// given
		final ItemModel item1 = mock(ItemModel.class);
		final DropOperationData operationData1 = prepareDropOperationData(item1);
		final ItemModel item2 = mock(ItemModel.class);
		final DropOperationData operationData2 = prepareDropOperationData(item2);

		final List<DropOperationData> data = Arrays.asList(operationData1, operationData2);

		final ObjectFacadeOperationResult result = mock(ObjectFacadeOperationResult.class);
		when(result.getSuccessfulObjects()).thenReturn(Arrays.asList(item1, item2));
		when(result.getFailedObjects()).thenReturn(Collections.emptySet());

		// when
		strategy.refreshFailedModels(data, result);

		// then
		verify(strategy).refreshFailedModels(data, result);
		verifyNoMoreInteractions(strategy);
	}

	@Test
	public void refreshFailedModelsWhenSaveOperationFailedForSomeObjects()
	{
		// given
		final ItemModel item1 = mock(ItemModel.class);
		final DropOperationData operationData1 = prepareDropOperationData(item1);
		final ItemModel item2 = mock(ItemModel.class);
		final DropOperationData operationData2 = prepareDropOperationData(item2);
		final ItemModel item3 = mock(ItemModel.class);
		final DropOperationData operationData3 = prepareDropOperationData(item3);
		final ItemModel item4 = mock(ItemModel.class);
		final DropOperationData operationData4 = prepareDropOperationData(item4);

		final List<DropOperationData> data = Arrays.asList(operationData1, operationData2, operationData3, operationData4);

		final ObjectFacadeOperationResult result = mock(ObjectFacadeOperationResult.class);
		when(result.getSuccessfulObjects()).thenReturn(Arrays.asList(item1, item3));
		when(result.getFailedObjects()).thenReturn(new HashSet<>(Arrays.asList(item2, item4)));

		// when
		strategy.refreshFailedModels(data, result);

		// then
		verify(strategy).refreshFailedModels(data, result);
		final ArgumentCaptor<Collection<DropOperationData>> captor = ArgumentCaptor.forClass((Class) Collection.class);
		verify(strategy).refreshModels(captor.capture());
		assertThat(captor.getValue()).containsOnly(operationData2, operationData4);
		verifyNoMoreInteractions(strategy);
	}

	@Test
	public void notifyUser()
	{
		// given
		final ItemModel item1 = mock(ItemModel.class);
		final DropOperationData operationData1 = prepareDropOperationData(item1);
		final ItemModel item2 = mock(ItemModel.class);
		final DropOperationData operationData2 = prepareDropOperationData(item2);
		final ItemModel item3 = mock(ItemModel.class);
		final DropOperationData operationData3 = prepareDropOperationData(item3);
		final ItemModel item4 = mock(ItemModel.class);
		final DropOperationData operationData4 = prepareDropOperationData(item4);

		final DefaultDragAndDropContext context = mock(DefaultDragAndDropContext.class);

		final ObjectFacadeOperationResult<Object> result = mock(ObjectFacadeOperationResult.class);
		when(result.getSuccessfulObjects()).thenReturn(Arrays.asList(item1, item3));
		when(result.getFailedObjects()).thenReturn(new HashSet<>(Arrays.asList(item2, item4)));
		final ObjectAccessException item2Exception = mock(ObjectAccessException.class);
		when(result.getErrorForObject(item2)).thenReturn(item2Exception);
		final ObjectAccessException item4Exception = mock(ObjectAccessException.class);
		when(result.getErrorForObject(item4)).thenReturn(item4Exception);

		final List<DropOperationData> operationsData = Arrays.asList(operationData1, operationData2, operationData3,
				operationData4);

		// when
		strategy.notifyUser(result, operationsData, context);

		// then
		final ArgumentCaptor<List<DropOperationData>> successCaptor = ArgumentCaptor.forClass((Class) List.class);
		verify(strategy).notifyUserAboutSuccess(successCaptor.capture(), eq(context));
		assertThat(successCaptor.getValue()).containsExactly(operationData1, operationData3);

		final ArgumentCaptor<Map<DropOperationData, ObjectAccessException>> failureCaptor = ArgumentCaptor
				.forClass((Class) Map.class);
		verify(strategy).notifyUserAboutFailure(failureCaptor.capture(), eq(context));

		final Map<DropOperationData, ObjectAccessException> failed = new HashMap<>();
		failed.put(operationData2, item2Exception);
		failed.put(operationData4, item4Exception);
		assertThat(failureCaptor.getValue()).isEqualTo(failed);
	}

	private static DropOperationData prepareDropOperationData(final Object data)
	{
		return new DropOperationData(data, TARGET_OBJECT, data, new DefaultDragAndDropContext.Builder().build(), "");
	}

	private DropOperationValidationData prepareOperationValidationData(final DropOperationData operationData)
	{
		return new DropOperationValidationData(operationData, new HashedMap());
	}
}
