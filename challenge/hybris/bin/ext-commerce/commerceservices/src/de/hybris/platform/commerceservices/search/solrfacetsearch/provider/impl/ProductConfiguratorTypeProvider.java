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

import de.hybris.platform.commerceservices.product.ProductConfigurableChecker;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Enriches product search document with dynamically calculated property {@code configuratorType}.
 *
 * @see de.hybris.platform.commercefacades.product.data.ProductData#getConfigurable()
 */
public class ProductConfiguratorTypeProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider
{
	private FieldNameProvider fieldNameProvider;
	private ProductConfigurableChecker productConfigurableChecker;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		ServicesUtil.validateParameterNotNullStandardMessage("model", model);
		if (model instanceof ProductModel)
		{
			final ProductModel product = (ProductModel) model;
			final String configuratorType = getProductConfigurableChecker().getFirstConfiguratorType(product);
			final String configuratorTypeIndex = configuratorType != null ? configuratorType : StringUtils.EMPTY;
			final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, null);
			return fieldNames.stream().map(fieldName -> new FieldValue(fieldName, configuratorTypeIndex))
					.collect(Collectors.toList());
		}
		else
		{
			if (model instanceof ItemModel)
			{
				throw new FieldValueProviderException(
						"Can not populate 'configuratorType' field: model " + ((ItemModel) model).getPk() + "is not a product");
			}
			throw new FieldValueProviderException("Can not populate 'configuratorType' field: model's class "
					+ model.getClass().getName() + " can not be cast to ProductModel");
		}
	}

	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	protected ProductConfigurableChecker getProductConfigurableChecker()
	{
		return productConfigurableChecker;
	}

	@Required
	public void setProductConfigurableChecker(final ProductConfigurableChecker productConfigurableChecker)
	{
		this.productConfigurableChecker = productConfigurableChecker;
	}
}
