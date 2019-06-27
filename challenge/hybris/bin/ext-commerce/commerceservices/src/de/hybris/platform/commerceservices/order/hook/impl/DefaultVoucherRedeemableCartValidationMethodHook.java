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

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.strategies.hooks.CartValidationHook;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.voucher.VoucherModelService;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.model.VoucherModel;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


public class DefaultVoucherRedeemableCartValidationMethodHook implements CartValidationHook
{
	private static final String VOUCHERNOTVALID = "voucherNotValid";

	private VoucherService voucherService;
	private VoucherModelService voucherModelService;

	@Override
	public void beforeValidateCart(final CommerceCartParameter parameter, final List<CommerceCartModification> modifications)
	{
		//not implemented
	}

	@Override
	public void afterValidateCart(final CommerceCartParameter parameter, final List<CommerceCartModification> modifications)
	{
		final CartModel cartModel = parameter.getCart();
		final UserModel userModel = cartModel.getUser();
		final Collection<String> appliedVoucherCodes = getVoucherService().getAppliedVoucherCodes(cartModel);

		if (CollectionUtils.isEmpty(appliedVoucherCodes))
		{
			return;
		}
		for (final String voucherCode : appliedVoucherCodes)
		{
			final VoucherModel voucherModel = getVoucherService().getVoucher(voucherCode);
			final boolean isVoucherRedeemable = getVoucherModelService().isApplicable(voucherModel, cartModel)
					&& getVoucherModelService().isReservable(voucherModel, voucherCode, userModel);
			if (!isVoucherRedeemable)
			{
				try
				{
					getVoucherService().releaseVoucher(voucherCode, cartModel);
					final CommerceCartModification cartModificationData = new CommerceCartModification();
					cartModificationData.setStatusCode(VOUCHERNOTVALID);
					modifications.add(cartModificationData);
				}
				catch (final JaloPriceFactoryException e)
				{
					throw new JaloSystemException(e);
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


	public VoucherModelService getVoucherModelService()
	{
		return voucherModelService;
	}

	@Required
	public void setVoucherModelService(final VoucherModelService voucherModelService)
	{
		this.voucherModelService = voucherModelService;
	}
}
