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
package de.hybris.platform.customerinterestsfacades.productinterest;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestRelationData;
import de.hybris.platform.notificationservices.enums.NotificationType;

import java.util.List;
import java.util.Optional;


/**
 * Facade to deal with the ProductInterests
 */
public interface ProductInterestFacade
{
	/**
	 * This method will save the new or edited interest
	 *
	 * @param productInterest
	 *           the ProductInterest to be saved
	 */
	void saveProductInterest(ProductInterestData productInterest);

	/**
	 * This method will remove the interest
	 *
	 * @param productInterest
	 *           the ProductInterest to be removed
	 */
	void removeProductInterest(ProductInterestData productInterest);

	/**
	 * This method will get the particular interest of the current customer according to the productcode and the
	 * notificationType
	 *
	 * @param productcode
	 *           the code of the product to be found
	 * @param notificationType
	 *           the notificationType of the wanted ProductInterest
	 * @return the ProductInterestData
	 */
	Optional<ProductInterestData> getProductInterestDataForCurrentCustomer(String productcode,
			NotificationType notificationType);

	/**
	 * Remove all interests for specific product for current customer
	 *
	 * @param productCode
	 *           the code of the product
	 */
	void removeAllProductInterests(final String productCode);

	/**
	 * Find interests watched by current customer
	 *
	 * @param pageableData
	 *           the pagination data
	 * @return Map whose key is ProductMode and value is the Map of NotificationType as key and creation time as value.
	 */
	List<ProductInterestRelationData> getProductsByCustomerInterests(final PageableData pageableData);

	/**
	 * Find interests watched by current customer
	 *
	 * @param searchPageData
	 *           the search page data
	 * @return sorted and paged product interests relation data
	 */
	SearchPageData<ProductInterestRelationData> getPaginatedProductInterestsByCustomer(final SearchPageData searchPageData);

	/**
	 * Find the total size of search result
	 *
	 * @param pageableData
	 *           the pagination data
	 * @return the total size of search result
	 */
	int getProductsCountByCustomerInterests(PageableData pageableData);
}
