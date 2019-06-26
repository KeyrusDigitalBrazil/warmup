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
package de.hybris.platform.b2b.dao;

import de.hybris.platform.b2b.dao.impl.DefaultCartToOrderCronJobModelDao;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;

import java.util.Date;
import java.util.List;



/**
 * A data access to {@link OrderModel}
 *
 *
 * @spring.bean b2bOrderDao
 */

public interface B2BOrderDao
{

	/**
	 * Find the rejected orders with the status of {@link OrderStatus#REJECTED} for a <code>user</code>.
	 *
	 * @param user
	 *           the user
	 * @return the list of rejected orders
	 */
	List<OrderModel> findRejectedOrders(final UserModel user);

	/**
	 * Find the orders with a status of {@link OrderStatus#REJECTED_BY_MERCHANT} for a user.
	 *
	 * @param user
	 *           the user
	 * @return the list of rejected orders
	 */
	List<OrderModel> findRejectedByMerchantOrders(final UserModel user);

	/**
	 * @deprecated Since 6.3. Please see quote functionality from commerce.
	 *
	 * Find the orders with a status of {@link OrderStatus#PENDING_QUOTE} for a user.
	 *
	 * @param user
	 *           the user
	 * @return List of orders
	 */
	@Deprecated
	List<OrderModel> findPendingQuoteOrders(final UserModel user);

	/**
	 * @deprecated Since 6.3. Please see quote functionality from commerce.
	 *
	 * Find the orders with a status of {@link OrderStatus#APPROVED_QUOTE} for a user.
	 *
	 * @param user
	 *           the user
	 * @return List of orders
	 */
	@Deprecated
	List<OrderModel> findApprovedQuoteOrders(final UserModel user);

	/**
	 * @deprecated Since 6.3. Please see quote functionality from commerce.
	 *
	 * Find the orders with a status of {@link OrderStatus#REJECTED_QUOTE} for a user.
	 *
	 * @param user
	 *           the user
	 * @return List of orders
	 */
	@Deprecated
	List<OrderModel> findRejectedQuoteOrders(final UserModel user);

	/**
	 * Find the orders with a status of {@link OrderStatus#APPROVED} for a user.
	 *
	 * @param user
	 *           the user
	 * @return the list of orders
	 */
	List<OrderModel> findApprovedOrders(final UserModel user);

	/**
	 * Find the orders with a status of {@link OrderStatus#PENDING_APPROVAL} for a user.
	 *
	 * @param user
	 *           the user
	 * @return the list of orders
	 */
	List<OrderModel> findPendingApprovalOrders(final UserModel user);

	/**
	 * Find the orders with a status of {@link OrderStatus#PENDING_APPROVAL_FROM_MERCHANT} for a user.
	 *
	 * @param user
	 *           the user
	 * @return the list of orders
	 */
	List<OrderModel> findPendingApprovalOrdersFromMerchant(final UserModel user);

	/**
	 * Find orders having a particular {@link OrderStatus} value for a user.
	 *
	 * @param user
	 *           the user
	 * @param status
	 *           the order's current status
	 * @return the list of orders
	 */
	List<OrderModel> findOrdersByStatus(final UserModel user, final OrderStatus status);

	/**
	 * Find orders approved for date range.
	 *
	 * @param user
	 *           the user
	 * @param startDate
	 *           the start date
	 * @param endDate
	 *           the end date
	 * @return the list
	 * @deprecated Since 4.4. Use {@link #findOrdersApprovedByDateRange(UserModel,Date,Date)} instead
	 */
	@Deprecated
	List<OrderModel> findOrdersApprovedForDateRange(final UserModel user, final Date startDate, final Date endDate);

	/**
	 * Find orders with a {@link OrderStatus#APPROVED} status within a date range for a user.
	 *
	 * @param user
	 *           the user
	 * @param startDate
	 *           the start date
	 * @param endDate
	 *           the end date
	 * @return the list of orders
	 */
	List<OrderModel> findOrdersApprovedByDateRange(final UserModel user, final Date startDate, final Date endDate);

	/**
	 * Finds CartToOrderCronJob for a given user.
	 *
	 * @param user
	 *           A user who had a cart scheduled for replenishment.
	 * @return A list of cronjobs responsible for replenisment of users cart.
	 * @deprecated Since 6.0. Use {@link DefaultCartToOrderCronJobModelDao#findCartToOrderCronJobs(UserModel)} instead
	 */
	@Deprecated
	List<CartToOrderCronJobModel> findCartToOrderCronJobs(final UserModel user);

	/**
	 * Find cart to order model with a particular <code>code</code>.
	 *
	 * @param code
	 *           the code of the cron job
	 * @return the cart to order cron job model
	 * @deprecated Since 6.0. Use {@link DefaultCartToOrderCronJobModelDao#findCartToOrderCronJob(String)} instead
	 */
	@Deprecated
	CartToOrderCronJobModel findCartToOrderModel(final String code);

	/**
	 * Find order by given status for a user.
	 *
	 * @param user
	 *           the user
	 * @param status
	 *           the status
	 * @return orders
	 */
	List<OrderModel> findOrdersByStatus(final UserModel user, final List<OrderStatus> status);

	/**
	 * Find orders with a {@link OrderStatus#APPROVED} status for b2b unit based on date range and currency.
	 *
	 * @param unitModel
	 *           the b2b unit
	 * @param startDate
	 *           the start date of the date range
	 * @param endDate
	 *           the end date of the date range
	 * @param currency
	 *           the currency of the order
	 * @return List of orders
	 */
	List<OrderModel> findOrdersApprovedForDateRangeByCurrency(final B2BUnitModel unitModel, final Date startDate,
			final Date endDate, final CurrencyModel currency);


	/**
	 * Find order total for the unit based on date range and converted into given currency
	 *
	 * @param unitModel
	 * @param startDate
	 * @param endDate
	 * @param currency
	 * @return List
	 * @deprecated Since 4.4. Use {@link #findOrderTotalsByDateRangeAndCurrency(B2BUnitModel,Date,Date,CurrencyModel)} instead
	 */
	@Deprecated
	Double findOrderTotalsForDateRangeByCurrency(final B2BUnitModel unitModel, final Date startDate, final Date endDate,
			final CurrencyModel currency);

	/**
	 * Find order total for the b2b unit based on date range and converted into given currency
	 *
	 * @param unitModel
	 *           the b2b unit
	 * @param startDate
	 *           the start date of the date range
	 * @param endDate
	 *           the end date of the date range
	 * @param currency
	 *           the currency type to be converted to
	 * @return List of orders
	 */
	Double findOrderTotalsByDateRangeAndCurrency(final B2BUnitModel unitModel, final Date startDate, final Date endDate,
			final CurrencyModel currency);


	/**
	 * Gets order for a give code.
	 *
	 * @param code
	 *           A unque quote
	 * @return An instace of AbstractOrderModel
	 */
	<T extends OrderModel> T findOrderByCode(String code);
}
