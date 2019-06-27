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
package de.hybris.platform.selectivecartservices.daos.impl;

import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.selectivecartservices.daos.SelectiveCartDao;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.wishlist2.model.Wishlist2Model;


/**
 * Default implementation of {@link SelectiveCartDao}
 */
public class DefaultSelectiveCartDao extends DefaultGenericDao<Wishlist2Model> implements SelectiveCartDao
{

	private static final String FIND_WISHLIST_BY_NAME = "SELECT {" + Wishlist2Model.PK + "} FROM {" + Wishlist2Model._TYPECODE
			+ "} WHERE {" + Wishlist2Model.USER + "} = ?" + Wishlist2Model.USER + " AND {" + Wishlist2Model.NAME + "} = ?"
			+ Wishlist2Model.NAME;

	private static final String FIND_CARTENTRY_BY_CARTPKANDENTRYNUM = "select {A:" + CartEntryModel.PK + "} from {"
			+ CartEntryModel._TYPECODE + " as A JOIN " + CartModel._TYPECODE + " as C on {A:" + CartEntryModel.ORDER + "} = {C:"
			+ CartModel.PK + "}} WHERE {A:" + CartEntryModel.ENTRYNUMBER + "}=?entryNumber and {C:" + CartModel.CODE + "}=?cartCode";

	public DefaultSelectiveCartDao()
	{
		super(Wishlist2Model._TYPECODE);
	}

	@Override
	public Wishlist2Model findWishlistByName(final UserModel user, final String name)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_WISHLIST_BY_NAME);
		query.addQueryParameter(Wishlist2Model.USER, user);
		query.addQueryParameter(Wishlist2Model.NAME, name);
		return getFlexibleSearchService().searchUnique(query);
	}

	@Override
	public CartEntryModel findCartEntryByCartCodeAndEntryNumber(final String cartCode, final Integer entryNumber)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_CARTENTRY_BY_CARTPKANDENTRYNUM);
		query.addQueryParameter("entryNumber", entryNumber);
		query.addQueryParameter("cartCode", cartCode);
		return getFlexibleSearchService().searchUnique(query);
	}

}
