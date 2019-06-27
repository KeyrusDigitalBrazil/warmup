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
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.ProductSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * This ValueProvider will provide the product stock level status for all the variant of a product. The stock level
 * count changes so frequently that it is not sensible to index the count directly, but rather to map the count to a
 * status (or band) and then index the status.
 */
public class VariantProductStockLevelStatusValueProvider extends ProductStockLevelStatusValueProvider
{

	private ProductSource productSource;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		final List<FieldValue> allFieldValues = new ArrayList<FieldValue>();
		final Set<ProductModel> products = getProductSource().getProducts(model);
		for (final ProductModel productModel : products)
		{
			final Collection<FieldValue> fieldValues = super.getFieldValues(indexConfig, indexedProperty, productModel);
			allFieldValues.addAll(fieldValues);
		}
		return allFieldValues;
	}

	protected ProductSource getProductSource()
	{
		return productSource;
	}

	@Required
	public void setProductSource(final ProductSource productSource)
	{
		this.productSource = productSource;
	}
}
