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
package de.hybris.platform.commerceservices.order.hook.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.model.VoucherInvalidationModel;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


public class DefaultVoucherRedemptionPlaceOrderMethodHook implements CommercePlaceOrderMethodHook
{
	private VoucherService voucherService;


	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult orderModel)
			throws InvalidCartException
	{
		// not implemented
	}

	@Override
	public void beforePlaceOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		// not implemented
	}

	@Override
	public void beforeSubmitOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		validateParameterNotNullStandardMessage("parameter", parameter);
		validateParameterNotNullStandardMessage("result", result);

		final CartModel cartModel = parameter.getCart();
		final OrderModel orderModel = result.getOrder();

		validateParameterNotNullStandardMessage("cartModel", cartModel);
		validateParameterNotNullStandardMessage("orderModel", orderModel);

		final Collection<String> appliedVoucherCodes = getVoucherService().getAppliedVoucherCodes(cartModel);

		if (CollectionUtils.isNotEmpty(appliedVoucherCodes))
		{
			for (final String voucherCode : appliedVoucherCodes)
			{
				final VoucherInvalidationModel voucherInvalidationModel = getVoucherService().redeemVoucher(voucherCode, orderModel);
				if (voucherInvalidationModel == null)
				{
					throw new InvalidCartException(
							String.format("Order [%s] contains invalid voucher: [%s]", orderModel.getCode(), voucherCode));
				}
			}
		}
	}

	public VoucherService getVoucherService()
	{
		return voucherService;
	}

	@Required
	public void setVoucherService(final VoucherService voucherService)
	{
		this.voucherService = voucherService;
	}
}
