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
package de.hybris.platform.entitlementfacades.product.converters.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import org.springframework.beans.factory.annotation.Required;


/**
 * SOLR Populator for {@link de.hybris.platform.subscriptionservices.model.SubscriptionProductModel}
 */
public class SearchResultProductPopulator<SOURCE extends SearchResultValueData, TARGET extends ProductData>
		implements Populator<SOURCE, TARGET>
{

	private ProductService productService;
	private Populator<ProductModel, ProductData> productEntitlementCollectionPopulator;

	@Override
	public void populate(final SOURCE source, final TARGET target)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("source", source);
		ServicesUtil.validateParameterNotNullStandardMessage("target", target);
		final ProductModel productModel = getProductService().getProductForCode(target.getCode());
		getProductEntitlementCollectionPopulator().populate(productModel, target);
	}

	protected <T> T getValue(final SOURCE source, final String propertyName)
	{
		if (source.getValues() == null)
		{
			return null;
		}
		// DO NOT REMOVE the cast (T) below, while it should be unnecessary it is required by the javac compiler
		return (T) source.getValues().get(propertyName);
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected Populator<ProductModel, ProductData> getProductEntitlementCollectionPopulator()
	{
		return productEntitlementCollectionPopulator;
	}

	@Required
	public void setProductEntitlementCollectionPopulator(final Populator<ProductModel, ProductData> productEntitlementCollectionPopulator)
	{
		this.productEntitlementCollectionPopulator = productEntitlementCollectionPopulator;
	}
}
