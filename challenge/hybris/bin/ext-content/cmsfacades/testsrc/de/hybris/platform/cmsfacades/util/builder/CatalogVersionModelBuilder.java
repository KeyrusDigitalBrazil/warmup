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
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.List;

public class CatalogVersionModelBuilder {

	private final CatalogVersionModel model;

	private CatalogVersionModelBuilder()
	{
		model = new CatalogVersionModel();
	}

	private CatalogVersionModelBuilder(final CatalogVersionModel model)
	{
		this.model = model;
	}

	protected CatalogVersionModel getModel()
	{
		return this.model;
	}

	public static CatalogVersionModelBuilder aModel()
	{
		return new CatalogVersionModelBuilder();
	}

	public static CatalogVersionModelBuilder fromModel(final CatalogVersionModel model)
	{
		return new CatalogVersionModelBuilder(model);
	}

	public CatalogVersionModelBuilder withCatalog(final CatalogModel catalog)
	{
		getModel().setCatalog(catalog);
		return this;
	}

	public CatalogVersionModelBuilder withVersion(final String version)
	{
		getModel().setVersion(version);
		return this;
	}

	public CatalogVersionModelBuilder withActive(final Boolean active)
	{
		getModel().setActive(active);
		return this;
	}

	public CatalogVersionModelBuilder withLanguages(final List<LanguageModel> lang)
	{
		getModel().setLanguages(lang);
		return this;
	}

	public CatalogVersionModelBuilder withDefaultCurrency(final CurrencyModel currency)
	{
		getModel().setDefaultCurrency(currency);
		return this;
	}

	public CatalogVersionModel build()
	{
		return this.getModel();
	}

}
