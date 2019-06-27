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
package de.hybris.platform.customercouponfacades.converter.populators;

import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;
import de.hybris.platform.customercouponservices.CustomerCouponService;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.util.Config;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populates {@link CustomerCouponModel} to {@link CustomerCouponData}
 */
public class CustomerCouponPopulator implements Populator<CustomerCouponModel, CustomerCouponData>
{

	private static final String COUPON_EXPIRE_NOTIFICATIONS_DAYS = "coupon.expire.notification.days";
	private static final String STATUS_PRESESSION = "PreSession";
	private static final String STATUS_EFFECTIVE = "Effective";
	private static final String STATUS_EXPIRESOON = "ExpireSoon";
	private static final String ROOT_CATEGORY = "coupon.rootcategory";
	private static final int ZERO = 0;

	private CommerceCommonI18NService commerceCommonI18NService;
	private CustomerCouponService customerCouponService;

	@Override
	public void populate(final CustomerCouponModel source, final CustomerCouponData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setActive(source.getActive());
		target.setCouponCode(source.getCouponId());
		target.setCouponId(source.getCouponId());
		target.setEndDate(source.getEndDate());
		target.setStartDate(source.getStartDate());
		target.setNotificationOn(getCustomerCouponService().getCouponNotificationStatus(source.getCouponId()));
		final List<PromotionSourceRuleModel> list = getCustomerCouponService()
				.getPromotionSourceRuleForCouponCode(source.getCouponId());
		//if coupon have assigned promotion source rule
		if (list.isEmpty())
		{
			target.setBindingAnyProduct(false);
		}
		else
		{
			target.setBindingAnyProduct(
					getCustomerCouponService().countProductOrCategoryForPromotionSourceRule(list.get(0).getCode()) == ZERO);
		}
		target.setSolrRootCategory(getSolrRootCategory());

		if (target.isBindingAnyProduct())
		{
			target.setProductUrl(MessageFormat.format("/c/{0}?q=%3Arelevance&text=#", target.getSolrRootCategory()));
		}
		else
		{
			target.setProductUrl(MessageFormat.format("/c/{0}?q=%3Arelevance%3AcustomerCouponCode%3A{1}&text=#",
					target.getSolrRootCategory(), source.getCouponId()));
		}

		final Date startDate = source.getStartDate();
		final Date endDate = source.getEndDate();

		if (startDate != null && endDate != null)
		{

			final DateTime endDateTime = new DateTime(endDate);
			final DateTime startDateTime = new DateTime(startDate);
			final DateTime expireSoonDateTime = endDateTime.minusDays(getCouponExpireNotificationDays());

			if (startDateTime.isAfterNow())
			{
				target.setStatus(STATUS_PRESESSION);
			}


			if (startDateTime.isBeforeNow() && expireSoonDateTime.isAfterNow())
			{
				target.setStatus(STATUS_EFFECTIVE);
			}

			if (endDateTime.isAfterNow() && expireSoonDateTime.isBeforeNow())
			{
				target.setStatus(STATUS_EXPIRESOON);
			}
		}
		else
		{
			target.setStatus(STATUS_EFFECTIVE);
		}
	}

	protected String getSolrRootCategory()
	{
		return Config.getString(ROOT_CATEGORY, StringUtils.EMPTY);
	}

	protected int getCouponExpireNotificationDays()
	{
		return Config.getInt(COUPON_EXPIRE_NOTIFICATIONS_DAYS, 0);
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


	public CustomerCouponService getCustomerCouponService()
	{
		return customerCouponService;
	}


	public void setCustomerCouponService(final CustomerCouponService customerCouponService)
	{
		this.customerCouponService = customerCouponService;
	}
}
