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
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Locale;


public class ProductModelBuilder
{
	private final ProductModel model;

	private ProductModelBuilder(final ProductModel model)
	{
		this.model = model;
	}

	private ProductModelBuilder()
	{
		this.model = new ProductModel();
	}

	public ProductModel getModel()
	{
		return model;
	}

	public static ProductModelBuilder aModel()
	{
		return new ProductModelBuilder();
	}

	public static ProductModelBuilder fromModel(final ProductModel model)
	{
		return new ProductModelBuilder(model);
	}

	public ProductModelBuilder withName(final String name)
	{
		getModel().setName(name);
		return this;
	}

	public ProductModelBuilder withName(final String name, final Locale locale)
	{
		getModel().setName(name, locale);
		return this;
	}

	public ProductModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}
	
	public ProductModelBuilder withCatalogVersion(final CatalogVersionModel catalogVersion)
	{
		getModel().setCatalogVersion(catalogVersion);
		return this;
	}

	
	public ProductModel build()
	{
		return this.getModel();
	}
}
