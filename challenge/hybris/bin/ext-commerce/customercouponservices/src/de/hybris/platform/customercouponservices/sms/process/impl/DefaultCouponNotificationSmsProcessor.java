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
package de.hybris.platform.customercouponservices.sms.process.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customercouponservices.constants.CustomercouponservicesConstants;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.notificationservices.processor.Processor;
import de.hybris.platform.notificationservices.service.NotificationService;
import de.hybris.platform.notificationservices.strategies.SendSmsMessageStrategy;
import de.hybris.platform.util.localization.Localization;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Deals with coupon notification for sending SMS to the customer
 */
public class DefaultCouponNotificationSmsProcessor implements Processor
{
	private static final Logger LOG = Logger.getLogger(DefaultCouponNotificationSmsProcessor.class.getName());

	private SendSmsMessageStrategy sendSmsMessageStrategy;
	private NotificationService notificationService;


	@Override
	public void process(final CustomerModel customer, final Map<String, ? extends ItemModel> dataMap)
	{
		final CouponNotificationModel couponNotification = (CouponNotificationModel) dataMap
				.get(CustomercouponservicesConstants.COUPON_NOTIFICATION);
		final NotificationType notificationType = dataMap.get(CustomercouponservicesConstants.NOTIFICATION_TYPE).getProperty(
				CustomercouponservicesConstants.NOTIFICATION_TYPE);

		final String message = Localization.getLocalizedString(
				"coupon.notification.sms." + notificationType.getCode() + ".content",
				new Object[]
				{ couponNotification.getCustomerCoupon().getCouponId() });
		final String phoneNumber = getNotificationService().getChannelValue(NotificationChannel.SMS, customer);

		if (StringUtils.isEmpty(phoneNumber))
		{
			LOG.warn("No phone number found for customer, message[" + message + "] will not be sent.");
			return;
		}

		getSendSmsMessageStrategy().sendMessage(phoneNumber, message);
	}


	protected NotificationService getNotificationService()
	{
		return notificationService;
	}

	@Required
	public void setNotificationService(final NotificationService notificationService)
	{
		this.notificationService = notificationService;
	}

	protected SendSmsMessageStrategy getSendSmsMessageStrategy()
	{
		return sendSmsMessageStrategy;
	}

	@Required
	public void setSendSmsMessageStrategy(final SendSmsMessageStrategy sendSmsMessageStrategy)
	{
		this.sendSmsMessageStrategy = sendSmsMessageStrategy;
	}



}
