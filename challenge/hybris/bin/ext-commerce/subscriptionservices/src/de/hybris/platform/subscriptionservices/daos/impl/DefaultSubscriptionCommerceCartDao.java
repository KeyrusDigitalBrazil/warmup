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
package de.hybris.platform.subscriptionservices.daos.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.dao.impl.DefaultCommerceCartDao;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Subscription specific extension of {@link DefaultCommerceCartDao} returning only master carts checking if Cart.parent
 * is NULL.
 */
public class DefaultSubscriptionCommerceCartDao extends DefaultCommerceCartDao
{
	@Override
	public List<CartModel> getCartsForSiteAndUser(final BaseSiteModel site, final UserModel user)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put("site", site);
		params.put("user", user);
		return doSearch(
				"SELECT {pk} FROM {Cart} WHERE {site} = ?site AND {user} = ?user and {parent} IS NULL ORDER BY {modifiedtime} DESC",
				params, CartModel.class);
	}
}
