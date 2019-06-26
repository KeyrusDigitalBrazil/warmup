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
package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.dao.B2BOrderDao;
import de.hybris.platform.b2b.dao.CartToOrderCronJobModelDao;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.QuoteEvaluationStrategy;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.impl.DefaultOrderService;
import de.hybris.platform.order.strategies.CreateOrderFromCartStrategy;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link B2BOrderService}
 *
 * @spring.bean b2bOrderService
 */
public class DefaultB2BOrderService extends DefaultOrderService implements B2BOrderService
{
	private static final long serialVersionUID = 4786522934349630161L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultB2BOrderService.class);
	private transient CreateOrderFromCartStrategy b2bCreateOrderFromCartStrategy;
	private transient List<QuoteEvaluationStrategy> b2bQuoteEvaluationStrategies;
	private transient BaseDao baseDao;
	private transient B2BOrderDao b2bOrderDao;
	private transient CartToOrderCronJobModelDao cartToOrderCronJobModelDao;
	private transient B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private transient B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
	private transient I18NService i18nService;
	private transient UserService userService;
	private transient SearchRestrictionService searchRestrictionService;
	private transient WorkflowProcessingService workflowProcessingService;

	@Override
	public OrderModel createOrderFromCart(final CartModel cart) throws InvalidCartException
	{
		if (isB2BContext(cart))
		{
			//FIXME: this should not be needed the unit on the cart should have already been set.
			//now we know the unit and the user and can set them
			if (cart.getUnit() == null)
			{
				final B2BCustomerModel currentB2BCustomer = getB2bCustomerService().getCurrentB2BCustomer();
				cart.setUnit(getB2bUnitService().getParent(currentB2BCustomer));
			}
			cart.setLocale(getI18nService().getCurrentLocale().toString());
			//FIXME: make sure that this service does not depend on a strategy from b2bapprovalprocess extension
			return this.getB2bCreateOrderFromCartStrategy().createOrderFromCart(cart);
		}
		else
		{
			return super.createOrderFromCart(cart);
		}
	}


	protected boolean isB2BContext(final AbstractOrderModel order)
	{
		return order != null && getB2bCustomerService().getCurrentB2BCustomer() != null && order.getStatus() != null;

	}


	@Override
	public List<OrderModel> getRejectedOrders(final UserModel user)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public List<OrderModel> execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return getB2bOrderDao().findRejectedOrders(user);
			}
		});
	}

	@Override
	public List<CartToOrderCronJobModel> getScheduledOrders(final UserModel user)
	{
		return getCartToOrderCronJobModelDao().findCartToOrderCronJobs(user);
	}

	/**
	 * @deprecated Since 4.4. Use {@link #getScheduledCartToOrderJobForCode(String)} instead
	 */
	@Deprecated
	@Override
	public CartToOrderCronJobModel findScheduledCartToOrderJob(final String code)
	{
		return getScheduledCartToOrderJobForCode(code);
	}

	@Override
	public CartToOrderCronJobModel getScheduledCartToOrderJobForCode(final String code)
	{
		return getCartToOrderCronJobModelDao().findCartToOrderCronJob(code);

	}

	@Override
	public List<OrderModel> getPendingApprovalOrders(final UserModel user)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public List<OrderModel> execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return getB2bOrderDao().findOrdersByStatus(user, Collections.singletonList(OrderStatus.PENDING_APPROVAL));
			}
		});

	}

	@Override
	public List<OrderModel> getApprovedOrders(final UserModel user)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public List<OrderModel> execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return getB2bOrderDao().findApprovedOrders(user);
			}
		});

	}

	/**
	 * @deprecated Since 4.4. Use {@link #getOrderForCode(String)} instead
	 */
	@Deprecated
	@Override
	public OrderModel getOrderByCode(final String code)
	{
		return getOrderForCode(code);
	}

	@Override
	public AbstractOrderModel getAbstractOrderForCode(final String code)
	{
		return getBaseDao().findFirstByAttribute(OrderModel.CODE, code, AbstractOrderModel.class);
	}

	@Override
	public OrderModel getOrderForCode(final String code)
	{
		return getB2bOrderDao().findOrderByCode(code);
	}

	@Override
	public List<OrderModel> getErroredOrders(final UserModel user)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public List<OrderModel> execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				final List<OrderStatus> status = new ArrayList<OrderStatus>();
				status.add(OrderStatus.B2B_PROCESSING_ERROR);
				status.add(OrderStatus.ASSIGNED_TO_ADMIN);
				return getB2bOrderDao().findOrdersByStatus(user, status);
			}
		});
	}

	/**
	 * @deprecated Since 4.4. Use {@link #getRejectedForMerchantOrders(UserModel)} instead
	 */
	@Deprecated
	@Override
	public List<OrderModel> getRejectedByMerchantOrders(final UserModel user)
	{
		return getRejectedForMerchantOrders(user);
	}

	@Override
	public List<OrderModel> getRejectedForMerchantOrders(final UserModel user)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public List<OrderModel> execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return getB2bOrderDao().findRejectedByMerchantOrders(user);
			}
		});
	}

	@Override
	public List<OrderModel> getPendingApprovalOrdersFromMerchant(final UserModel user)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public List<OrderModel> execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return getB2bOrderDao().findPendingApprovalOrdersFromMerchant(user);
			}
		});
	}

	/**
	 * @deprecated Since 6.3.
	 */
	@Deprecated
	@Override
	public List<OrderModel> getRejectedQuoteOrders(final UserModel user)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public List<OrderModel> execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return getB2bOrderDao().findRejectedQuoteOrders(user);
			}
		});
	}

	/**
	 * @deprecated Since 6.3.
	 */
	@Deprecated
	@Override
	public List<OrderModel> getApprovedQuoteOrders(final UserModel user)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public List<OrderModel> execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return getB2bOrderDao().findApprovedQuoteOrders(user);
			}
		});

	}

	/**
	 * @deprecated Since 6.3.
	 */
	@Deprecated
	@Override
	public List<OrderModel> getPendingQuoteOrders(final UserModel user)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public List<OrderModel> execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return getB2bOrderDao().findPendingQuoteOrders(user);
			}
		});
	}

	/**
	 * @deprecated Since 6.3.
	 */
	@Deprecated
	@Override
	public boolean isQuoteAllowed(final AbstractOrderModel source)
	{
		boolean isQuoteAllowed = true;

		for (final QuoteEvaluationStrategy strategy : b2bQuoteEvaluationStrategies)
		{
			if (!strategy.isQuoteAllowed(source))
			{
				isQuoteAllowed = false;
				break;
			}
		}
		return isQuoteAllowed;
	}

	/**
	 * Modified to and and remove the workflow from the order when the order is deleted.
	 */
	@Override
	public void deleteOrder(final String code)
	{
		final OrderModel order = getOrderForCode(code);
		final WorkflowModel workFlowModel = order.getWorkflow();
		if (workFlowModel != null)
		{
			getWorkflowProcessingService().endWorkflow(workFlowModel);
			getModelService().remove(workFlowModel);
		}
		getModelService().remove(order);
	}

	@Override
	public double getTotalDiscount(final AbstractOrderEntryModel entry)
	{
		return DiscountValue.sumAppliedValues(entry.getDiscountValues());
	}

	@Override
	public boolean hasItemDiscounts(final AbstractOrderModel order)
	{
		boolean hasDiscountedItems = false;
		for (final AbstractOrderEntryModel entry : order.getEntries())
		{
			// check if at least one entity in the order is discounted.
			if (getOrderEntryDiscountAmount(entry) > 0d)
			{
				hasDiscountedItems = true;
				break;
			}
		}
		return hasDiscountedItems;
	}

	@Override
	public double getOrderEntryDiscountAmount(final AbstractOrderEntryModel entry)
	{
		double entryDiscountTotal = 0D;
		final List<?> discountValues = entry.getDiscountValues();
		if (CollectionUtils.isNotEmpty(discountValues))
		{
			final CurrencyModel currency = entry.getOrder().getCurrency();
			final List<?> values = DiscountValue.apply(1, entry.getBasePrice().doubleValue(), currency.getDigits().intValue(),
					discountValues, currency.getIsocode());
			entryDiscountTotal = DiscountValue.sumAppliedValues(values);
		}
		return entryDiscountTotal;
	}

	@Required
	public void setB2bOrderDao(final B2BOrderDao b2bOrderDao)
	{
		this.b2bOrderDao = b2bOrderDao;
	}

	@Required
	public void setBaseDao(final BaseDao baseDao)
	{
		this.baseDao = baseDao;
	}

	protected I18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	@Required
	public void setB2bCustomerService(final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
	}

	protected CreateOrderFromCartStrategy getB2bCreateOrderFromCartStrategy()
	{
		return b2bCreateOrderFromCartStrategy;
	}

	@Required
	public void setB2bCreateOrderFromCartStrategy(final CreateOrderFromCartStrategy b2bCreateOrderFromCartStrategy)
	{
		this.b2bCreateOrderFromCartStrategy = b2bCreateOrderFromCartStrategy;
	}

	protected CartToOrderCronJobModelDao getCartToOrderCronJobModelDao()
	{
		return cartToOrderCronJobModelDao;
	}

	@Required
	public void setCartToOrderCronJobModelDao(final CartToOrderCronJobModelDao cartToOrderCronJobModelDao)
	{
		this.cartToOrderCronJobModelDao = cartToOrderCronJobModelDao;
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
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

	protected SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	@Required
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}


	protected BaseDao getBaseDao()
	{
		return baseDao;
	}

	protected B2BOrderDao getB2bOrderDao()
	{
		return b2bOrderDao;
	}

	protected B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2bCustomerService()
	{
		return b2bCustomerService;
	}

	protected List<QuoteEvaluationStrategy> getB2bQuoteEvaluationStrategies()
	{
		return b2bQuoteEvaluationStrategies;
	}

	@Autowired
	public void setB2bQuoteEvaluationStrategies(final List<QuoteEvaluationStrategy> b2bQuoteEvaluationStrategies)
	{
		this.b2bQuoteEvaluationStrategies = b2bQuoteEvaluationStrategies;
	}

	public WorkflowProcessingService getWorkflowProcessingService()
	{
		return workflowProcessingService;
	}

	@Required
	public void setWorkflowProcessingService(final WorkflowProcessingService workflowProcessingService)
	{
		this.workflowProcessingService = workflowProcessingService;
	}

}
