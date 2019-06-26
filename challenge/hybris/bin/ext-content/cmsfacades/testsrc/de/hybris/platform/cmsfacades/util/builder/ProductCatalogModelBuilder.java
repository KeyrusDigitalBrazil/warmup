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

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;

import java.util.Locale;
import java.util.Set;

public class ProductCatalogModelBuilder {

	private final CatalogModel model;

	private ProductCatalogModelBuilder()
	{
		model = new CatalogModel();
	}

	private ProductCatalogModelBuilder(final CatalogModel model)
	{
		this.model = model;
	}

	protected CatalogModel getModel()
	{
		return this.model;
	}

	public static ProductCatalogModelBuilder aModel()
	{
		return new ProductCatalogModelBuilder();
	}

	public static ProductCatalogModelBuilder fromModel(final CatalogModel model)
	{
		return new ProductCatalogModelBuilder(model);
	}

	public CatalogModel build()
	{
		return this.getModel();
	}

	public ProductCatalogModelBuilder withId(final String id)
	{
		getModel().setId(id);
		return this;
	}

	public ProductCatalogModelBuilder withName(final String name, final Locale loc)
	{
		getModel().setName(name, loc);
		return this;
	}

	public ProductCatalogModelBuilder withCatalogVersions(final Set<CatalogVersionModel> catalogVersions)
	{
		getModel().setCatalogVersions(catalogVersions);
		return this;
	}

	public ProductCatalogModelBuilder withDefault(final boolean isDefault)
	{
		getModel().setDefaultCatalog(isDefault);
		return this;
	}
}
