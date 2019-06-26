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
package de.hybris.platform.variants.interceptor;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.variants.model.GenericVariantProductModel;
import de.hybris.platform.variants.model.VariantCategoryModel;
import de.hybris.platform.variants.model.VariantValueCategoryModel;

import java.util.Collection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


@UnitTest
public class GenericVariantProductValidateInterceptorUnitTest
{

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	private GenericVariantProductValidateInterceptor interceptor;
	private L10NService l10NService;

	@Before
	public void setUp()
	{
		l10NService = mock(L10NService.class);
		interceptor = new GenericVariantProductValidateInterceptor();
		interceptor.setL10NService(l10NService);
	}

	@Test
	public void testValidateBaseProductSuperCategoriesBaseProductIsNull() throws InterceptorException
	{
		final String errorString = "error.genericvariantproduct.nobaseproduct";
		when(l10NService.getLocalizedString(Mockito.contains(errorString), Mockito.any())).thenReturn(errorString);

		final GenericVariantProductModel genericVariant = mock(GenericVariantProductModel.class);
		when(genericVariant.getCode()).thenReturn("genericVariantCode");
		final Collection<CategoryModel> variantValueCategories = asList(mock(CategoryModel.class));

		expectedException.expectMessage(errorString);
		interceptor.validateBaseProductSuperCategories(genericVariant, variantValueCategories);
	}

	@Test
	public void testValidateBaseProductSuperCategoriesNotMatchingCategorySize() throws InterceptorException
	{
		final String errorString = "error.genericvariantproduct.nosameamountofvariantcategories";
		when(l10NService.getLocalizedString(Mockito.contains(errorString), Mockito.any())).thenReturn(errorString);

		final GenericVariantProductModel genericVariant = mock(GenericVariantProductModel.class);
		when(genericVariant.getCode()).thenReturn("genericVariantCode");
		final ProductModel productModel = mock(ProductModel.class);
		when(genericVariant.getBaseProduct()).thenReturn(productModel);
		when(productModel.getSupercategories())
				.thenReturn(asList(mock(VariantCategoryModel.class), mock(VariantCategoryModel.class)));
		final Collection<CategoryModel> variantValueCategories = asList(mock(VariantCategoryModel.class));

		// superCategories.size() != variantValueCategories.size()
		expectedException.expectMessage(errorString);
		interceptor.validateBaseProductSuperCategories(genericVariant, variantValueCategories);
	}

	@Test
	public void testValidateBaseProductSuperCategoriesNotMatchingSupercategories() throws InterceptorException
	{
		final String errorString = "error.genericvariantproduct.variantcategorynotinbaseproduct";
		when(l10NService.getLocalizedString(Mockito.contains(errorString), Mockito.any())).thenReturn(errorString);

		final GenericVariantProductModel genericVariant = mock(GenericVariantProductModel.class);
		when(genericVariant.getCode()).thenReturn("genericVariantCode");
		final ProductModel productModel = mock(ProductModel.class);
		when(genericVariant.getBaseProduct()).thenReturn(productModel);
		final VariantCategoryModel variantCategoryModel = mock(VariantCategoryModel.class);

		when(productModel.getSupercategories()).thenReturn(asList(mock(CategoryModel.class), variantCategoryModel));
		final VariantValueCategoryModel variantValueCategoryModel = mock(VariantValueCategoryModel.class);
		final Collection<CategoryModel> variantValueCategories = asList(variantValueCategoryModel);
		when(variantValueCategoryModel.getSupercategories()).thenReturn(asList(mock(VariantCategoryModel.class)));

		// superCategories.size() == variantValueCategories.size()
		expectedException.expectMessage(errorString);
		interceptor.validateBaseProductSuperCategories(genericVariant, variantValueCategories);
	}

	@Test
	public void testValidateBaseProductSuperCategoriesSupercategoriesOfNonSupportedType() throws InterceptorException
	{
		final String errorString = "error.genericvariantproduct.nosameamountofvariantcategories";
		when(l10NService.getLocalizedString(Mockito.contains(errorString), Mockito.any())).thenReturn(errorString);

		final GenericVariantProductModel genericVariant = mock(GenericVariantProductModel.class);
		when(genericVariant.getCode()).thenReturn("genericVariantCode");
		final ProductModel productModel = mock(ProductModel.class);
		when(genericVariant.getBaseProduct()).thenReturn(productModel);
		final VariantCategoryModel variantCategoryModel = mock(VariantCategoryModel.class);

		when(productModel.getSupercategories()).thenReturn(asList(mock(CategoryModel.class), variantCategoryModel));
		final VariantValueCategoryModel variantValueCategoryModel = mock(VariantValueCategoryModel.class);
		final Collection<CategoryModel> variantValueCategories = asList(variantValueCategoryModel);
		when(variantValueCategoryModel.getSupercategories()).thenReturn(asList(mock(CategoryModel.class)));

		expectedException.expectMessage(errorString);
		interceptor.validateBaseProductSuperCategories(genericVariant, variantValueCategories);
	}

	@Test
	public void testValidateBaseProductSuperCategoriesOK() throws InterceptorException
	{
		final GenericVariantProductModel genericVariant = mock(GenericVariantProductModel.class);
		when(genericVariant.getCode()).thenReturn("genericVariantCode");
		final ProductModel productModel = mock(ProductModel.class);
		when(genericVariant.getBaseProduct()).thenReturn(productModel);
		final VariantCategoryModel variantCategoryModel = mock(VariantCategoryModel.class);

		when(productModel.getSupercategories()).thenReturn(asList(mock(CategoryModel.class), variantCategoryModel));
		final VariantValueCategoryModel variantValueCategoryModel = mock(VariantValueCategoryModel.class);
		final Collection<CategoryModel> variantValueCategories = asList(variantValueCategoryModel);
		when(variantValueCategoryModel.getSupercategories()).thenReturn(asList(variantCategoryModel));

		interceptor.validateBaseProductSuperCategories(genericVariant, variantValueCategories);
	}

}
