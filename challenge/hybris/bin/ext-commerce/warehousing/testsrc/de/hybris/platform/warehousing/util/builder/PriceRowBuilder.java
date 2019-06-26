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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.model.PriceRowModel;


public class PriceRowBuilder
{
	private final PriceRowModel model;

	private PriceRowBuilder()
	{
		model = new PriceRowModel();
	}

	public static PriceRowBuilder aModel()
	{
		return new PriceRowBuilder();
	}

	private PriceRowModel getModel()
	{
		return this.model;
	}

	public PriceRowModel build()
	{
		return getModel();
	}

	public PriceRowBuilder withCurrency(final CurrencyModel currency)
	{
		getModel().setCurrency(currency);
		return this;
	}

	public PriceRowBuilder withPrice(final Double price)
	{
		getModel().setPrice(price);
		return this;
	}

	public PriceRowBuilder withProduct(final ProductModel product)
	{
		getModel().setProduct(product);
		return this;
	}

	public PriceRowBuilder withUnit(final UnitModel unit)
	{
		getModel().setUnit(unit);
		return this;
	}

	public PriceRowBuilder withCatalogVersion(final CatalogVersionModel catalogVersion)
	{
		getModel().setCatalogVersion(catalogVersion);
		return this;
	}

	public PriceRowBuilder withUser(final UserModel user)
	{
		getModel().setUser(user);
		return this;
	}

	public PriceRowBuilder withProductId(final String productId)
	{
		getModel().setProductId(productId);
		return this;
	}
}
