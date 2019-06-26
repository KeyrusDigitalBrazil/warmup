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


import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.AbstractMultidimensionalProductFieldValueProvider;
import de.hybris.platform.core.model.product.ProductModel;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;


/**
 * Retrieves the multidimensional flag for a product model.
 */
public class MultidimentionalProductFlagValueProvider extends AbstractMultidimensionalProductFieldValueProvider implements Serializable
{

	@Override
	public Object getFieldValue(final ProductModel product)
	{
		Boolean isMultidimentional = null;

		final Object variants = modelService.getAttributeValue(product, "variants");
		isMultidimentional = Boolean.valueOf(CollectionUtils.isNotEmpty((Collection) variants));

		return isMultidimentional;
	}

}
