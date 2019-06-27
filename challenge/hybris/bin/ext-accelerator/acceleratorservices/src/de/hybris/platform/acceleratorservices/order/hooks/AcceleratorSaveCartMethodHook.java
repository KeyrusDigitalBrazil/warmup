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
package de.hybris.platform.acceleratorservices.order.hooks;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.hook.CommerceSaveCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.impl.DefaultCartService;
import de.hybris.platform.servicelayer.session.SessionService;

import org.apache.log4j.Logger;


/**
 * Hook to remove the session cart & fetch a new empty cart for the user. It can be enabled for a site by appending the
 * site uid to the property acceleratorservices.commercesavecart.sessioncart.hook.enabled.{siteUid}, by default it is
 * not enabled.
 */
public class AcceleratorSaveCartMethodHook implements CommerceSaveCartMethodHook
{
	private static final Logger LOG = Logger.getLogger(AcceleratorSaveCartMethodHook.class);

	private CartService cartService;
	private SessionService sessionService;
	private SiteConfigService siteConfigService;

	@Override
	public void beforeSaveCart(final CommerceSaveCartParameter parameters) throws CommerceSaveCartException
	{
		// Auto-generated method stub
	}

	@Override
	public void afterSaveCart(final CommerceSaveCartParameter parameters, final CommerceSaveCartResult saveCartResult)
			throws CommerceSaveCartException
	{
		if (getSiteConfigService().getBoolean("acceleratorservices.commercesavecart.sessioncart.hook.enabled", false)
				&& getCartService().hasSessionCart())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Removing the session cart attribute & fetching a new session cart.");
			}
			getSessionService().removeAttribute(DefaultCartService.SESSION_CART_PARAMETER_NAME);
			getCartService().getSessionCart();
		}
	}


	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the siteConfigService
	 */
	public SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	/**
	 * @param siteConfigService
	 *           the siteConfigService to set
	 */
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}

}
