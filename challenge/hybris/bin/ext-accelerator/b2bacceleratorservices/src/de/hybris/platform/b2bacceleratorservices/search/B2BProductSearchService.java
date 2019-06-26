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
package de.hybris.platform.b2bacceleratorservices.search;

import de.hybris.platform.b2bacceleratorservices.product.B2BProductService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collection;


/**
 * @deprecated Since 6.0. Use {@link de.hybris.platform.b2bacceleratorservices.product.B2BProductService} instead.
 *
 * @param <T>
 *           type of item to be searched that extends {@link ProductModel}.
 *
 */
@Deprecated
public interface B2BProductSearchService<T extends ProductModel>
{
	/**
	 * Gets all visible {@link de.hybris.platform.core.model.product.ProductModel} for a given collection of SKUs.
	 *
	 * @deprecated Since 6.0. Use {@link B2BProductService#getProductsForSkus(Collection, PageableData)} instead.
	 *
	 * @param skus
	 *           collection of product IDs
	 * @param pageableData
	 *           pagination information
	 * @return List of paginated {@link ProductModel} objects
	 *
	 */
	@Deprecated
	SearchPageData<T> findProductsBySkus(Collection<String> skus, PageableData pageableData);

}
