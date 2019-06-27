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
import de.hybris.platform.acceleratorservices.constants.AcceleratorServicesConstants;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.hook.CommerceSaveCartRestorationMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;

import org.apache.log4j.Logger;


/**
 * Hook to set the saveTime as null before performing the restore. It can be enabled for a site by appending the site
 * uid to the property acceleratorservices.commercesavecart.restoration.savetime.hook.enabled.{siteUid}, by default it
 * is not enabled.
 */
public class AcceleratorSaveCartRestorationHook implements CommerceSaveCartRestorationMethodHook
{
	private static final Logger LOG = Logger.getLogger(AcceleratorSaveCartRestorationHook.class);

	private SiteConfigService siteConfigService;

	@Override
	public void beforeRestoringCart(final CommerceCartParameter parameters) throws CommerceCartRestorationException
	{

		if (getSiteConfigService().getBoolean(AcceleratorServicesConstants.SAVECART_RESTORATION_SAVETIMEHOOK_ENABLED, false))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Setting saveTime as null on the cart to be restored");
			}
			final CartModel cartModel = parameters.getCart();
			//Convert save cart to active cart
			cartModel.setSaveTime(null);
		}
	}

	@Override
	public void afterRestoringCart(final CommerceCartParameter parameters) throws CommerceCartRestorationException
	{
		// Auto-generated method stub
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
