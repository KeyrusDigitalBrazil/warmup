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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.core.context.CockpitContext;
import com.hybris.cockpitng.core.context.impl.DefaultCockpitContext;
import com.hybris.cockpitng.dnd.DefaultDragAndDropContext;
import com.hybris.cockpitng.dnd.DragAndDropActionType;
import com.hybris.cockpitng.dnd.DragAndDropContext;
import com.hybris.cockpitng.services.dnd.DragAndDropConfigurationService;


@RunWith(MockitoJUnitRunner.class)
public class CategoryToCatalogVersionDropHandlerTest
{
	@Spy
	@InjectMocks
	private CategoryToCatalogVersionDropHandler handler;

	@Mock
	private CategoryModel category;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private CatalogVersionModel anotherCatalogVersion;
	@Mock
	private DragAndDropContext context;
	@Mock
	private DragAndDropConfigurationService dragAndDropConfigurationService;

	@Before
	public void setup()
	{
		when(dragAndDropConfigurationService.getDefaultActionType()).thenReturn(DragAndDropActionType.REPLACE);
	}

	@Test
	public void shouldAllowToDropOnTheSameCatalogVersion()
	{
		// given
		when(category.getCatalogVersion()).thenReturn(catalogVersion);

		// when
		final boolean result = handler.canHandleDrop(category, catalogVersion, context);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldNotAllowToDropOnAnotherCatalogVersion()
	{
		// given
		final CatalogVersionModel anotherCatalogVersion = mock(CatalogVersionModel.class);
		when(category.getCatalogVersion()).thenReturn(anotherCatalogVersion);

		// when
		final boolean result = handler.canHandleDrop(category, catalogVersion, context);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldMoveCategoryAsRootCategoryWhenCategoryHasOnlySingleSupercategory()
	{
		final CockpitContext draggedContext = new DefaultCockpitContext();
		draggedContext.setParameter(CategoryToCatalogVersionDropHandler.PARENT_OBJECT, mock(CategoryModel.class));

		// given
		final CategoryModel supercategory = mock(CategoryModel.class);
		final List<CategoryModel> supercategories = Arrays.asList(supercategory);
		given(category.getSupercategories()).willReturn(supercategories);
		given(category.getCatalogVersion()).willReturn(catalogVersion);
		given(context.getDraggedContext()).willReturn(draggedContext);

		// when
 		handler.handleDrop(Collections.singletonList(category), catalogVersion, context);

		// then
		final ArgumentCaptor<List> modifiedSupercategories = ArgumentCaptor.forClass(List.class);
		verify(category).setSupercategories(modifiedSupercategories.capture());
		assertThat(modifiedSupercategories.getValue()).isEmpty();
	}

	@Test
	public void shouldLeaveCategoryAsARootCategoryWhenCategoryIsAlreadyRootCategory()
	{
		final CockpitContext draggedContext = new DefaultCockpitContext();
		draggedContext.setParameter(CategoryToCatalogVersionDropHandler.PARENT_OBJECT, mock(CatalogVersionModel.class));

		// given
		given(category.getSupercategories()).willReturn(new ArrayList<>());
		given(category.getCatalogVersion()).willReturn(catalogVersion);
		given(context.getDraggedContext()).willReturn(draggedContext);

		// when
		handler.handleDrop(Collections.singletonList(category), catalogVersion, context);

		// then
		final ArgumentCaptor<List> modifiedSupercategories = ArgumentCaptor.forClass(List.class);
		verify(category).setSupercategories(modifiedSupercategories.capture());
		assertThat(modifiedSupercategories.getValue()).isEmpty();
	}

	@Test
	public void shouldRemoveSingleSupercategory()
	{
		// given
		final CategoryModel firstSupercategory = mock(CategoryModel.class);
		final CategoryModel secondSupercategory = mock(CategoryModel.class);
		final CategoryModel thirdSupercategory = mock(CategoryModel.class);
		final List<CategoryModel> supercategories = Arrays.asList(firstSupercategory, secondSupercategory, thirdSupercategory);
		final CockpitContext dragContext = mock(CockpitContext.class);
		given(category.getSupercategories()).willReturn(supercategories);
		given(context.getDraggedContext()).willReturn(dragContext);
		given(dragContext.getParameter("parentObject")).willReturn(secondSupercategory);
		given(category.getCatalogVersion()).willReturn(catalogVersion);

		// when
		handler.handleDrop(Collections.singletonList(category), catalogVersion, context);

		// then
		final ArgumentCaptor<List> modifiedSupercategories = ArgumentCaptor.forClass(List.class);
		verify(category).setSupercategories(modifiedSupercategories.capture());
		assertThat(modifiedSupercategories.getValue()).contains(firstSupercategory, thirdSupercategory);
	}

	@Test
	public void shouldAddParentToObjectsToRefresh()
	{
		// given
		final CockpitContext draggedContext = new DefaultCockpitContext();
		final CategoryModel parentCategory = mock(CategoryModel.class);
		draggedContext.setParameter(CategoryToCatalogVersionDropHandler.PARENT_OBJECT, parentCategory);
		final DragAndDropContext context = new DefaultDragAndDropContext.Builder().withDraggedContext(draggedContext).build();

		// when
		handler.addRelatedObjectToUpdateToContext(category, catalogVersion, context);

		// then
		assertThat(context.getParameter("relatedObjectsToUpdate")).isInstanceOf(List.class);
		assertThat((List)context.getParameter("relatedObjectsToUpdate")).containsExactly(parentCategory);
	}

	@Test
	public void shouldAddCatalogVersionToObjectsToRefresh()
	{
		// given
		final CockpitContext draggedContext = new DefaultCockpitContext();
		final CategoryModel parentCategory = mock(CategoryModel.class);
		final List<CategoryModel> supercategories = new ArrayList<>();
		supercategories.add(parentCategory);
		draggedContext.setParameter(CategoryToCatalogVersionDropHandler.PARENT_OBJECT, parentCategory);
		final DragAndDropContext context = new DefaultDragAndDropContext.Builder().withDraggedContext(draggedContext).build();
		given(category.getSupercategories()).willReturn(supercategories);

		// when
		handler.addRelatedObjectToUpdateToContext(category, catalogVersion, context);

		// then
		assertThat(context.getParameter("relatedObjectsToUpdate")).isInstanceOf(List.class);
		assertThat((List)context.getParameter("relatedObjectsToUpdate")).containsExactly(parentCategory, catalogVersion);
	}

}
