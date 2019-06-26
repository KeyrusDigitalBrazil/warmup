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
package com.hybris.backoffice.excel.exporting;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ExcelExportParams;
import com.hybris.backoffice.excel.data.SelectedAttribute;


@RunWith(MockitoJUnitRunner.class)
public class DefaultExcelSupercategorySelectionDecoratorTest
{
	private static final List<ItemModel> NO_ITEMS_TO_EXPORT = emptyList();
	private static final List<SelectedAttribute> NO_SELECTED_ATTRIBUTES = emptyList();
	private static final Collection<ExcelAttribute> NO_ADDITIONAL_ATTRIBUTES = emptySet();

	@Mock
	TypeService mockedTypeService;
	@Mock
	PermissionCRUDService mockedPermissionCRUDService;
	@InjectMocks
	DefaultExcelSupercategorySelectionDecorator decorator;

	@Test
	public void shouldAddSupercategoryAttribute()
	{
		// given
		final Collection<ExcelAttribute> classificationAttributes = Collections.singletonList(new ExcelClassificationAttribute());
		final List<SelectedAttribute> selectedAttributes = new ArrayList<>();
		final ExcelExportParams excelExportParams = new ExcelExportParams(NO_ITEMS_TO_EXPORT, selectedAttributes,
				classificationAttributes);
		final boolean hasAccessToSupercategoriesAttribute = true;

		final ComposedTypeModel productType = mock(ComposedTypeModel.class);
		final AttributeDescriptorModel supercategoriesAttribute = mock(AttributeDescriptorModel.class);
		given(mockedTypeService.getComposedTypeForCode(ProductModel._TYPECODE)).willReturn(productType);
		given(mockedTypeService.getAttributeDescriptor(productType, ProductModel.SUPERCATEGORIES))
				.willReturn(supercategoriesAttribute);
		given(mockedPermissionCRUDService.canReadAttribute(supercategoriesAttribute))
				.willReturn(hasAccessToSupercategoriesAttribute);

		// when
		final ExcelExportParams result = decorator.decorate(excelExportParams);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getSelectedAttributes()).hasSize(1);
		assertThat(result.getSelectedAttributes().get(0).getAttributeDescriptor()).isEqualTo(supercategoriesAttribute);
	}

	@Test
	public void shouldNotDecorateIfSupercategoriesAttributeIsAlreadySelected()
	{
		// given
		final List<SelectedAttribute> selectedAttributesIncludingSupercategories = Collections
				.singletonList(prepareSupercategoriesSelectedAttribute());
		final ExcelExportParams excelExportParams = new ExcelExportParams(NO_ITEMS_TO_EXPORT,
				selectedAttributesIncludingSupercategories, NO_ADDITIONAL_ATTRIBUTES);

		// when
		final ExcelExportParams result = decorator.decorate(excelExportParams);

		// then
		assertThat(result).isSameAs(excelExportParams);
		verifyZeroInteractions(mockedTypeService, mockedPermissionCRUDService);
	}

	@Test
	public void shouldNotDecorateIfNotASingleClassificationAttributeIsSelected()
	{
		// given
		final ExcelExportParams excelExportParams = new ExcelExportParams(NO_ITEMS_TO_EXPORT, NO_SELECTED_ATTRIBUTES,
				NO_ADDITIONAL_ATTRIBUTES);

		// when
		final ExcelExportParams result = decorator.decorate(excelExportParams);

		// then
		assertThat(result).isSameAs(excelExportParams);
		verifyZeroInteractions(mockedTypeService, mockedPermissionCRUDService);
	}

	@Test
	public void shouldNotDecorateIfDoesNotHavePermissionToReadSupercategoriesAttribute()
	{
		// given
		final Collection<ExcelAttribute> classificationAttributes = Collections.singletonList(new ExcelClassificationAttribute());
		final ExcelExportParams excelExportParams = new ExcelExportParams(NO_ITEMS_TO_EXPORT, NO_SELECTED_ATTRIBUTES,
				classificationAttributes);
		final boolean hasAccessToSupercategoriesAttribute = false;

		final ComposedTypeModel productType = mock(ComposedTypeModel.class);
		final AttributeDescriptorModel supercategoriesAttribute = mock(AttributeDescriptorModel.class);
		given(mockedTypeService.getComposedTypeForCode(ProductModel._TYPECODE)).willReturn(productType);
		given(mockedTypeService.getAttributeDescriptor(productType, ProductModel.SUPERCATEGORIES))
				.willReturn(supercategoriesAttribute);
		given(mockedPermissionCRUDService.canReadAttribute(supercategoriesAttribute))
				.willReturn(hasAccessToSupercategoriesAttribute);

		// when
		final ExcelExportParams result = decorator.decorate(excelExportParams);

		// then
		assertThat(result).isSameAs(excelExportParams);
	}

	@Test
	public void shouldHaveOrderInjectable()
	{
		// given
		final int orderValue = 1337;

		// when
		decorator.setOrder(orderValue);

		// then
		assertThat(decorator.getOrder()).isEqualTo(orderValue);
	}

	private SelectedAttribute prepareSupercategoriesSelectedAttribute()
	{
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		given(attributeDescriptorModel.getQualifier()).willReturn(ProductModel.SUPERCATEGORIES);
		return new SelectedAttribute(attributeDescriptorModel);
	}
}
