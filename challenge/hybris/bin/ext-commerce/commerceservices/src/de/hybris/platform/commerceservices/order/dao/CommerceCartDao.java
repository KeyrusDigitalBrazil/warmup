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
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.Date;
import java.util.List;


/**
 * The Interface CommerceCartDao.
 */
public interface CommerceCartDao extends Dao
{

	/**
	 * Gets the cart for guid and site and user.
	 *
	 * @param guid
	 *           the guid
	 * @param site
	 *           the site
	 * @param user
	 *           the user
	 * @return the cart for guid and site and user
	 */
	CartModel getCartForGuidAndSiteAndUser(String guid, BaseSiteModel site, UserModel user);

	/**
	 * Gets the cart for guid and site.
	 *
	 * @param guid
	 *           the guid
	 * @param site
	 *           the site
	 * @return the cart for guid and site
	 */
	CartModel getCartForGuidAndSite(String guid, BaseSiteModel site);

	/**
	 * Gets the cart for code and user.
	 *
	 * @param code
	 *           the code
	 * @param user
	 *           the user
	 * @return the cart for code and user
	 */
	CartModel getCartForCodeAndUser(String code, UserModel user);

	/**
	 * Gets the cart for site and user. Excluding saved carts and quote carts. To get save cart, use
	 * {@link SaveCartDao#getSavedCartsForSiteAndUser(de.hybris.platform.commerceservices.search.pagedata.PageableData, BaseSiteModel, UserModel, List)}
	 *
	 * @param site
	 *           the site
	 * @param user
	 *           the user
	 * @return the cart for site and user
	 */
	CartModel getCartForSiteAndUser(BaseSiteModel site, UserModel user);

	/**
	 * Gets the carts for site and user. Excluding saved carts and quote carts. To get save cart, use
	 * {@link SaveCartDao#getSavedCartsForSiteAndUser(de.hybris.platform.commerceservices.search.pagedata.PageableData, BaseSiteModel, UserModel, List)}
	 *
	 * @param site
	 *           the site
	 * @param user
	 *           the user
	 * @return the carts for site and user
	 */
	List<CartModel> getCartsForSiteAndUser(BaseSiteModel site, UserModel user);

	/**
	 * Gets the carts for removal for site and user. Excluding saved carts. To get save cart for removal, use
	 * {@link SaveCartDao#getSavedCartsForRemovalForSite(BaseSiteModel)}
	 *
	 * @param modifiedBefore
	 *           the modified before
	 * @param site
	 *           the site
	 * @param user
	 *           the user
	 * @return the carts for removal for site and user
	 */
	List<CartModel> getCartsForRemovalForSiteAndUser(Date modifiedBefore, BaseSiteModel site, UserModel user);
}
