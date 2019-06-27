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
package com.hybris.backoffice.cockpitng.dnd.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.dnd.DefaultDragAndDropContext;
import com.hybris.cockpitng.dnd.DragAndDropActionType;
import com.hybris.cockpitng.dnd.DropOperationData;
import com.hybris.cockpitng.validation.model.ValidationInfo;
import com.hybris.cockpitng.validation.model.ValidationSeverity;


@RunWith(MockitoJUnitRunner.class)
public class CategoryToCatalogVersionValidatorTest
{
	@InjectMocks
	private CategoryToCatalogVersionValidator validator;

	@Mock
	private DefaultDragAndDropContext context;

	@Mock
	private DropOperationData operationData;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testIsApplicable() throws Exception
	{
		// given
		when(operationData.getDragged()).thenReturn(mock(CategoryModel.class));
		when(operationData.getTarget()).thenReturn(mock(CatalogVersionModel.class));

		// when
		final boolean applicable = validator.isApplicable(operationData, context);

		// then
		assertThat(applicable).isTrue();
	}

	@Test
	public void testIsApplicableClassificationClass() throws Exception
	{
		// given
		when(operationData.getDragged()).thenReturn(mock(ClassificationClassModel.class));
		when(operationData.getTarget()).thenReturn(mock(CatalogVersionModel.class));

		// when
		final boolean applicable = validator.isApplicable(operationData, context);

		// then
		assertThat(applicable).isTrue();
	}

	@Test
	public void testIsNotApplicable() throws Exception
	{
		// given
		when(operationData.getDragged()).thenReturn(mock(ProductModel.class));
		when(operationData.getTarget()).thenReturn(mock(CatalogVersionModel.class));

		// when
		final boolean applicable = validator.isApplicable(operationData, context);

		// then
		assertThat(applicable).isFalse();
	}

	@Test
	public void testValidateNoAppend() throws Exception
	{
		// given
		when(operationData.getDragged()).thenReturn(mock(CategoryModel.class));
		when(operationData.getTarget()).thenReturn(mock(CatalogVersionModel.class));
		when(context.getActionType()).thenReturn(DragAndDropActionType.APPEND);

		// when
		final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

		// then
		assertThat(validationInfos).hasSize(1);
		assertThat(validationInfos.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.ERROR);
	}

	@Test
	public void testValidateHasSupercategory() throws Exception
	{
		// given
		final CategoryModel draggedCategoryModel = mock(CategoryModel.class);
		when(operationData.getDragged()).thenReturn(draggedCategoryModel);
		when(operationData.getModified()).thenReturn(draggedCategoryModel);
		when(operationData.getTarget()).thenReturn(mock(CatalogVersionModel.class));
		when(context.getActionType()).thenReturn(DragAndDropActionType.REPLACE);
		when(draggedCategoryModel.getSupercategories()).thenReturn(Collections.singletonList(mock(CategoryModel.class)));
		

		// when
		final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

		// then
		assertThat(validationInfos).hasSize(1);
		assertThat(validationInfos.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.WARN);
	}

	@Test
	public void testValidateOk() throws Exception
	{
		// given
		final CategoryModel draggedCategoryModel = mock(CategoryModel.class);
		when(operationData.getDragged()).thenReturn(draggedCategoryModel);
		when(operationData.getModified()).thenReturn(draggedCategoryModel);
		when(operationData.getTarget()).thenReturn(mock(CatalogVersionModel.class));
		when(context.getActionType()).thenReturn(DragAndDropActionType.REPLACE);
		when(draggedCategoryModel.getSupercategories()).thenReturn(Collections.emptyList());


		// when
		final List<ValidationInfo> validationInfos = validator.validate(operationData, context);

		// then
		assertThat(validationInfos).isEmpty();
	}
}
