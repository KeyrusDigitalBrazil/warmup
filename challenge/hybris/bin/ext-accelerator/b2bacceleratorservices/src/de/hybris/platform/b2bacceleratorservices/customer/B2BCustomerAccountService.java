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
package de.hybris.platform.b2bacceleratorservices.customer;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;

import java.util.List;


/**
 * Interface to handle specific B2B customer account management services.
 */
public interface B2BCustomerAccountService extends CustomerAccountService
{
	/**
	 * Gets the Scheduling job by code
	 *
	 *
	 * @param code
	 *           unique job identifier
	 * @param user
	 *           a customer assigned to the cart
	 * @return the {@link CartToOrderCronJobModel} identified by <param>code</param>
	 */
	CartToOrderCronJobModel getCartToOrderCronJobForCode(String code, UserModel user);

	/**
	 * Gets all order replenishment cron jobs for a given user.
	 *
	 * @param user
	 *           user object
	 * @return replenishment cron jobs created by a user.
	 */
	List<? extends CartToOrderCronJobModel> getCartToOrderCronJobsForUser(UserModel user);

	/**
	 * Gets all order replenishment cron jobs for a given user.
	 *
	 * @param user
	 *           user object
	 * @param pageableData
	 *           pagination info
	 * @return replenishment cron jobs created by a user.
	 */
	SearchPageData<CartToOrderCronJobModel> getPagedCartToOrderCronJobsForUser(UserModel user, PageableData pageableData);

	/**
	 * All orders created by a replenishment cron job
	 *
	 * @param jobCode
	 *           unique cron job id
	 * @param pageableData
	 *           pagination info
	 * @return orders created by a replenishment cron job
	 */
	SearchPageData<OrderModel> getOrdersForJob(String jobCode, PageableData pageableData);
}
