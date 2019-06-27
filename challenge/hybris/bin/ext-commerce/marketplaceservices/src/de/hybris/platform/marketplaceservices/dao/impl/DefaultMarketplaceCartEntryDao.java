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
package de.hybris.platform.marketplaceservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.dao.impl.DefaultCartEntryDao;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.marketplaceservices.dao.MarketplaceCartEntryDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.List;

/**
 *
 */
public class DefaultMarketplaceCartEntryDao extends DefaultCartEntryDao implements MarketplaceCartEntryDao
{

	protected static final String CART_CODE = "code";

	protected static final String FIND_UNSALEBALE_CARTENTRIES_IN_CART = "select {A:" + CartEntryModel.PK + "} from {"
			+ CartEntryModel._TYPECODE + " as A JOIN " + ProductModel._TYPECODE + " AS B ON {A:" + CartEntryModel.PRODUCT + "}={B:"
			+ ProductModel.PK + "} JOIN " + CartModel._TYPECODE
			+ " as C on {A:" + CartEntryModel.ORDER + "} = {C:" + CartModel.PK + "}} WHERE {B:" + ProductModel.SALEABLE
			+ "}=false and {C:" + CartModel.CODE + "}=?" + CART_CODE;

	@Override
	public List<CartEntryModel> findUnSaleableCartEntries(final CartModel cart)
	{
		validateParameterNotNull(cart, "Cart must not be null");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_UNSALEBALE_CARTENTRIES_IN_CART);
		query.addQueryParameter(CART_CODE, cart.getCode());
		return getFlexibleSearchService().<CartEntryModel> search(query).getResult();
	}

}
