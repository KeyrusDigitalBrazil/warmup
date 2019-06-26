/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapproductconfigsomservices.cart.impl;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.cart.impl.DefaultCartService;
import de.hybris.platform.sap.sapproductconfigsomservices.bolfacade.CPQBolCartFacade;
import de.hybris.platform.sap.sapproductconfigsomservices.cart.CPQCartService;


/**
 * Basic cart functions for SAP synchronous order management. In this case, the cart will be created in the back end
 * session, it does not touch the hybris persistence.<br>
 * The class synchronizes accesses to the BOL object representing the cart, as this is not thread safe. Multi-threaded
 * accesses can happen although we use request sequencing, since also filters might call cart facades.
 *
 */
public class CPQDefaultCartService extends DefaultCartService implements CPQCartService
{

	@Override
	public String addConfigurationToCart(final ConfigModel configModel)
	{
		final Basket currentCart = getBolCartFacade().getCart();
		synchronized (currentCart)
		{
			return getBolCartFacade().addConfigurationToCart(configModel);
		}
	}


	@Override
	public String updateConfigurationInCart(final String key, final ConfigModel configModel)
	{
		final Basket currentCart = getBolCartFacade().getCart();
		synchronized (currentCart)
		{
			return getBolCartFacade().updateConfigurationInCart(key, configModel);
		}
	}


	/**
	 * @return the bolCartFacade
	 */
	@Override
	public CPQBolCartFacade getBolCartFacade()
	{
		final CPQBolCartFacade bolCartFacade = (CPQBolCartFacade) super.getBolCartFacade();
		return bolCartFacade;
	}


	@Override
	public Item getItemByKey(final String itemKey)
	{
		final Basket currentCart = getBolCartFacade().getCart();
		synchronized (currentCart)
		{
			return currentCart.getItem(new TechKey(itemKey));
		}
	}


}
