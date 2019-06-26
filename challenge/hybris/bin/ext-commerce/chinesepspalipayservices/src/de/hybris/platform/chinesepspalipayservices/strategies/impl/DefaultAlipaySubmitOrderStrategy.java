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
package de.hybris.platform.chinesepspalipayservices.strategies.impl;

import de.hybris.platform.chinesepaymentservices.model.ChinesePaymentInfoModel;
import de.hybris.platform.chinesepspalipayservices.strategies.AlipayPaymentInfoStrategy;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.order.strategies.SubmitOrderStrategy;

import org.springframework.beans.factory.annotation.Required;



public class DefaultAlipaySubmitOrderStrategy implements SubmitOrderStrategy
{
	private AlipayPaymentInfoStrategy alipayPaymentInfoStrategy;

	@Override
	public void submitOrder(final OrderModel order)
	{
		if (order.getStatus() == null || OrderStatus.CREATED.equals(order.getStatus()))
		{
			final PaymentInfoModel paymentInfoModel = order.getPaymentInfo();
			if (paymentInfoModel instanceof ChinesePaymentInfoModel)
			{
				alipayPaymentInfoStrategy.updatePaymentInfoForPlaceOrder(order);
			}
		}
	}


	@Required
	public void setAlipayPaymentInfoStrategy(final AlipayPaymentInfoStrategy alipayPaymentInfoStrategy)
	{
		this.alipayPaymentInfoStrategy = alipayPaymentInfoStrategy;
	}


	protected AlipayPaymentInfoStrategy getAlipayPaymentInfoStrategy()
	{
		return alipayPaymentInfoStrategy;
	}



}
