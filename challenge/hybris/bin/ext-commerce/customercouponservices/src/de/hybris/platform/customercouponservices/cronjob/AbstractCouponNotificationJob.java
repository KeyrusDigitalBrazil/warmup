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
package de.hybris.platform.customercouponservices.cronjob;

import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.customercouponservices.constants.CustomercouponservicesConstants;
import de.hybris.platform.customercouponservices.daos.CouponNotificationDao;
import de.hybris.platform.customercouponservices.daos.CustomerCouponDao;
import de.hybris.platform.customercouponservices.enums.CouponNotificationStatus;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.notificationservices.enums.SiteMessageType;
import de.hybris.platform.notificationservices.model.SiteMessageModel;
import de.hybris.platform.notificationservices.service.NotificationService;
import de.hybris.platform.notificationservices.service.SiteMessageService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.localization.Localization;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.task.TaskExecutor;


/**
 * Sends coupon notification to the customer when a customer coupon will take effect or expire soon
 */
public abstract class AbstractCouponNotificationJob extends AbstractJobPerformable<CronJobModel>
{
	private static final String COUPON_EXPIRE_NOTIFICATION_DAYS = "coupon.expire.notification.days";
	private static final String COUPON_EFFECTIVE_NOTIFICAITON_DAYS = "coupon.effective.notification.days";
	private static final Integer ZERO = 0;

	private CustomerCouponDao customerCouponDao;
	private CouponNotificationDao couponNotificationDao;
	private TaskExecutor taskExecutor;
	private NotificationService notificationService;
	private SiteMessageService siteMessageService;
	private CommerceCommonI18NService commerceCommonI18NService;

	/**
	 * Executes cronjob and sends coupon notification when a coupon will take effect or expire soon
	 * 
	 * @param job
	 *           cronjob model
	 * @return the cronjob execution result
	 */
	@Override
	public PerformResult perform(final CronJobModel job)
	{
		final Integer configEffectiveDays = Config.getInt(COUPON_EFFECTIVE_NOTIFICAITON_DAYS, ZERO);
		final DateTime effectiveDay = new DateTime().plusDays(configEffectiveDays);
		final Integer configExpireDays = Config.getInt(COUPON_EXPIRE_NOTIFICATION_DAYS, ZERO);
		final DateTime expireDay = new DateTime().plusDays(configExpireDays);

		final Map<CustomerCouponModel, SiteMessageModel> messages = new ConcurrentHashMap<>();
		getCouponNotificationDao().findAllCouponNotifications().forEach(notification -> {
			final boolean isUnassignedCoupon = getCustomerCouponDao()
					.countAssignedCouponForCustomer(notification.getCustomerCoupon().getCouponId(), notification.getCustomer()) < 1;
			if (new DateTime(notification.getCustomerCoupon().getEndDate()).isBeforeNow() || isUnassignedCoupon)
			{
				modelService.remove(notification);
				return;
			}
			sendNotification(notification, effectiveDay, expireDay, messages);
		});

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	protected void sendNotification(final CouponNotificationModel notification, final DateTime effectiveDay,
			final DateTime expireDay, final Map<CustomerCouponModel, SiteMessageModel> messages)
	{
		final CustomerCouponModel coupon = notification.getCustomerCoupon();
		if (notification.getStatus().equals(CouponNotificationStatus.INIT)
				&& new DateTime(coupon.getStartDate()).isBefore(effectiveDay))
		{
			if (!messages.containsKey(coupon))
			{
				messages.put(coupon, createSiteMessage(notification, NotificationType.COUPON_EFFECTIVE));
			}
			sendCouponNotificaiton(notification, NotificationType.COUPON_EFFECTIVE, messages.get(coupon));
		}
		if ((notification.getStatus().equals(CouponNotificationStatus.INIT)
				|| notification.getStatus().equals(CouponNotificationStatus.EFFECTIVESENT))
				&& new DateTime(coupon.getEndDate()).isBefore(expireDay))
		{
			if (!messages.containsKey(coupon))
			{
				messages.put(coupon, createSiteMessage(notification, NotificationType.COUPON_EXPIRE));
			}
			sendCouponNotificaiton(notification, NotificationType.COUPON_EXPIRE, messages.get(coupon));
		}
	}

	protected SiteMessageModel createSiteMessage(final CouponNotificationModel notification,
			final NotificationType notificationType)
	{
		final CustomerCouponModel coupon = notification.getCustomerCoupon();
		final Locale locale = getCommerceCommonI18NService().getLocaleForLanguage(notification.getLanguage());
		final String title = Localization.getLocalizedString("coupon.notification.sitemessage.title");
		final String content = Localization
				.getLocalizedString("coupon.notification.sitemessage." + notificationType.getCode() + ".content", new Object[]
		{ coupon.getCouponId() });

		return getSiteMessageService().createMessage(title, content, SiteMessageType.SYSTEM, coupon, notificationType, locale);
	}

	protected void sendCouponNotificaiton(final CouponNotificationModel couponNotification,
			final NotificationType notificationType, final SiteMessageModel message)
	{
		final Map<String, ItemModel> data = new HashMap<>();
		data.put(CustomercouponservicesConstants.LANGUAGE, couponNotification.getLanguage());
		data.put(CustomercouponservicesConstants.COUPON_NOTIFICATION, couponNotification);
		data.put(CustomercouponservicesConstants.SITE_MESSAGE, message);
		final ItemModel notificationTypeItem = new ItemModel();
		notificationTypeItem.setProperty(CustomercouponservicesConstants.NOTIFICATION_TYPE, notificationType);
		data.put(CustomercouponservicesConstants.NOTIFICATION_TYPE, notificationTypeItem);

		taskExecutor.execute(createTask(data));

		couponNotification.setStatus(CouponNotificationStatus.EFFECTIVESENT);

		if (NotificationType.COUPON_EXPIRE.equals(notificationType))
		{
			couponNotification.setStatus(CouponNotificationStatus.EXPIRESENT);
		}

		modelService.save(couponNotification);
	}

	protected abstract CouponNotificationTask createTask(final Map<String, ItemModel> data);


	protected CustomerCouponDao getCustomerCouponDao()
	{
		return customerCouponDao;
	}

	@Required
	public void setCustomerCouponDao(final CustomerCouponDao customerCouponDao)
	{
		this.customerCouponDao = customerCouponDao;
	}

	@Required
	public void setTaskExecutor(final TaskExecutor taskExecutor)
	{
		this.taskExecutor = taskExecutor;
	}

	protected TaskExecutor getTaskExecutor()
	{
		return taskExecutor;
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

	protected CouponNotificationDao getCouponNotificationDao()
	{
		return couponNotificationDao;
	}

	@Required
	public void setCouponNotificationDao(final CouponNotificationDao couponNotificationDao)
	{
		this.couponNotificationDao = couponNotificationDao;
	}

	protected SiteMessageService getSiteMessageService()
	{
		return siteMessageService;
	}

	@Required
	public void setSiteMessageService(final SiteMessageService siteMessageService)
	{
		this.siteMessageService = siteMessageService;
	}

	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

}
