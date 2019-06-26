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
package de.hybris.platform.customerinterestsservices.productinterest;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Date;
import java.util.Map;
import java.util.Optional;


/**
 * Service to deal with the ProductInterests
 */
public interface ProductInterestService
{
	/**
	 * This method will save the new or edited interest
	 *
	 * @param productInterest
	 *           the ProductInterestModel to be saved
	 */
	void saveProductInterest(final ProductInterestModel productInterest);

	/**
	 * This method will remove the interest
	 *
	 * @param productInterest
	 *           the ProductInterestModel to be removed
	 */
	void removeProductInterest(final ProductInterestModel productInterest);

	/**
	 * This method is used to find the ProductInterestModel by productModel, customerModel, notificationType,
	 * baseStore,baseSite
	 *
	 * @param productModel
	 *           the product of the ProductInterest
	 * @param customerModel
	 *           the customer of the ProductInterest
	 * @param notificationType
	 *           the notificationType of the ProductInterest
	 * @param baseStore
	 *           the baseStore of the ProductInterest
	 * @param baseSite
	 *           the baseSite of the ProductInterest
	 * @return ProductInterestModel if found and an empty Optional otherwise
	 */
	Optional<ProductInterestModel> getProductInterest(final ProductModel productModel, final CustomerModel customerModel,
			final NotificationType notificationType, final BaseStoreModel baseStore, final BaseSiteModel baseSite);

	/**
	 * This method will remove the interest for the current customer by product
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
	Map<ProductModel, Map<NotificationType, Date>> getProductsByCustomerInterests(final PageableData pageableData);

	/**
	 * Find all interests watched by current customer
	 *
	 * @return Map whose key is ProductMode and value is the Map of NotificationType as key and creation time as value.
	 */
	Map<ProductModel, Map<NotificationType, Date>> findProductInterestsByCustomer();

	/**
	 * Find the total size of search result
	 *
	 * @param pageableData
	 *           the pagination data
	 * @return the total size of search result
	 */
	int getProductsCountByCustomerInterests(PageableData pageableData);
}
