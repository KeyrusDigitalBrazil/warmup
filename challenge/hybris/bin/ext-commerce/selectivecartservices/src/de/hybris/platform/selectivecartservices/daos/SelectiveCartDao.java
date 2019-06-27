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
package de.hybris.platform.selectivecartservices.daos;

import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;


/**
 * Looks up items related to selective cart
 */
public interface SelectiveCartDao
{

	/**
	 * Finds wishlist by name for the current user
	 * 
	 * @param user
	 *           the current user
	 * @param name
	 *           the Wishlist2Model name
	 * @return the Wishlist2Model
	 */
	Wishlist2Model findWishlistByName(UserModel user, String name);

	/**
	 * Finds cart entry by cart code and entry number
	 * 
	 * @param cartCode
	 *           the cart code
	 * @param entryNumber
	 *           the entry number
	 * @return the CartEntryModel
	 */
	CartEntryModel findCartEntryByCartCodeAndEntryNumber(String cartCode, Integer entryNumber);
}
