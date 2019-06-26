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

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;

import java.util.Locale;


public class ProductModelBuilder
{
	private final ProductModel model;

	private ProductModelBuilder()
	{
		model = new ProductModel();
	}

	private ProductModel getModel()
	{
		return this.model;
	}

	public static ProductModelBuilder aModel()
	{
		return new ProductModelBuilder();
	}

	public ProductModel build()
	{
		return getModel();
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

	public ProductModelBuilder withName(final String name, final Locale locale)
	{
		getModel().setName(name, locale);
		return this;
	}

	public ProductModelBuilder withUnit(final UnitModel unit)
	{
		getModel().setUnit(unit);
		return this;
	}

	public ProductModelBuilder withPrice(final Double price)
	{
		getModel().setPriceQuantity(price);
		return this;
	}
	
	public ProductModelBuilder withApprovalStatus(final ArticleApprovalStatus approvalStatus)
	{
		getModel().setApprovalStatus(approvalStatus);
		return this;
	}

	public ProductModelBuilder withStartLineNumber(final Integer startLineNumber)
	{
		getModel().setStartLineNumber(startLineNumber);
		return this;
	}

	public ProductModelBuilder withOwner(final ItemModel owner)
	{
		getModel().setOwner(owner);
		return this;
	}

}
