/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.b2bordermanagementfacades.order.converters.populator;


import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.ordermanagementfacades.order.converters.populator.OrderRequestReversePopulator;
import de.hybris.platform.ordermanagementfacades.order.data.OrderRequestData;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * B2bOrdermanagementfacades populator for converting {@link OrderRequestData}
 */
public class B2bOrderRequestReversePopulator extends OrderRequestReversePopulator
{
	/**
	 * Extracts payment information from the {@link OrderRequestData} and assigns it to {@link OrderModel}
	 *
	 * @param source
	 * 		the {@link OrderRequestData}
	 * @param target
	 * 		the {@link OrderModel}
	 */
	@Override
	protected void addPaymentInformation(final OrderRequestData source, final OrderModel target)
	{
		final List<PaymentTransactionModel> paymentTransactions = new ArrayList<>();
		source.getPaymentTransactions().forEach(paymentTransactionData -> {
			final PaymentTransactionModel paymentTransaction = getModelService().create(PaymentTransactionModel.class);
			getPaymentTransactionReverseConverter().convert(paymentTransactionData, paymentTransaction);
			paymentTransaction.setCode(target.getUser().getUid() + UUID.randomUUID());
			if (paymentTransaction.getInfo() == null)
			{
				final PaymentInfoModel paymentInfoModel = new PaymentInfoModel();
				paymentInfoModel.setOwner(getUserService().getUserForUID(source.getUser().getUid()));
				paymentTransaction.setInfo(paymentInfoModel);
			}
			paymentTransaction.getInfo().setCode(target.getUser().getUid() + UUID.randomUUID());
			paymentTransaction.getInfo().setUser(target.getUser());
			paymentTransactions.add(paymentTransaction);
		});
		target.setPaymentTransactions(paymentTransactions);

		target.setPaymentInfo(target.getPaymentTransactions().iterator().next().getInfo());

	}
}
