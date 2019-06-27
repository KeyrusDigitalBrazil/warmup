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


import de.hybris.platform.chinesepspwechatpayservices.data.WeChatPayQueryResult;
import de.hybris.platform.chinesepspwechatpayservices.data.WeChatRawDirectPayNotification;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.WeChatPayPaymentTransactionEntryModel;

import java.util.Optional;


/**
 * Methods to change payment transaction and payment transaction entries by given params
 */
public interface WeChatPayPaymentTransactionStrategy
{

	/**
	 * Save new transaction with entry for some order once new direct_pay request is issued.
	 *
	 * @param orderModel
	 *           order launching direct_pay
	 */
	void createForNewRequest(OrderModel orderModel);


	/**
	 * Update weChatPayPaymentTransaction and entry once notify data from weChat is received.
	 *
	 * @param orderModel
	 *           Order handled by the notify data {@link OrderModel}
	 * @param weChatPayNotifyResponseData
	 *           Notify data from weChatPay {@link WeChatRawDirectPayNotification}
	 */
	void updateForNotification(OrderModel orderModel, WeChatRawDirectPayNotification weChatPayNotifyResponseData);

	/**
	 *
	 * Save WeChatPayPaymentTransactionEntry once payment status check (WechatPay's check trade) is completed
	 *
	 * @param orderModel
	 *           order launching check trade
	 * @param weChatPayQueryResult
	 *           Data needed for launching check trade
	 * @return WeChatPayPaymentTransactionEntryModel The PaymentTransactionEntry which is updated by
	 *         checkTradeResponseData
	 *
	 */
	Optional<WeChatPayPaymentTransactionEntryModel> saveForStatusCheck(OrderModel orderModel,
			WeChatPayQueryResult weChatPayQueryResult);


}
