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
package de.hybris.platform.chinesepspwechatpayservices.notifications;

import de.hybris.platform.chinesepspwechatpayservices.data.WeChatRawDirectPayNotification;


/**
 * Provide method to handle notification from WeChat
 */
public interface WeChatPayNotificationService
{
	/**
	 * Handling the Asyn-response of the 3rd part payment service provider server
	 *
	 * @param weChatPayNotification
	 *           The parameters of WeChatRawDirectPayNotification
	 */
	void handleWeChatPayPaymentResponse(final WeChatRawDirectPayNotification weChatPayNotification);
}
