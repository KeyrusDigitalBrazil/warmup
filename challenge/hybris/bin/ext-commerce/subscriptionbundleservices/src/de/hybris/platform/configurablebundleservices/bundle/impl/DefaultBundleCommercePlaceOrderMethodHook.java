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

package de.hybris.platform.configurablebundleservices.bundle.impl;

import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.i18n.L10NService;

import org.springframework.beans.factory.annotation.Required;


public class DefaultBundleCommercePlaceOrderMethodHook implements CommercePlaceOrderMethodHook
{
	private BundleCommerceCartService bundleCommerceCartService;
	private L10NService l10NService;

	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult orderModel)
			throws InvalidCartException
	{
		return; //NOPMD
	}

	@Override
	public void beforePlaceOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		if (parameter != null)
		{
			final CartModel masterCart = parameter.getCart();
			final BundleTemplateModel invalidComponent = getBundleCommerceCartService().getFirstInvalidComponentInCart(masterCart);
			if (invalidComponent != null) {
				final String cartInvalidMessage = getL10NService().getLocalizedString(
						"bundleservices.validation.cartcomponentisinvalid", new Object[]
								{invalidComponent.getParentTemplate().getName(), invalidComponent.getName()});
				throw new InvalidCartException(cartInvalidMessage);
			}
		}
	}

	@Override
	public void beforeSubmitOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		return; //NOPMD
	}

	@Required
	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
	}

	protected L10NService getL10NService()
	{
		return l10NService;
	}

	@Required
	public void setBundleCommerceCartService(final BundleCommerceCartService bundleCommerceCartService)
	{
		this.bundleCommerceCartService = bundleCommerceCartService;
	}

	protected BundleCommerceCartService getBundleCommerceCartService()
	{
		return bundleCommerceCartService;
	}
}
