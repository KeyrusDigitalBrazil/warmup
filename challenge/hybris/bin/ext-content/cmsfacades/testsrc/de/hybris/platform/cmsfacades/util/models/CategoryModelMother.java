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
import de.hybris.platform.cmsfacades.util.builder.CategoryModelBuilder;

import java.util.Locale;

public class CategoryModelMother extends AbstractModelMother<CategoryModel>

{
	public static final String SANDALS = "sandals";
	public static final String HEELS = "heels";
	public static final String ACCESSORIES = "accessories";


	private CategoryDao categoryDao;

	protected CategoryModel createCategory(final String nameCode, final CatalogVersionModel catalogVersion)
	{
		return getFromCollectionOrSaveAndReturn(() -> getCategoryDao().findCategoriesByCode(nameCode),
				() -> CategoryModelBuilder.aModel() //
				.withName(nameCode, Locale.ENGLISH) //
				.withCode(nameCode) //
				.withCatalogVersion(catalogVersion) //
				.build());
	}

	public CategoryModel createSandalsCategory(final CatalogVersionModel catalogVersion)
	{
		return createCategory(SANDALS,catalogVersion);
	}

	public CategoryModel createHeelsCategory(final CatalogVersionModel catalogVersion)
	{
		return createCategory(HEELS, catalogVersion);
	}

	public CategoryModel createAccessoriesCategory(final CatalogVersionModel catalogVersion)
	{
		return createCategory(ACCESSORIES, catalogVersion);
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