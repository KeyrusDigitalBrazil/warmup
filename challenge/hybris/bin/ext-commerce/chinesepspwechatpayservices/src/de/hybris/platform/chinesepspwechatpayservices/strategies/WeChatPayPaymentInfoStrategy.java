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
package de.hybris.platform.chinesepspwechatpayservices.strategies;

import de.hybris.platform.chinesepaymentservices.model.ChinesePaymentInfoModel;
import de.hybris.platform.core.model.order.OrderModel;


/**
 * Methods to update Payment info
 */
public interface WeChatPayPaymentInfoStrategy
{
	/**
	 * update paymentinfo once payment method is chosen.
	 *
	 * @param chinesePaymentInfoModel
	 *           ChinesePaymentInfoModel to be updated {@link ChinesePaymentInfoModel}
	 * @return updated ChinesePaymentInfoModel
	 */
	ChinesePaymentInfoModel updatePaymentInfoForPayemntMethod(ChinesePaymentInfoModel chinesePaymentInfoModel);

	/**
	 * update paymentinfo once order is placed.
	 *
	 * @param order
	 *           Placed order {@link OrderModel}
	 */
	void updatePaymentInfoForPlaceOrder(OrderModel order);

}
