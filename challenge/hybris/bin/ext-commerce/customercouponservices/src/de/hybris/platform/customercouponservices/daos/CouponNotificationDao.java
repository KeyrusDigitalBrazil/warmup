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
package de.hybris.platform.customercouponservices.daos;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.List;


/**
 * Looks up items related to {@link CouponNotificationModel}
 */
public interface CouponNotificationDao extends GenericDao<CouponNotificationModel>
{
	/**
	 * Finds all customer coupons applicable for sending out notifications
	 * 
	 * @return the list of CouponNotificationModel
	 * 
	 */
	default List<CouponNotificationModel> findAllCouponNotifications()
	{
		return find();
	}

	/**
	 * Finds coupon notifications by coupon code
	 * 
	 * @param couponCode
	 *           the coupon code
	 * @return the list of CouponNotificationModel
	 * 
	 */
	List<CouponNotificationModel> findCouponNotificationByCouponCode(String couponCode);

	/**
	 * Finds coupon notifications for the customer
	 * 
	 * @param customer
	 *           the customer model
	 * @return the list of CouponNotificationModel
	 */
	List<CouponNotificationModel> findCouponNotificationsForCustomer(CustomerModel customer);


}
