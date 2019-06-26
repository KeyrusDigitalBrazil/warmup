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
package de.hybris.platform.customercouponfacades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;
import de.hybris.platform.customercouponservices.model.CouponNotificationProcessModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;

import com.sap.security.core.server.csi.XSSEncoder;


/**
 * Deals with the context for sending coupon notification Emails
 */
public class CouponNotificationEmailContext extends AbstractEmailContext<CouponNotificationProcessModel>
{
	private Locale emailLocale;
	private static final String COUPON_TITLE = "couponTitle";
	private static final String COUPON_SUMMARY = "couponSummary";
	private static final String COUPON_NOTIFICATION_TYPE = "couponNotificationType";
	private static final String COUPON_LINK = "couponLink";
	private static final String VALIDITY_DATE = "validityDate";
	private static final String START_DATE = "startDate";
	private static final String END_DATE = "endDate";
	private static final String START_DATE_YEAR = "startDateYear";
	private static final String END_DATE_YEAR = "endDateYear";

	private static final String COUPON_EFFECTIVE = "COUPON_EFFECTIVE";
	private static final String COUPON_EFFECTIVE_MESSAGE = "effective";
	private static final String COUPON_EXPIRE_MESSAGE = "expire";
	private static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";


	private Converter<CustomerCouponModel, CustomerCouponData> customerCouponConverter;

	private static final Logger LOG = Logger.getLogger(CouponNotificationEmailContext.class);

	@Override
	public void init(final CouponNotificationProcessModel businessProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(businessProcessModel, emailPageModel);
		setEmailLocale(businessProcessModel);
		updateBaseUrl(businessProcessModel, emailLocale);
		updateTitle(businessProcessModel, emailLocale);
		updateCouponTitle(businessProcessModel, emailLocale);
		updateCouponSummary(businessProcessModel, emailLocale);
		updateCouponNotificationType(businessProcessModel);
		updateCouponValidDate(businessProcessModel);
		updateCouponLink(businessProcessModel);
	}

	protected void updateCouponTitle(final CouponNotificationProcessModel businessProcessModel, final Locale emailLocale)
	{
		put(COUPON_TITLE, businessProcessModel.getCouponNotification().getCustomerCoupon().getName(emailLocale));
	}

	protected void updateCouponSummary(final CouponNotificationProcessModel businessProcessModel, final Locale emailLocale)
	{
		put(COUPON_SUMMARY, businessProcessModel.getCouponNotification().getCustomerCoupon().getDescription(emailLocale));
	}

	protected void updateCouponNotificationType(final CouponNotificationProcessModel businessProcessModel)
	{
		if (businessProcessModel.getNotificationType().getCode().equals(COUPON_EFFECTIVE))
		{
			put(COUPON_NOTIFICATION_TYPE, COUPON_EFFECTIVE_MESSAGE);
		}
		else
		{
			put(COUPON_NOTIFICATION_TYPE, COUPON_EXPIRE_MESSAGE);
		}
	}

	protected void updateCouponLink(final CouponNotificationProcessModel businessProcessModel)
	{
		final CustomerCouponData couponData = getCustomerCouponConverter()
				.convert(businessProcessModel.getCouponNotification().getCustomerCoupon());
		if (couponData.isBindingAnyProduct())
		{
			put(COUPON_LINK, "c/" + couponData.getSolrRootCategory() + "?q=%3Arelevance&text=");
		}
		else
		{
			put(COUPON_LINK, "c/" + couponData.getSolrRootCategory() + "?q=%3Arelevance%3AcustomerCouponCode%3A"
					+ encodeUrl(couponData.getCouponCode()) + "&text=");
		}
	}

	protected void updateCouponValidDate(final CouponNotificationProcessModel businessProcessModel)
	{
		final CustomerCouponData couponData = getCustomerCouponConverter()
				.convert(businessProcessModel.getCouponNotification().getCustomerCoupon());
		final DateTime startDateTime = new DateTime(couponData.getStartDate());
		final DateTime endDateTime = new DateTime(couponData.getEndDate());

		final String validPeriod = startDateTime.toString(TIME_PATTERN) + " ~ " + endDateTime.toString(TIME_PATTERN);

		put(VALIDITY_DATE, validPeriod);
		put(START_DATE, startDateTime.toString(TIME_PATTERN));
		put(END_DATE, endDateTime.toString(TIME_PATTERN));
		put(START_DATE_YEAR, String.valueOf(startDateTime.getYear()));
		put(END_DATE_YEAR, String.valueOf(endDateTime.getYear()));
	}

	protected void setEmailLocale(final CouponNotificationProcessModel businessProcessModel)
	{
		final String isoCode = getEmailLanguage(businessProcessModel).getIsocode();
		emailLocale = new Locale(isoCode);
	}

	protected void updateTitle(final CouponNotificationProcessModel businessProcessModel, final Locale emailLocale)
	{
		final String title = businessProcessModel.getCouponNotification().getCustomer().getTitle().getName(emailLocale);
		put(TITLE, title);
	}

	protected void updateBaseUrl(final CouponNotificationProcessModel businessProcessModel, final Locale emailLocale)
	{
		final String baseUrl = (String) get(BASE_URL);
		final String baseSecrueUrl = (String) get(SECURE_BASE_URL);
		final String defaultIsoCode = businessProcessModel.getCouponNotification().getBaseSite().getDefaultLanguage().getIsocode();
		final String siteIsoCode = emailLocale.getLanguage();
		put(BASE_URL, baseUrl.replaceAll("/" + defaultIsoCode + "$", "/" + siteIsoCode));
		put(SECURE_BASE_URL, baseSecrueUrl.replaceAll("/" + defaultIsoCode + "$", "/" + siteIsoCode));
	}

	protected static String encodeUrl(final String url)
	{
		try
		{
			return XSSEncoder.encodeURL(url);
		}
		catch (final UnsupportedEncodingException e)
		{
			//log the error and return not encoded url
			LOG.error(e);
			return url;
		}
	}

	@Override
	protected BaseSiteModel getSite(final CouponNotificationProcessModel couponNotificationProcessModel)
	{
		return couponNotificationProcessModel.getCouponNotification().getBaseSite();
	}

	@Override
	protected CustomerModel getCustomer(final CouponNotificationProcessModel couponNotificationProcessModel)
	{
		return couponNotificationProcessModel.getCouponNotification().getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final CouponNotificationProcessModel couponNotificationProcessModel)
	{
		return couponNotificationProcessModel.getLanguage();
	}


	protected Converter<CustomerCouponModel, CustomerCouponData> getCustomerCouponConverter()
	{
		return customerCouponConverter;
	}

	@Required
	public void setCustomerCouponConverter(final Converter<CustomerCouponModel, CustomerCouponData> customerCouponConverter)
	{
		this.customerCouponConverter = customerCouponConverter;
	}

}
