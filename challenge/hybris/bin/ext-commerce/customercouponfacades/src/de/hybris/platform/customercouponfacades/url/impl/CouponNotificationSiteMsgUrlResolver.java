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
package de.hybris.platform.customercouponfacades.url.impl;

import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.notificationfacades.url.SiteMessageUrlResolver;


/**
 * Resolves the URL related to coupon notification in site message
 */
public class CouponNotificationSiteMsgUrlResolver extends SiteMessageUrlResolver<CustomerCouponModel>
{

	@Override
	public String resolve(final CustomerCouponModel source)
	{
		return getDefaultUrl();
	}

}
