/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.catalog.model.CatalogModel;

import java.util.Locale;


public class CatalogModelBuilder
{
	private final CatalogModel model;

	private CatalogModelBuilder()
	{
		model = new CatalogModel();
	}

	private CatalogModel getModel()
	{
		return this.model;
	}

	public static CatalogModelBuilder aModel()
	{
		return new CatalogModelBuilder();
	}

	public CatalogModel build()
	{
		return getModel();
	}

	public CatalogModelBuilder withId(final String id)
	{
		getModel().setId(id);
		return this;
	}

	public CatalogModelBuilder withName(final String name, final Locale locale)
	{
		getModel().setName(name, locale);
		return this;
	}

	public CatalogModelBuilder withDefaultCatalog(final Boolean defaultCatalog)
	{
		getModel().setDefaultCatalog(defaultCatalog);
		return this;
	}

}
