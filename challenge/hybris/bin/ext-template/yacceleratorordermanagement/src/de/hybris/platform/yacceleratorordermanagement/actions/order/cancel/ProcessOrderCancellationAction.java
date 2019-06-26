/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.yacceleratorordermanagement.actions.order.cancel;

import de.hybris.platform.basecommerce.enums.OrderCancelEntryStatus;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordercancel.OrderCancelCallbackService;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordercancel.OrderCancelResponse.ResponseStatus;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.util.Collections.singletonList;


/**
 * Creates the required events from the inventory when a cancellation is requested and process the cancellation
 */
public class ProcessOrderCancellationAction extends AbstractAction<OrderProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(ProcessOrderCancellationAction.class);

	protected enum Transition
	{
		OK, WAIT, SOURCING;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<>();

			for (final Transition transition : Transition.values())
			{
				res.add(transition.toString());
			}
			return res;
		}
	}

	private OrderCancelCallbackService orderCancelCallbackService;
	private OrderCancelService orderCancelService;
	private CalculationService calculationService;
	private ImpersonationService impersonationService;
	private PromotionsService promotionsService;
	private TimeService timeService;

	@Override
	public String execute(OrderProcessModel process) throws Exception
	{
		validateParameterNotNullStandardMessage("process", process);
		LOG.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());

		final OrderModel order = process.getOrder();
		validateParameterNotNullStandardMessage("order", order);

		final OrderCancelRecordEntryModel orderCancelRecordEntryModel = getOrderCancelService().getPendingCancelRecordEntry(order);
		final OrderCancelResponse orderCancelResponse = createOrderCancelResponseFromCancelRecordEntry(order,
				orderCancelRecordEntryModel);
		getOrderCancelCallbackService().onOrderCancelResponse(orderCancelResponse);

		final ImpersonationContext impersonationContext = new ImpersonationContext();
		impersonationContext.setSite(process.getOrder().getSite());

		getImpersonationService().executeInContext(impersonationContext, () -> {
			try
			{
				getTimeService().setCurrentTime(order.getDate());
				getCalculationService().calculate(order, order.getDate());
				getPromotionsService().updatePromotions(singletonList(order.getSite().getDefaultPromotionGroup()), order);
			}
			catch (final CalculationException e) //NOSONAR
			{
				LOG.error("An error occurred during order {} recalculation: {}", order.getCode(), e.getMessage());
			}
			return null;
		});

		String transition;

		//Restricting Re-sourcing when an ON_HOLD order gets cancelled
		if (!OrderStatus.ON_HOLD.equals(order.getStatus()) && process.getOrder().getEntries().stream()
				.anyMatch(entry -> ((OrderEntryModel) entry).getQuantityUnallocated().longValue() > 0))
		{
			transition = Transition.SOURCING.toString();
		}
		else if (process.getOrder().getEntries().stream()
				.anyMatch(entry -> ((OrderEntryModel) entry).getQuantityPending().longValue() > 0))
		{
			transition = Transition.WAIT.toString();
		}
		else
		{
			transition = Transition.OK.toString();
		}

		if (process.getOrder().getEntries().stream()
				.allMatch(entry -> (entry.getQuantity() != null && entry.getQuantity().longValue() == 0)))
		{
			order.setStatus(OrderStatus.CANCELLED);
		}
		else if (!OrderStatus.ON_HOLD.equals(order.getStatus()) && process.getOrder().getEntries().stream()
				.anyMatch(orderEntry -> ((OrderEntryModel) orderEntry).getQuantityUnallocated().longValue() > 0))
		{
			order.setStatus(OrderStatus.SUSPENDED);
		}
		else if (!OrderStatus.ON_HOLD.equals(order.getStatus()))
		{
			order.setStatus(OrderStatus.READY);
		}
		getModelService().save(order);
		return transition;
	}

	/**
	 * Creates a {@link OrderCancelResponse} from the {@link OrderCancelRecordEntryModel}
	 *
	 * @param order
	 * @param orderCancelRecordEntryModel
	 * @return the created orderCancelResponse
	 */
	protected OrderCancelResponse createOrderCancelResponseFromCancelRecordEntry(final OrderModel order,
			final OrderCancelRecordEntryModel orderCancelRecordEntryModel)
	{
		final List<OrderCancelEntry> orderCancelEntries = new ArrayList<>();
		orderCancelRecordEntryModel.getOrderEntriesModificationEntries().forEach(modEntry ->
		{
			final OrderEntryCancelRecordEntryModel oecrem = (OrderEntryCancelRecordEntryModel) modEntry;
			final OrderCancelEntry orderCancelEntry = new OrderCancelEntry(oecrem.getOrderEntry(),
					oecrem.getCancelRequestQuantity().longValue(), oecrem.getNotes(), oecrem.getCancelReason());
			orderCancelEntries.add(orderCancelEntry);
		});

		return new OrderCancelResponse(order, orderCancelEntries, extractResponseStatus(orderCancelRecordEntryModel),
				orderCancelRecordEntryModel.getNotes());
	}

	/**
	 * Extract {@link ResponseStatus} from {@link OrderCancelRecordEntryModel}
	 *
	 * @param orderCancelRecordEntryModel
	 * @return the responseStatus
	 */
	protected ResponseStatus extractResponseStatus(final OrderCancelRecordEntryModel orderCancelRecordEntryModel)
	{
		return orderCancelRecordEntryModel.getCancelResult().equals(OrderCancelEntryStatus.PARTIAL) ?
				ResponseStatus.partial :
				ResponseStatus.full;
	}


	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	public OrderCancelCallbackService getOrderCancelCallbackService()
	{
		return orderCancelCallbackService;
	}

	@Required
	public void setOrderCancelCallbackService(OrderCancelCallbackService orderCancelCallbackService)
	{
		this.orderCancelCallbackService = orderCancelCallbackService;
	}

	protected OrderCancelService getOrderCancelService()
	{
		return orderCancelService;
	}

	@Required
	public void setOrderCancelService(final OrderCancelService orderCancelService)
	{
		this.orderCancelService = orderCancelService;
	}

	protected CalculationService getCalculationService()
	{
		return calculationService;
	}

	@Required
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}

	protected ImpersonationService getImpersonationService()
	{
		return impersonationService;
	}

	@Required
	public void setImpersonationService(final ImpersonationService impersonationService)
	{
		this.impersonationService = impersonationService;
	}

	protected PromotionsService getPromotionsService()
	{
		return promotionsService;
	}

	@Required
	public void setPromotionsService(final PromotionsService promotionsService)
	{
		this.promotionsService = promotionsService;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}
}
