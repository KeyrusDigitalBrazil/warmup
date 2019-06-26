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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.core.context.CockpitContext;
import com.hybris.cockpitng.dnd.DragAndDropContext;
import com.hybris.cockpitng.dnd.DropOperationData;


@RunWith(MockitoJUnitRunner.class)
public class CategoryToCategoryDropHandlerTest
{
	@Spy
	private CategoryToCategoryDropHandler handler;

	@Mock
	private CategoryModel dragged;
	@Mock
	private CategoryModel target;
	@Mock
	private DragAndDropContext context;
	@Mock
	private CockpitContext dragContext;

	@Before
	public void setup()
	{
		when(context.getDraggedContext()).thenReturn(dragContext);
	}

	@Test
	public void testHandleAppendWhenDraggedDoesNotHaveSupercategoriesAndCopyToCategory()
	{
		// given
		when(dragged.getSupercategories()).thenReturn(Collections.emptyList());

		doNothing().when(handler).addRelatedObjectToUpdateToContextAppend(dragged, target, context);

		// when
		final List<DropOperationData<CategoryModel, CategoryModel, Object>> result = handler.handleAppend(Collections.singletonList(dragged), target, context);

		// test
		assertThat(result.get(0).getDragged()).isSameAs(dragged);

		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(dragged).setSupercategories(captor.capture());
		assertThat(captor.getValue()).containsExactly(target);
	}

	@Test
	public void testHandleAppendWhenDraggedHasSupercategoryAndCopyToCategory()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion);
		when(target.getCatalogVersion()).thenReturn(catalogVersion);

		final CategoryModel supercategory = mock(CategoryModel.class);
		when(dragged.getSupercategories()).thenReturn(Arrays.asList(supercategory));
		when(dragContext.getParameter(CategoryToCategoryDropHandler.CONTEXT_PARAMETER_PARENT_OBJECT)).thenReturn(supercategory);

		doNothing().when(handler).addRelatedObjectToUpdateToContextAppend(dragged, target, context);

		// when
		final List<DropOperationData<CategoryModel, CategoryModel, Object>> result = handler.handleAppend(Collections.singletonList(dragged), target, context);

		// test
		assertThat(result.get(0).getDragged()).isSameAs(dragged);

		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(dragged).setSupercategories(captor.capture());
		assertThat(captor.getValue()).containsExactly(supercategory, target);
	}

	@Test
	public void testHandleAppendWhenDraggedHasSupercategoryAndCopyToIt()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion);
		when(target.getCatalogVersion()).thenReturn(catalogVersion);

		when(dragged.getSupercategories()).thenReturn(Arrays.asList(target));
		when(dragContext.getParameter(CategoryToCategoryDropHandler.CONTEXT_PARAMETER_PARENT_OBJECT)).thenReturn(target);

		doNothing().when(handler).addRelatedObjectToUpdateToContextAppend(dragged, target, context);

		// when
		final List<DropOperationData<CategoryModel, CategoryModel, Object>> result = handler.handleAppend(Collections.singletonList(dragged), target, context);

		// test
		assertThat(result.get(0).getDragged()).isSameAs(dragged);

		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(dragged).setSupercategories(captor.capture());
		assertThat(captor.getValue()).containsExactly(target);
	}

	@Test
	public void testAddRelatedObjectToUpdateToContextAppendWhenDraggedDoesNotHaveSupercategoriesAndCopyToCategoryInTheSameCatalogVersion()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion);
		when(target.getCatalogVersion()).thenReturn(catalogVersion);

		when(dragged.getSupercategories()).thenReturn(Collections.emptyList());

		// when
		handler.addRelatedObjectToUpdateToContextAppend(dragged, target, context);

		// test
		final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> valueCaptor = ArgumentCaptor.forClass(List.class);
		verify(context).setParameter(keyCaptor.capture(), valueCaptor.capture());
		assertThat(keyCaptor.getValue()).isSameAs(CategoryToCategoryDropHandler.PAREMETER_RELATED_OBJECTS_TO_UPDATE);
		assertThat(valueCaptor.getValue()).containsExactly(catalogVersion, target);
	}

	@Test
	public void testAddRelatedObjectToUpdateToContextAppendWhenDraggedDoesNotHaveSupercategoriesInAssignedCategoryAndCopyToCategoryInOtherCatalogVersion()
	{
		// given
		final CatalogVersionModel catalogVersion1 = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion1);
		final CatalogVersionModel catalogVersion2 = mock(CatalogVersionModel.class);
		when(target.getCatalogVersion()).thenReturn(catalogVersion2);

		final CategoryModel supercategory = mock(CategoryModel.class);
		when(supercategory.getCatalogVersion()).thenReturn(catalogVersion2);
		when(dragged.getSupercategories()).thenReturn(Collections.emptyList());

		// when
		handler.addRelatedObjectToUpdateToContextAppend(dragged, target, context);

		// test
		final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> valueCaptor = ArgumentCaptor.forClass(List.class);
		verify(context).setParameter(keyCaptor.capture(), valueCaptor.capture());
		assertThat(keyCaptor.getValue()).isSameAs(CategoryToCategoryDropHandler.PAREMETER_RELATED_OBJECTS_TO_UPDATE);
		assertThat(valueCaptor.getValue()).containsExactly(target);
	}

	@Test
	public void testAddRelatedObjectToUpdateToContextAppendWhenDraggedHasSupercategoryAndCopyToCategory()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion);
		when(target.getCatalogVersion()).thenReturn(catalogVersion);

		final CategoryModel supercategory = mock(CategoryModel.class);
		when(supercategory.getCatalogVersion()).thenReturn(catalogVersion);
		when(dragged.getSupercategories()).thenReturn(Arrays.asList(supercategory));

		// when
		handler.addRelatedObjectToUpdateToContextAppend(dragged, target, context);

		// test
		final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> valueCaptor = ArgumentCaptor.forClass(List.class);
		verify(context).setParameter(keyCaptor.capture(), valueCaptor.capture());
		assertThat(keyCaptor.getValue()).isSameAs(CategoryToCategoryDropHandler.PAREMETER_RELATED_OBJECTS_TO_UPDATE);
		assertThat(valueCaptor.getValue()).containsExactly(target);
	}

	@Test
	public void testHandleReplaceWhenDraggedDoesNotHaveSupercategoriesAndMoveToCategory()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion);
		when(target.getCatalogVersion()).thenReturn(catalogVersion);

		when(dragged.getSupercategories()).thenReturn(Collections.emptyList());
		when(dragContext.getParameter(CategoryToCategoryDropHandler.CONTEXT_PARAMETER_PARENT_OBJECT)).thenReturn(catalogVersion);

		doNothing().when(handler).addRelatedObjectToUpdateToContextReplace(dragged, target, context);

		// when
		final CategoryModel result = handler.replaceSupercategory(dragged, target, context);

		// test
		assertThat(result).isSameAs(dragged);

		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(dragged).setSupercategories(captor.capture());
		assertThat(captor.getValue()).containsExactly(target);
	}

	@Test
	public void testHandleReplaceWhenDraggedHasSupercategoriesAndMoveToCategory()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion);
		when(target.getCatalogVersion()).thenReturn(catalogVersion);

		final CategoryModel supercategory1 = mock(CategoryModel.class);
		final CategoryModel supercategory2 = mock(CategoryModel.class);
		when(dragged.getSupercategories()).thenReturn(Arrays.asList(supercategory1, supercategory2));
		when(dragContext.getParameter(CategoryToCategoryDropHandler.CONTEXT_PARAMETER_PARENT_OBJECT)).thenReturn(supercategory1);

		doNothing().when(handler).addRelatedObjectToUpdateToContextReplace(dragged, target, context);

		// when
		final CategoryModel result = handler.replaceSupercategory(dragged, target, context);

		// test
		assertThat(result).isSameAs(dragged);

		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(dragged).setSupercategories(captor.capture());
		assertThat(captor.getValue()).containsExactly(supercategory2, target);
	}

	@Test
	public void testHandleReplaceWhenDraggedHasSupercategoryAndMoveToIt()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion);
		when(target.getCatalogVersion()).thenReturn(catalogVersion);

		when(dragged.getSupercategories()).thenReturn(Arrays.asList(target));
		when(dragContext.getParameter(CategoryToCategoryDropHandler.CONTEXT_PARAMETER_PARENT_OBJECT)).thenReturn(target);

		doNothing().when(handler).addRelatedObjectToUpdateToContextReplace(dragged, target, context);

		// when
		final CategoryModel result = handler.replaceSupercategory(dragged, target, context);

		// test
		assertThat(result).isSameAs(dragged);

		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(dragged).setSupercategories(captor.capture());
		assertThat(captor.getValue()).containsExactly(target);
	}

	@Test
	public void testAddRelatedObjectToUpdateToContextReplaceWhenDraggedHasSupercategoryAndMoveToCategoryInTheSameCatalogVersion()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion);
		when(target.getCatalogVersion()).thenReturn(catalogVersion);

		final CategoryModel supercategory = mock(CategoryModel.class);
		when(supercategory.getCatalogVersion()).thenReturn(catalogVersion);
		when(dragged.getSupercategories()).thenReturn(Arrays.asList(supercategory));
		when(dragContext.getParameter(CategoryToCategoryDropHandler.CONTEXT_PARAMETER_PARENT_OBJECT)).thenReturn(supercategory);

		// when
		handler.addRelatedObjectToUpdateToContextReplace(dragged, target, context);

		// test
		final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> valueCaptor = ArgumentCaptor.forClass(List.class);
		verify(context).setParameter(keyCaptor.capture(), valueCaptor.capture());
		assertThat(keyCaptor.getValue()).isSameAs(CategoryToCategoryDropHandler.PAREMETER_RELATED_OBJECTS_TO_UPDATE);
		assertThat(valueCaptor.getValue()).containsExactly(supercategory, target);
	}

	@Test
	public void testAddRelatedObjectToUpdateToContextReplaceWhenDraggedDoesNotHaveSupercategoriesAndMoveToCategoryInTheSameCatalogVersion()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion);
		when(target.getCatalogVersion()).thenReturn(catalogVersion);

		when(dragged.getSupercategories()).thenReturn(Collections.emptyList());
		when(dragContext.getParameter(CategoryToCategoryDropHandler.CONTEXT_PARAMETER_PARENT_OBJECT)).thenReturn(catalogVersion);

		// when
		handler.addRelatedObjectToUpdateToContextReplace(dragged, target, context);

		// test
		final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> valueCaptor = ArgumentCaptor.forClass(List.class);
		verify(context).setParameter(keyCaptor.capture(), valueCaptor.capture());
		assertThat(keyCaptor.getValue()).isSameAs(CategoryToCategoryDropHandler.PAREMETER_RELATED_OBJECTS_TO_UPDATE);
		assertThat(valueCaptor.getValue()).containsExactly(catalogVersion, target);
	}

	@Test
	public void testAddRelatedObjectToUpdateToContextReplaceWhenDraggedHasSupercategoriesAndMoveToCategoryInTheOtherCatalogVersion()
	{
		// given
		final CatalogVersionModel catalogVersion1 = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion1);
		final CatalogVersionModel catalogVersion2 = mock(CatalogVersionModel.class);
		when(target.getCatalogVersion()).thenReturn(catalogVersion2);

		final CategoryModel supercategory1 = mock(CategoryModel.class);
		final CategoryModel supercategory2 = mock(CategoryModel.class);
		when(supercategory1.getCatalogVersion()).thenReturn(catalogVersion1);
		when(supercategory2.getCatalogVersion()).thenReturn(catalogVersion1);
		when(dragged.getSupercategories()).thenReturn(Arrays.asList(supercategory1, supercategory2));
		when(dragContext.getParameter(CategoryToCategoryDropHandler.CONTEXT_PARAMETER_PARENT_OBJECT)).thenReturn(supercategory1);

		// when
		handler.addRelatedObjectToUpdateToContextReplace(dragged, target, context);

		// test
		final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> valueCaptor = ArgumentCaptor.forClass(List.class);
		verify(context).setParameter(keyCaptor.capture(), valueCaptor.capture());
		assertThat(keyCaptor.getValue()).isSameAs(CategoryToCategoryDropHandler.PAREMETER_RELATED_OBJECTS_TO_UPDATE);
		assertThat(valueCaptor.getValue()).containsExactly(supercategory1, target);
	}

	@Test
	public void testAddRelatedObjectToUpdateToContextReplaceWhenDraggedHasSupercategoryAndMoveToCategoryInTheOtherCatalogVersion()
	{
		// given
		final CatalogVersionModel catalogVersion1 = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion1);
		final CatalogVersionModel catalogVersion2 = mock(CatalogVersionModel.class);
		when(target.getCatalogVersion()).thenReturn(catalogVersion2);

		final CategoryModel supercategory = mock(CategoryModel.class);
		when(supercategory.getCatalogVersion()).thenReturn(catalogVersion1);
		when(dragged.getSupercategories()).thenReturn(Arrays.asList(supercategory));
		when(dragContext.getParameter(CategoryToCategoryDropHandler.CONTEXT_PARAMETER_PARENT_OBJECT)).thenReturn(supercategory);

		// when
		handler.addRelatedObjectToUpdateToContextReplace(dragged, target, context);

		// test
		final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> valueCaptor = ArgumentCaptor.forClass(List.class);
		verify(context).setParameter(keyCaptor.capture(), valueCaptor.capture());
		assertThat(keyCaptor.getValue()).isSameAs(CategoryToCategoryDropHandler.PAREMETER_RELATED_OBJECTS_TO_UPDATE);
		assertThat(valueCaptor.getValue()).containsExactly(supercategory, catalogVersion1, target);
	}

	@Test
	public void testAddRelatedObjectToUpdateToContextReplaceWhenDraggedDoesNotHaveSupercategoriesAndMoveToCategoryInOtherCatalogVersion()
	{
		// given
		final CatalogVersionModel catalogVersion1 = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion1);
		final CatalogVersionModel catalogVersion2 = mock(CatalogVersionModel.class);
		when(target.getCatalogVersion()).thenReturn(catalogVersion2);

		when(dragged.getSupercategories()).thenReturn(Collections.emptyList());
		when(dragContext.getParameter(CategoryToCategoryDropHandler.CONTEXT_PARAMETER_PARENT_OBJECT)).thenReturn(catalogVersion1);

		// when
		handler.addRelatedObjectToUpdateToContextReplace(dragged, target, context);

		// test
		final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> valueCaptor = ArgumentCaptor.forClass(List.class);
		verify(context).setParameter(keyCaptor.capture(), valueCaptor.capture());
		assertThat(keyCaptor.getValue()).isSameAs(CategoryToCategoryDropHandler.PAREMETER_RELATED_OBJECTS_TO_UPDATE);
		assertThat(valueCaptor.getValue()).containsExactly(target);
	}

	@Test
	public void testAddRelatedObjectToUpdateToContextReplaceWhenDraggedHasSupercategoryAndMoveToCategoryInOtherCatalogVersion()
	{
		// given
		final CatalogVersionModel catalogVersion1 = mock(CatalogVersionModel.class);
		when(dragged.getCatalogVersion()).thenReturn(catalogVersion1);
		final CatalogVersionModel catalogVersion2 = mock(CatalogVersionModel.class);
		when(target.getCatalogVersion()).thenReturn(catalogVersion2);

		final CategoryModel supercategory = mock(CategoryModel.class);
		when(supercategory.getCatalogVersion()).thenReturn(catalogVersion1);
		when(dragged.getSupercategories()).thenReturn(Arrays.asList(supercategory));
		when(dragContext.getParameter(CategoryToCategoryDropHandler.CONTEXT_PARAMETER_PARENT_OBJECT)).thenReturn(supercategory);

		// when
		handler.addRelatedObjectToUpdateToContextReplace(dragged, target, context);

		// test
		final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> valueCaptor = ArgumentCaptor.forClass(List.class);
		verify(context).setParameter(keyCaptor.capture(), valueCaptor.capture());
		assertThat(keyCaptor.getValue()).isSameAs(CategoryToCategoryDropHandler.PAREMETER_RELATED_OBJECTS_TO_UPDATE);
		assertThat(valueCaptor.getValue()).containsExactly(supercategory, catalogVersion1, target);
	}
}
