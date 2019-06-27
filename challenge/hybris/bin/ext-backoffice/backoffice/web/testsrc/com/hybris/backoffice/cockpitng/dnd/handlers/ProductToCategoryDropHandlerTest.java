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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

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

import com.hybris.backoffice.widgets.contextpopulator.ContextPopulator;
import com.hybris.cockpitng.core.context.CockpitContext;
import com.hybris.cockpitng.dnd.DragAndDropContext;


@RunWith(MockitoJUnitRunner.class)
public class ProductToCategoryDropHandlerTest
{
	@Spy
	private ProductToCategoryDropHandler handler;

	@Mock
	private ProductModel product;
	@Mock
	private CategoryModel category;
	@Mock
	private CategoryModel selectedContextCategory;
	@Mock
	private DragAndDropContext context;
	@Mock
	private CockpitContext dragContext;

	@Before
	public void setUp()
	{
		when(context.getDraggedContext()).thenReturn(dragContext);
		when(dragContext.getParameter(ContextPopulator.SELECTED_OBJECT)).thenReturn(selectedContextCategory);
	}

	@Test
	public void testHandleAppend()
	{
		// given
		final CategoryModel supercategory = mock(CategoryModel.class);
		when(product.getSupercategories()).thenReturn(Arrays.asList(supercategory));

		// when
		final ProductModel result = handler.copyProductToCategory(product, category, context);

		// test
		assertThat(result).isSameAs(product);

		verify(product).getSupercategories();
		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(product).setSupercategories(captor.capture());
		assertThat(captor.getValue()).containsExactly(supercategory, category);
		verifyNoMoreInteractions(product);
	}

	@Test
	public void testHandleAppendWithAppend()
	{
		//given
		when(product.getSupercategories()).thenReturn(Collections.singleton(selectedContextCategory));

		//when
		handler.copyProductToCategory(product, category, context);

		//then
		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(product).getSupercategories();
		verify(product).setSupercategories(captor.capture());

		assertThat(captor.getValue()).contains(category, selectedContextCategory);
	}

	@Test
	public void testHandleReplace()
	{
		// when
		final ProductModel result = handler.moveProductToCategory(product, category, context);

		// test
		assertThat(result).isSameAs(product);

		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(product).getSupercategories();
		verify(product).setSupercategories(captor.capture());
		assertThat(captor.getValue()).containsExactly(category);
		verifyNoMoreInteractions(product);
	}

}
