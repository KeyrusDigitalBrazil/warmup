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
package de.hybris.platform.chinesepaymentfacades.payment.populator;

import de.hybris.platform.chinesepaymentfacades.payment.data.ChinesePaymentInfoData;
import de.hybris.platform.chinesepaymentservices.checkout.strategies.ChinesePaymentServicesStrategy;
import de.hybris.platform.chinesepaymentservices.model.ChinesePaymentInfoModel;
import de.hybris.platform.chinesepaymentservices.payment.ChinesePaymentService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.PaymentModeService;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


public class CartChinesePaymentInfoPopulator implements Populator<CartModel, CartData>
{
	private static final String PAYMENT_SERVICE_PROVIDER_SERVICE_SUFFIX = "PaymentService";

	private ChinesePaymentServicesStrategy chinesePaymentServicesStrategy;

	private PaymentModeService paymentModeService;

	@Override
	public void populate(final CartModel source, final CartData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		final ChinesePaymentInfoModel chinesePaymentInfomodel = source.getChinesePaymentInfo();
		if (chinesePaymentInfomodel != null)
		{
			final ChinesePaymentInfoData chinesePaymentInfoData = new ChinesePaymentInfoData();
			chinesePaymentInfoData.setId(chinesePaymentInfomodel.getCode());
			chinesePaymentInfoData.setPaymentProvider(chinesePaymentInfomodel.getPaymentProvider());
			chinesePaymentInfoData.setServiceType(chinesePaymentInfomodel.getServiceType().getCode());

			final String paymentProviderName = paymentModeService
					.getPaymentModeForCode(source.getChinesePaymentInfo().getPaymentProvider()).getName();
			chinesePaymentInfoData.setPaymentProviderName(paymentProviderName);

			final ChinesePaymentService chinesePaymentService = chinesePaymentServicesStrategy
					.getPaymentService(chinesePaymentInfomodel.getPaymentProvider() + PAYMENT_SERVICE_PROVIDER_SERVICE_SUFFIX);
			chinesePaymentInfoData.setPaymentProviderLogo(chinesePaymentService.getPspLogoUrl());
			target.setChinesePaymentInfo(chinesePaymentInfoData);
		}
	}

	protected ChinesePaymentServicesStrategy getChinesePaymentServicesStrategy()
	{
		return chinesePaymentServicesStrategy;
	}

	@Required
	public void setChinesePaymentServicesStrategy(final ChinesePaymentServicesStrategy chinesePaymentServicesStrategy)
	{
		this.chinesePaymentServicesStrategy = chinesePaymentServicesStrategy;
	}

	protected PaymentModeService getPaymentModeService()
	{
		return paymentModeService;
	}

	@Required
	public void setPaymentModeService(final PaymentModeService paymentModeService)
	{
		this.paymentModeService = paymentModeService;
	}
}
