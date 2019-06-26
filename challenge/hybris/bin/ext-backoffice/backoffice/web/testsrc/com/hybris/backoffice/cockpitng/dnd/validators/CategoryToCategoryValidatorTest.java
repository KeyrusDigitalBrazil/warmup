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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CategoryToCategoryValidatorTest
{
	@Spy
	private CategoryToCategoryValidator validator;

	@Mock
	private CategoryModel category;
	@Mock
	private ItemModelContext context;

	@Mock
	private CategoryModel supercategoryFrom;
	@Mock
	private CategoryModel supercategoryTo;
	@Mock
	private CatalogVersionModel catalogVersionSame;
	@Mock
	private CatalogVersionModel catalogVersionOther;

	@Before
	public void setup()
	{
		when(category.getCatalogVersion()).thenReturn(catalogVersionSame);
		when(category.getItemModelContext()).thenReturn(context);
		doReturn(Boolean.TRUE).when(context).isDirty(CategoryModel.SUPERCATEGORIES);
	}

	@Test
	public void testValidateWhenCategoryHasSupercategoriesAndCopiedToCategoryInTheSameCatalogVersion()
	{
		// given
		when(supercategoryFrom.getCatalogVersion()).thenReturn(catalogVersionSame);
		when(supercategoryTo.getCatalogVersion()).thenReturn(catalogVersionSame);

		when(context.getOriginalValue(CategoryModel.SUPERCATEGORIES)).thenReturn(Arrays.asList(supercategoryFrom));
		when(category.getSupercategories()).thenReturn(Arrays.asList(supercategoryFrom, supercategoryTo));

		// when
		final boolean result = validator.validate(category);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testValidateWhenCategoryHasSupercategoriesAndCopiedToCategoryInTheOtherCatalogVersion()
	{
		// given
		when(supercategoryFrom.getCatalogVersion()).thenReturn(catalogVersionSame);
		when(supercategoryTo.getCatalogVersion()).thenReturn(catalogVersionOther);

		when(context.getOriginalValue(CategoryModel.SUPERCATEGORIES)).thenReturn(Arrays.asList(supercategoryFrom));
		when(category.getSupercategories()).thenReturn(Arrays.asList(supercategoryFrom, supercategoryTo));

		// when
		final boolean result = validator.validate(category);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testValidateWhenCategoryDoesNotHaveSupercategoriesAndCopiedToCategoryInTheSameCatalogVersion()
	{
		// given
		when(supercategoryTo.getCatalogVersion()).thenReturn(catalogVersionSame);

		when(context.getOriginalValue(CategoryModel.SUPERCATEGORIES)).thenReturn(Collections.emptyList());
		when(category.getSupercategories()).thenReturn(Arrays.asList(supercategoryTo));

		// when
		final boolean result = validator.validate(category);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testValidateWhenCategoryDoesNotHaveSupercategoriesAndCopiedToCategoryInTheOtherCatalogVersion()
	{
		// given
		when(supercategoryTo.getCatalogVersion()).thenReturn(catalogVersionOther);

		when(context.getOriginalValue(CategoryModel.SUPERCATEGORIES)).thenReturn(Collections.emptyList());
		when(category.getSupercategories()).thenReturn(Arrays.asList(supercategoryTo));

		// when
		final boolean result = validator.validate(category);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testValidateWhenCategoryHasSupercategoriesInOtherCatalogVersionAndCopiedToCategoryInTheSameCatalogVersion()
	{
		// given
		when(supercategoryFrom.getCatalogVersion()).thenReturn(catalogVersionOther);
		when(supercategoryTo.getCatalogVersion()).thenReturn(catalogVersionSame);

		when(context.getOriginalValue(CategoryModel.SUPERCATEGORIES)).thenReturn(Arrays.asList(supercategoryFrom));
		when(category.getSupercategories()).thenReturn(Arrays.asList(supercategoryFrom, supercategoryTo));

		// when
		final boolean result = validator.validate(category);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testValidateWhenCategoryHasSupercategoriesInOtherCatalogVersionAndCopiedToCategoryInOtherCatalogVersion()
	{
		// given
		when(supercategoryFrom.getCatalogVersion()).thenReturn(catalogVersionOther);
		when(supercategoryTo.getCatalogVersion()).thenReturn(catalogVersionOther);

		when(context.getOriginalValue(CategoryModel.SUPERCATEGORIES)).thenReturn(Arrays.asList(supercategoryFrom));
		when(category.getSupercategories()).thenReturn(Arrays.asList(supercategoryFrom, supercategoryTo));

		// when
		final boolean result = validator.validate(category);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testValidateWhenCategoryHasSupercategoryAndMovedToCategoryInTheSameCatalogVersion()
	{
		// given
		when(supercategoryFrom.getCatalogVersion()).thenReturn(catalogVersionSame);
		when(supercategoryTo.getCatalogVersion()).thenReturn(catalogVersionSame);

		when(context.getOriginalValue(CategoryModel.SUPERCATEGORIES)).thenReturn(Arrays.asList(supercategoryFrom));
		when(category.getSupercategories()).thenReturn(Arrays.asList(supercategoryTo));

		// when
		final boolean result = validator.validate(category);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testValidateWhenCategoryHasSupercategoryAndMovedToCategoryInTheOtherCatalogVersion()
	{
		// given
		when(supercategoryFrom.getCatalogVersion()).thenReturn(catalogVersionSame);
		when(supercategoryTo.getCatalogVersion()).thenReturn(catalogVersionOther);

		when(context.getOriginalValue(CategoryModel.SUPERCATEGORIES)).thenReturn(Arrays.asList(supercategoryFrom));
		when(category.getSupercategories()).thenReturn(Arrays.asList(supercategoryTo));

		// when
		final boolean result = validator.validate(category);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void testValidateWhenCategoryDoesNotHaveSupercategoriesAndMovedToCategoryInTheSameCatalogVersion()
	{
		// given
		when(supercategoryTo.getCatalogVersion()).thenReturn(catalogVersionSame);

		when(context.getOriginalValue(CategoryModel.SUPERCATEGORIES)).thenReturn(Collections.emptyList());
		when(category.getSupercategories()).thenReturn(Arrays.asList(supercategoryTo));

		// when
		final boolean result = validator.validate(category);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testValidateWhenCategoryDoesNotHaveSupercategoriesAndMovedToCategoryInTheOtherCatalogVersion()
	{
		// given
		when(supercategoryTo.getCatalogVersion()).thenReturn(catalogVersionOther);

		when(context.getOriginalValue(CategoryModel.SUPERCATEGORIES)).thenReturn(Collections.emptyList());
		when(category.getSupercategories()).thenReturn(Arrays.asList(supercategoryTo));

		// when
		final boolean result = validator.validate(category);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testValidateWhenCategoryHasSupercategoriesInOtherCatalogVersionAndMovedToCategoryInTheSameCatalogVersion()
	{
		// given
		when(supercategoryFrom.getCatalogVersion()).thenReturn(catalogVersionOther);
		when(supercategoryTo.getCatalogVersion()).thenReturn(catalogVersionSame);

		when(context.getOriginalValue(CategoryModel.SUPERCATEGORIES)).thenReturn(Arrays.asList(supercategoryFrom));
		when(category.getSupercategories()).thenReturn(Arrays.asList(supercategoryTo));

		// when
		final boolean result = validator.validate(category);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void testValidateWhenCategoryHasSupercategoriesInOtherCatalogVersionAndMovedToCategoryInOtherCatalogVersion()
	{
		// given
		when(supercategoryFrom.getCatalogVersion()).thenReturn(catalogVersionOther);
		when(supercategoryTo.getCatalogVersion()).thenReturn(catalogVersionOther);

		when(context.getOriginalValue(CategoryModel.SUPERCATEGORIES)).thenReturn(Arrays.asList(supercategoryFrom));
		when(category.getSupercategories()).thenReturn(Arrays.asList(supercategoryTo));

		// when
		final boolean result = validator.validate(category);

		// then
		assertThat(result).isTrue();
	}
}