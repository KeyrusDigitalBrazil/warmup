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
package de.hybris.platform.chinesepspwechatpayservices.notifications.impl;

import de.hybris.platform.chinesepaymentservices.order.service.ChineseOrderService;
import de.hybris.platform.chinesepspwechatpayservices.constants.WeChatPaymentConstants;
import de.hybris.platform.chinesepspwechatpayservices.dao.WeChatPayOrderDao;
import de.hybris.platform.chinesepspwechatpayservices.data.WeChatRawDirectPayNotification;
import de.hybris.platform.chinesepspwechatpayservices.notifications.WeChatPayNotificationService;
import de.hybris.platform.chinesepspwechatpayservices.strategies.WeChatPayPaymentTransactionStrategy;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Global class for Chinesepspwechatpayservices notifications impl.
 */
public class DefaultWeChatPayNotificationService implements WeChatPayNotificationService
{
	private ModelService modelService;

	private WeChatPayPaymentTransactionStrategy weChatPayPaymentTransactionStrategy;

	private WeChatPayOrderDao weChatPayOrderDao;

	private ChineseOrderService chineseOrderService;

	private static final Logger LOG = Logger.getLogger(DefaultWeChatPayNotificationService.class.getName());

	@Override
	public void handleWeChatPayPaymentResponse(final WeChatRawDirectPayNotification weChatPayNotification)
	{
		getWeChatPayOrderDao().findOrderByCode(weChatPayNotification.getOutTradeNo()).ifPresent(
				orderModel -> {
					LOG.info("Handle Response for order: " + weChatPayNotification.getOutTradeNo());
					getWeChatPayPaymentTransactionStrategy().updateForNotification(orderModel, weChatPayNotification);
					if (WeChatPaymentConstants.Notification.RETURN_SUCCESS.equals(weChatPayNotification.getReturnCode())
							&& WeChatPaymentConstants.Notification.RESULT_SUCCESS.equals(weChatPayNotification.getResultCode()))
					{
						orderModel.setPaymentStatus(PaymentStatus.PAID);
					}
					else
					{
						orderModel.setPaymentStatus(PaymentStatus.NOTPAID);
					}
					getModelService().save(orderModel);
				});
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected WeChatPayPaymentTransactionStrategy getWeChatPayPaymentTransactionStrategy()
	{
		return weChatPayPaymentTransactionStrategy;
	}

	@Required
	public void setWeChatPayPaymentTransactionStrategy(
			final WeChatPayPaymentTransactionStrategy weChatPayPaymentTransactionStrategy)
	{
		this.weChatPayPaymentTransactionStrategy = weChatPayPaymentTransactionStrategy;
	}

	protected WeChatPayOrderDao getWeChatPayOrderDao()
	{
		return weChatPayOrderDao;
	}

	@Required
	public void setWeChatPayOrderDao(final WeChatPayOrderDao weChatPayOrderDao)
	{
		this.weChatPayOrderDao = weChatPayOrderDao;
	}

	protected ChineseOrderService getChineseOrderService()
	{
		return chineseOrderService;
	}

	@Required
	public void setChineseOrderService(final ChineseOrderService chineseOrderService)
	{
		this.chineseOrderService = chineseOrderService;
	}


}
