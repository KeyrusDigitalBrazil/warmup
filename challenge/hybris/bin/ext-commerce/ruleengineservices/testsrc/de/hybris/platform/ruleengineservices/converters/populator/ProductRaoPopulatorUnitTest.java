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
package de.hybris.platform.ruleengineservices.converters.populator;

import static de.hybris.platform.ruleengineservices.util.TestUtil.createNewConverter;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.impl.DefaultCategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ruleengineservices.rao.CategoryRAO;
import de.hybris.platform.ruleengineservices.rao.ProductRAO;
import de.hybris.platform.ruleengineservices.util.ProductUtils;
import de.hybris.platform.variants.model.GenericVariantProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductRaoPopulatorUnitTest
{
	private ProductRaoPopulator productRaoPopulator;
	private ProductUtils productUtils;

	@Before
	public void setUp() throws Exception
	{
		productRaoPopulator = new ProductRaoPopulator();
		productRaoPopulator.setCategoryConverter(createNewConverter(CategoryRAO.class, new CategoryRaoPopulator()));
		final CategoryService catService = new DefaultCategoryService()
		{
			@Override
			public Collection<CategoryModel> getAllSupercategoriesForCategory(final CategoryModel category)
			{
				final CategoryModel categoryModel = new CategoryModel();
				categoryModel.setCode("super" + category.getCode());
				return Collections.singletonList(categoryModel);
			}
		};
		productRaoPopulator.setCategoryService(catService);
		productUtils = mock(ProductUtils.class);
		productRaoPopulator.setProductUtils(productUtils);
	}

	protected Collection<CategoryModel> getCategories(final String... categoryCodes)
	{
		final Collection<CategoryModel> categories = new ArrayList<CategoryModel>();
		for (final String code : Arrays.asList(categoryCodes))
		{
			final CategoryModel cat = new CategoryModel();
			cat.setCode(code);
			categories.add(cat);
		}
		return categories;
	}

	@Test
	public void testProductModelPopulating()
	{
		final ProductModel productModel = new ProductModel();
		productModel.setCode("prod1");
		productModel.setSupercategories(getCategories("cat1", "cat2"));
		final ProductRAO productRao = new ProductRAO();
		productRaoPopulator.populate(productModel, productRao);
		Assert.assertEquals(productModel.getCode(), productRao.getCode());
		// check that categories and supercategories are and only present
		final Set<String> expectedCategoryCodes = new HashSet<String>(Arrays.asList("cat1", "cat2", "supercat1", "supercat2"));
		Assert.assertEquals(expectedCategoryCodes.size(), productRao.getCategories().size());
		Assert.assertEquals(
				expectedCategoryCodes.size(),
				productRao.getCategories().stream().filter(c -> expectedCategoryCodes.contains(c.getCode()))
						.collect(Collectors.toSet()).size());
	}

	@Test
	public void testGenericVariantProductModelPopulating()
	{
		final GenericVariantProductModel genericVariantProductModel = new GenericVariantProductModel();
		genericVariantProductModel.setCode("code2");
		genericVariantProductModel.setSupercategories(getCategories("cat1", "cat2"));
		final ProductModel productModel = new ProductModel();
		productModel.setSupercategories(getCategories("cat3", "cat4"));
		genericVariantProductModel.setBaseProduct(productModel);

		when(productUtils.getAllBaseProducts(genericVariantProductModel)).thenReturn(Sets.newHashSet(productModel));

		final ProductRAO productRao = new ProductRAO();
		productRaoPopulator.populate(genericVariantProductModel, productRao);
		Assert.assertEquals(genericVariantProductModel.getCode(), productRao.getCode());
		// check that categories and supercategories are and only present
		final Set<String> expectedCategoryCodes = new HashSet<String>(Arrays.asList("cat1", "cat2", "supercat1", "supercat2",
				"cat3", "cat4", "supercat3", "supercat4"));
		Assert.assertEquals(expectedCategoryCodes.size(), productRao.getCategories().size());
		Assert.assertEquals(
				expectedCategoryCodes.size(),
				productRao.getCategories().stream().filter(c -> expectedCategoryCodes.contains(c.getCode()))
						.collect(Collectors.toSet()).size());
	}

	@Test
	public void testProductModelPopulatingNoCategories()
	{
		final ProductModel productModel = new ProductModel();
		productModel.setCode("prod1");
		final ProductRAO productRao = new ProductRAO();
		productRaoPopulator.populate(productModel, productRao);
		Assert.assertEquals(productModel.getCode(), productRao.getCode());
		Assert.assertEquals(0, productRao.getCategories().size());
	}

	@Test
	public void testGenericVariantProductModelNoCategoriesPopulating()
	{
		final GenericVariantProductModel genericVariantProductModel = new GenericVariantProductModel();
		genericVariantProductModel.setCode("code2");
		final ProductModel productModel = new ProductModel();
		genericVariantProductModel.setBaseProduct(productModel);
		final ProductRAO productRao = new ProductRAO();
		productRaoPopulator.populate(genericVariantProductModel, productRao);
		Assert.assertEquals(genericVariantProductModel.getCode(), productRao.getCode());
		Assert.assertEquals(0, productRao.getCategories().size());
	}
}
