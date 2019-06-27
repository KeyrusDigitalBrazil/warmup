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
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.dnd.DefaultDragAndDropContext;
import com.hybris.cockpitng.dnd.DragAndDropActionType;
import com.hybris.cockpitng.dnd.DragAndDropContext;
import com.hybris.cockpitng.dnd.DropOperationData;
import com.hybris.cockpitng.validation.model.ValidationInfo;
import com.hybris.cockpitng.validation.model.ValidationSeverity;


@RunWith(MockitoJUnitRunner.class)
public class ProductToCategoryValidatorTest
{
	@Spy
	private ProductToCategoryValidator validator;

	@Before
	public void setUp()
	{
		doAnswer(inv -> inv.getArguments()[0]).when(validator).getLabel(anyString(), anyVararg());
	}

	@Test
	public void testCannotMoveVariants()
	{
		final VariantProductModel variant = mock(VariantProductModel.class);
		final DragAndDropContext context = mock(DragAndDropContext.class);
		final DropOperationData operationData = mock(DropOperationData.class);
		when(operationData.getDragged()).thenReturn(variant);

		final List<ValidationInfo> validate = validator.validate(operationData, context);

		assertThat(validate).hasSize(1);
		assertThat(validate.get(0).getValidationMessage())
				.isEqualTo(ProductToCategoryValidator.DND_VALIDATION_VERIANT_PRODUCT_TO_CATEGORY_MSG);
		assertThat(validate.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.ERROR);
	}

	@Test
	public void shouldAddValidationErrorWhenProductHasMoreThanOneCategoryWithoutSelectedCategory()
	{
		// given
		final ProductModel product = mock(ProductModel.class);
		when(product.getSupercategories()).thenReturn(Arrays.asList(mock(CategoryModel.class), mock(CategoryModel.class)));

		final CategoryModel category = mock(CategoryModel.class);

		final DefaultDragAndDropContext dragAndDropContext = new DefaultDragAndDropContext.Builder().build();
		dragAndDropContext.setActionType(DragAndDropActionType.REPLACE);

		final DropOperationData operationData = new DropOperationData(product, category, product, dragAndDropContext, "");

		// when
		final List<ValidationInfo> validationInfos = validator.validate(operationData, dragAndDropContext);

		// then
		assertThat(validationInfos).hasSize(1);
		assertThat(validationInfos.get(0).getValidationMessage())
				.isEqualTo(ProductToCategoryValidator.DND_VALIDATION_PRODUCT_TO_CATEGORY_WITHOUT_CONTEXT_MSG);
	}

}
