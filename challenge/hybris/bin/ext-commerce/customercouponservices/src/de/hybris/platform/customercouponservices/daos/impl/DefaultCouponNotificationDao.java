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
package de.hybris.platform.customercouponservices.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customercouponservices.daos.CouponNotificationDao;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@link CouponNotificationDao}
 */
public class DefaultCouponNotificationDao extends DefaultGenericDao<CouponNotificationModel> implements CouponNotificationDao
{

	private static final String FIND_COUPON_NOTIFICATION_FOR_CODE = "SELECT {" + CouponNotificationModel.PK + "} FROM {"
			+ CouponNotificationModel._TYPECODE + " as r},{" + CustomerCouponModel._TYPECODE + " as s} " + " WHERE "
			+ "{r.customerCoupon} = {s.pk} AND {s.couponid} =?couponCode";

	private static final String FIND_COUPON_NOTIFICATION_FOR_CUSTOMER = "SELECT {" + CouponNotificationModel.PK + "} FROM {"
			+ CouponNotificationModel._TYPECODE + " as r} WHERE {r:customer} =?customer";


	public DefaultCouponNotificationDao()
	{
		super(CouponNotificationModel._TYPECODE);
	}

	@Override
	public List<CouponNotificationModel> findCouponNotificationByCouponCode(final String couponCode)
	{
		validateParameterNotNull(couponCode, "Coupon code must not be null");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_COUPON_NOTIFICATION_FOR_CODE);
		query.addQueryParameter("couponCode", couponCode);
		return getFlexibleSearchService().<CouponNotificationModel> search(query).getResult();
	}

	@Override
	public List<CouponNotificationModel> findCouponNotificationsForCustomer(final CustomerModel customer)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_COUPON_NOTIFICATION_FOR_CUSTOMER);
		query.addQueryParameter("customer", customer);
		final List<CouponNotificationModel> results = getFlexibleSearchService().<CouponNotificationModel> search(query).getResult();
		if (CollectionUtils.isNotEmpty(results))
		{
			return results;
		}
		return Collections.emptyList();
	}

}
