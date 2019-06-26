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
package de.hybris.platform.customercouponservices.strategies.impl;

import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.customercouponservices.strategies.CouponCampaignURLGenerationStrategy;
import de.hybris.platform.util.Config;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;


/**
 * Default implementation of {@link CouponCampaignURLGenerationStrategy}
 */
public class DefaultCouponCampaignURLGenerationStrategy implements CouponCampaignURLGenerationStrategy
{

	private static final String URL_PREFIX_KEY = "coupon.claiming.url.prefix";

	@Override
	public String generate(final CustomerCouponModel coupon)
	{
		if (Objects.isNull(coupon))
		{
			return StringUtils.EMPTY;
		}

		final String urlPrefix = getUrlPrefix();
		final String couponId = coupon.getCouponId();
		if (StringUtils.isEmpty(couponId) || StringUtils.isEmpty(urlPrefix))
		{
			return StringUtils.EMPTY;
		}

		return urlPrefix + couponId;
	}

	protected String getUrlPrefix()
	{
		return Config.getString(URL_PREFIX_KEY, StringUtils.EMPTY);
	}

}
