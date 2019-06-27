/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.daos.CategoryDao;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cmsfacades.util.builder.ProductCategoryModelBuilder;

import java.util.Locale;


public class ProductCategoryModelMother extends AbstractModelMother<CategoryModel>
{
	public static final String ELECTRONICS = "electronics";
	public static final String CARS = "cars";

	private CategoryDao categoryDao;

	protected CategoryModel getCategoryByCode(final String categoryCode, final CatalogVersionModel catalogVersion)
	{
		return getFromCollectionOrSaveAndReturn(() -> getCategoryDao().findCategoriesByCode(categoryCode),
				() -> ProductCategoryModelBuilder.aModel() //
						.withName(categoryCode, Locale.ENGLISH) //
						.withCatalogVersion(catalogVersion) //
						.withCode(categoryCode)
						.build());
	}

	public CategoryModel createDefaultCategory(final CatalogVersionModel catalogVersion)
	{
		return getCategoryByCode(ELECTRONICS, catalogVersion);
	}

	public CategoryModel createCarsCategory(final CatalogVersionModel catalogVersion)
	{
		return getCategoryByCode(CARS, catalogVersion);
	}

	public CategoryDao getCategoryDao()
	{
		return categoryDao;
	}

	public void setCategoryDao(final CategoryDao categoryDao)
	{
		this.categoryDao = categoryDao;
	}
}
