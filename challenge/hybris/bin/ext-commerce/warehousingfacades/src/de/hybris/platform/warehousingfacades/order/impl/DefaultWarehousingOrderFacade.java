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
package de.hybris.platform.warehousingfacades.order.impl;


import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordermanagementfacades.OmsBaseFacade;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.warehousing.onhold.service.OrderOnHoldService;
import de.hybris.platform.warehousingfacades.order.WarehousingOrderFacade;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Default implementation of {@link WarehousingOrderFacade}.
 */
public class DefaultWarehousingOrderFacade extends OmsBaseFacade implements WarehousingOrderFacade
{
	protected static final String ORDER_ACTION_EVENT_NAME = "OrderActionEvent";
	protected static final String RE_SOURCE_CHOICE = "reSource";
	protected static final String PUT_ON_HOLD_CHOICE = "putOnHold";

	private OrderOnHoldService orderOnHoldService;
	private BusinessProcessService businessProcessService;
	private List<OrderStatus> onHoldableOrderStatusList;

	@Override
	public void putOrderOnHold(final String orderCode)
	{
		validateParameterNotNullStandardMessage("orderCode", orderCode);

		final OrderModel order = getOrderModelForCode(orderCode);
		if (getOnHoldableOrderStatusList().contains(order.getStatus()) && order.getEntries().stream()
				.anyMatch(orderEntry -> ((OrderEntryModel) orderEntry).getQuantityPending() > 0))
		{
			order.getOrderProcess().stream()
					.filter(process -> process.getCode().startsWith(order.getStore().getSubmitOrderProcessCode())).forEach(
					filteredProcess -> getBusinessProcessService().triggerEvent(
							BusinessProcessEvent.builder(filteredProcess.getCode() + "_" + ORDER_ACTION_EVENT_NAME)
									.withChoice(PUT_ON_HOLD_CHOICE).withEventTriggeringInTheFutureDisabled().build()));
		}
		else
		{
			throw new IllegalStateException(
					String.format(Localization.getLocalizedString("warehousingfacade.order.onhold.error"), orderCode));
		}
	}

	@Override
	public void reSource(final String orderCode)
	{
		final OrderModel order = getOrderModelForCode(orderCode);

		if (OrderStatus.SUSPENDED.equals(order.getStatus()) || OrderStatus.ON_HOLD.equals(order.getStatus()))
		{
			order.getOrderProcess().stream()
					.filter(process -> process.getCode().startsWith(order.getStore().getSubmitOrderProcessCode())).forEach(
					filteredProcess -> getBusinessProcessService().triggerEvent(
							BusinessProcessEvent.builder(filteredProcess.getCode() + "_" + ORDER_ACTION_EVENT_NAME)
									.withChoice(RE_SOURCE_CHOICE).withEventTriggeringInTheFutureDisabled().build()));
		}
		else
		{
			throw new IllegalStateException(
					String.format(Localization.getLocalizedString("warehousingfacade.order.resourced.error.wrongstatus"),
							order.getStatus()));
		}
	}

	protected OrderOnHoldService getOrderOnHoldService()
	{
		return orderOnHoldService;
	}

	@Required
	public void setOrderOnHoldService(final OrderOnHoldService orderOnHoldService)
	{
		this.orderOnHoldService = orderOnHoldService;
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	protected List<OrderStatus> getOnHoldableOrderStatusList()
	{
		return onHoldableOrderStatusList;
	}

	@Required
	public void setOnHoldableOrderStatusList(final List<OrderStatus> onHoldableOrderStatusList)
	{
		this.onHoldableOrderStatusList = onHoldableOrderStatusList;
	}
}
