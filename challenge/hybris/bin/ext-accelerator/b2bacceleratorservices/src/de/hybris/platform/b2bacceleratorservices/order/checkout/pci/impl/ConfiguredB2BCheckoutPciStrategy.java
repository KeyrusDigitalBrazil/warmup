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
package de.hybris.platform.b2bacceleratorservices.order.checkout.pci.impl;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorservices.payment.constants.PaymentConstants;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class ConfiguredB2BCheckoutPciStrategy extends AbstractB2BCheckoutPciStrategy
{
	private SiteConfigService siteConfigService;

	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	@Required
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}

	@Override
	public CheckoutPciOptionEnum getSubscriptionPciOption()
	{
		final CheckoutPciOptionEnum checkoutPciOption = getSiteConfiguredSubscriptionPciOption();
		if (checkoutPciOption != null)
		{
			return checkoutPciOption;
		}

		return getDefaultCheckoutPciStrategy().getSubscriptionPciOption();
	}

	protected CheckoutPciOptionEnum getSiteConfiguredSubscriptionPciOption()
	{
		// Check if there is any HOP configuration
		final boolean enabled = getSiteConfigService().getBoolean(PaymentConstants.PaymentProperties.HOP_PCI_STRATEGY_ENABLED,
				false);
		if (enabled)
		{
			return CheckoutPciOptionEnum.HOP;
		}
		return null;
	}
}
