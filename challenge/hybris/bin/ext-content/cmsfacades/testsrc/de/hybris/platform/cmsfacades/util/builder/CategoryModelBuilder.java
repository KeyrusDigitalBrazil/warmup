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
package de.hybris.platform.cmsfacades.util.builder;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;

import java.util.Locale;


public class CategoryModelBuilder
{

	private final CategoryModel model;

	private CategoryModelBuilder()
	{
		model = new CategoryModel();
	}

	protected CategoryModel getModel()
	{
		return this.model;
	}

	public static CategoryModelBuilder aModel()
	{
		return new CategoryModelBuilder();
	}

	public static CategoryModelBuilder fromModel(final CategoryModel model)
	{
		return new CategoryModelBuilder();
	}


	public CategoryModel build()
	{
		return getModel();
	}

	public CategoryModelBuilder withCatalogVersion(final CatalogVersionModel catalogue)
	{
		getModel().setCatalogVersion(catalogue);
		return this;
	}

	public CategoryModelBuilder withName(final String name, final Locale locale)
	{
		getModel().setName(name, locale);
		return this;
	}

	public CategoryModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

}
