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
package de.hybris.platform.customercouponservices.strategies;

import de.hybris.platform.acceleratorservices.process.strategies.impl.AbstractProcessContextStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customercouponservices.model.CouponNotificationProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Optional;


/**
 * Handles coupon notification information when generating Email context
 */
public class CouponNotificationProcessContextStrategy extends AbstractProcessContextStrategy
{

	@Override
	public BaseSiteModel getCmsSite(final BusinessProcessModel businessProcessModel)
	{
		ServicesUtil.validateParameterNotNull(businessProcessModel, BUSINESS_PROCESS_MUST_NOT_BE_NULL_MSG);

		return Optional.of(businessProcessModel)
				.filter(businessProcess -> businessProcess instanceof CouponNotificationProcessModel)
				.map(businessProcess -> ((CouponNotificationProcessModel) businessProcess).getCouponNotification().getBaseSite())
				.orElse(null);
	}

	@Override
	protected CustomerModel getCustomer(final BusinessProcessModel businessProcess)
	{
		return Optional.of(businessProcess).filter(bp -> bp instanceof CouponNotificationProcessModel)
				.map(bp -> ((CouponNotificationProcessModel) businessProcess).getCouponNotification().getCustomer()).orElse(null);
	}

	@Override
	protected LanguageModel computeLanguage(final BusinessProcessModel businessProcess)
	{
		return Optional.of(businessProcess).filter(bp -> bp instanceof CouponNotificationProcessModel)
				.map(bp -> ((CouponNotificationProcessModel) businessProcess).getCouponNotification().getLanguage())
				.orElse(super.computeLanguage(businessProcess));
	}


}
