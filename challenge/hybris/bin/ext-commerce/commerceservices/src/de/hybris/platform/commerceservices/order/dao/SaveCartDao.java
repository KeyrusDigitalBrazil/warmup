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
package de.hybris.platform.commerceservices.order.dao;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;


/**
 * Interface for dao object to handle the saved cart feature
 */
public interface SaveCartDao extends Dao
{

	/**
	 * Returns a list of saved carts which have to be deleted
	 *
	 * @param site
	 * @return list of saved carts for removal
	 */
	List<CartModel> getSavedCartsForRemovalForSite(BaseSiteModel site);

	/**
	 * Retrieve carts by user and basesite where order status equals ones of the status in the list.
	 *
	 * @param pageableData
	 * @param user
	 *           mandatory parameter
	 * @param baseSite
	 *           optional parameter
	 * @param orderStatus
	 *           optional list
	 * @return list of saved user carts
	 */
	SearchPageData<CartModel> getSavedCartsForSiteAndUser(PageableData pageableData, BaseSiteModel baseSite, UserModel user,
			List<OrderStatus> orderStatus);

	/**
	 * Return the total number of the saved carts by user and basesite
	 *
	 * @param baseSite
	 * @return the total number
	 */
	Integer getSavedCartsCountForSiteAndUser(BaseSiteModel baseSite, UserModel user);
}