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
package de.hybris.platform.marketplaceservices.catalog.impl;

import static java.util.Collections.singletonList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class MarketplaceCategorySourceTest
{
	private static final String TEST_ROOT_CAT_CODE = "TestRootCat";
	private static final String TEST_CATS_QUALIFIER = "supercategories";


	private MarketplaceCategorySource marketplaceCategorySource;
	@Mock
	private ModelService modelService;
	@Mock
	private CategoryService categoryService;
	@Mock
	private VariantProductModel model;
	@Mock
	private ProductModel baseProduct;

	@Mock
	private IndexedProperty indexedProperty;
	@Mock
	private IndexConfig indexConfig;
	@Mock
	private CatalogVersionModel catalogVersion;
	// |-rootCategory (Brands)
	// |----classificationClass
	// |----category1
	// |-------category2
	// |----category3
	@Mock
	private CategoryModel category1;
	@Mock
	private CategoryModel category2;
	@Mock
	private CategoryModel category3;
	@Mock
	private CategoryModel rootCategory;
	@Mock
	private ClassificationClassModel classificationClass;

	private boolean includeClassificationClasses;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		configure();
	}

	protected void configure()
	{
		marketplaceCategorySource = new MarketplaceCategorySource();

		marketplaceCategorySource.setModelService(modelService);
		marketplaceCategorySource.setCategoriesQualifier(TEST_CATS_QUALIFIER);
		marketplaceCategorySource.setIncludeClassificationClasses(includeClassificationClasses);
		marketplaceCategorySource.setRootCategory(TEST_ROOT_CAT_CODE);
		marketplaceCategorySource.setCategoryService(categoryService);

		given(rootCategory.getSupercategories()).willReturn(null);
		given(rootCategory.getAllSupercategories()).willReturn(null);
		final List<CategoryModel> superCats = new ArrayList<CategoryModel>();
		superCats.add(rootCategory);
		superCats.add(classificationClass);
		given(category1.getSupercategories()).willReturn(superCats);
		given(category1.getAllSupercategories()).willReturn(superCats);
		given(category2.getSupercategories()).willReturn(singletonList(category1));
		given(category2.getAllSupercategories()).willReturn(singletonList(category1));
		given(category3.getSupercategories()).willReturn(singletonList(rootCategory));
		given(category3.getAllSupercategories()).willReturn(singletonList(rootCategory));

		given(model.getBaseProduct()).willReturn(baseProduct);
		given(modelService.getAttributeValue(Matchers.<Object> anyObject(), eq(TEST_CATS_QUALIFIER)))
				.willReturn(singletonList(category2));

		given(category2.getCatalogVersion()).willReturn(catalogVersion);
		given(categoryService.getCategoryForCode(catalogVersion, TEST_ROOT_CAT_CODE)).willReturn(rootCategory);

	}


	@Test
	public void testGetCategories()
	{
		//Test ProductModel
		Set<CategoryModel> superCategories = new HashSet<>();
		superCategories.add(category2);
		given(model.getSupercategories()).willReturn(superCategories);

		Collection<CategoryModel> result = marketplaceCategorySource.getCategoriesForConfigAndProperty(indexConfig,
				indexedProperty, model);

		Assert.assertNotNull(result);
		Assert.assertTrue(result.contains(rootCategory));
		Assert.assertTrue(result.contains(category1));
		Assert.assertTrue(result.contains(category2));

		//Test VariantProductModel
		final ProductModel model2 = Mockito.spy(new ProductModel());
		given(model2.getSupercategories()).willReturn(superCategories);

		result = marketplaceCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty, model2);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.contains(rootCategory));
		Assert.assertTrue(result.contains(category1));
		Assert.assertTrue(result.contains(category2));
	}

}
