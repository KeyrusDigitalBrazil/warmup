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

package de.hybris.platform.chinesepaymentfacades.order.task;

import de.hybris.platform.chinesepaymentservices.checkout.ChineseCheckoutService;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.strategies.impl.EventPublishingSubmitOrderStrategy;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Runs for reducing stock and publishing SubmitOrderEvent when order being paid
 */
public class SubmitOrderEventTask implements TaskRunner<TaskModel>
{
	private static final long DEFAULT_MAX_RETRIES = 3;
	private static final long DEFAULT_RETRY_DELAY = 5 * 60 * 1000L;
	private static final String ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE = "Order with guid %s not found for current user in current BaseStore";

	private long maxRetries = DEFAULT_MAX_RETRIES;
	private long retryDelay = DEFAULT_RETRY_DELAY;

	private ChineseCheckoutService chineseCheckoutService;
	private EventPublishingSubmitOrderStrategy eventPublishingSubmitOrderStrategy;
	private UserService userService;
	private CustomerAccountService customerAccountService;

	private static final Logger LOG = Logger.getLogger(SubmitOrderEventTask.class);

	@Override
	public void run(final TaskService paramTaskService, final TaskModel taskModel) 
	{
		Assert.notNull(taskModel, "There is no task for SubmitOrderEventTask runner bean.");
		final OrderModel orderModel = getOrderForCurrentStoreAndUser(taskModel);
		if (Objects.nonNull(orderModel) && PaymentStatus.PAID.equals(orderModel.getPaymentStatus())
				&& OrderStatus.CREATED.equals(orderModel.getStatus()))
		{
			getChineseCheckoutService().deleteStockLevelReservationHistoryEntry(orderModel.getCode());
			getEventPublishingSubmitOrderStrategy().submitOrder(orderModel);

		}
		else
		{
			if (taskModel.getRetry().intValue() < getMaxRetries())
			{
				final RetryLaterException ex = new RetryLaterException("Can't not publish submit order event for order "
						+ (Objects.isNull(orderModel) ? "is null" : orderModel.getCode()));
				ex.setDelay(getRetryDelay());
				ex.setRollBack(false);
				throw ex;
			}
			else
			{
				throw new IllegalStateException("finally cannot publish submit order event after " + taskModel.getRetry()
						+ " retries");
			}
		}
	}

	@Override
	public void handleError(final TaskService paramTaskService, final TaskModel taskModel, final Throwable error)
	{
		LOG.error("Failed to perform the submitOrderEventTask", error);
	}

	public OrderModel getOrderForCurrentStoreAndUser(final TaskModel taskModel)
	{

		final Map<String, Object> contextData = (Map<String, Object>) taskModel.getContext();
		final BaseStoreModel baseStoreModel = (BaseStoreModel) contextData.get("baseStore");
		final UserModel currentUser = (UserModel) contextData.get("currentUser");
		final String orderCode = ((OrderModel) taskModel.getContextItem()).getCode();
		OrderModel order = null;
		if (Objects.nonNull(baseStoreModel) && Objects.nonNull(currentUser) && StringUtils.isNotEmpty(orderCode))
		{
			if (getUserService().isAnonymousUser(currentUser))
			{
				order = getCustomerAccountService().getOrderForCode(orderCode, baseStoreModel);
			}
			else
			{
				try
				{
					order = getCustomerAccountService().getOrderForCode((CustomerModel) currentUser, orderCode, baseStoreModel);
				}
				catch (final ModelNotFoundException e)
				{
					throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE, orderCode));
				}
			}
		}
		return order;
	}

	protected long getMaxRetries()
	{
		return maxRetries;
	}

	@Required
	public void setMaxRetries(final int maxRetries)
	{
		this.maxRetries = maxRetries;
	}

	protected long getRetryDelay()
	{
		return retryDelay;
	}

	@Required
	public void setRetryDelay(final int retryDelay)
	{
		this.retryDelay = retryDelay;
	}

	protected ChineseCheckoutService getChineseCheckoutService()
	{
		return chineseCheckoutService;
	}

	@Required
	public void setChineseCheckoutService(final ChineseCheckoutService chineseCheckoutService)
	{
		this.chineseCheckoutService = chineseCheckoutService;
	}

	protected EventPublishingSubmitOrderStrategy getEventPublishingSubmitOrderStrategy()
	{
		return eventPublishingSubmitOrderStrategy;
	}

	@Required
	public void setEventPublishingSubmitOrderStrategy(final EventPublishingSubmitOrderStrategy eventPublishingSubmitOrderStrategy)
	{
		this.eventPublishingSubmitOrderStrategy = eventPublishingSubmitOrderStrategy;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

}
