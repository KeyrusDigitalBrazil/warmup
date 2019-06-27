/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.ysapomsfulfillment.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.warehousing.allocation.AllocationService;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.sourcing.SourcingService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.Locale;


/**
 * Action node responsible for sourcing the order and allocating the consignments. After the consignments are created,
 * the consignment sub-process is started for every consignment.
 */
public class SapSourceOrderAction extends AbstractProceduralAction<OrderProcessModel>
{

	private static final Logger LOGGER = Logger.getLogger(SapSourceOrderAction.class);

	private SourcingService sourcingService;
	private AllocationService allocationService;
	private BusinessProcessService businessProcessService;

	@Override
	public void executeAction(final OrderProcessModel process) throws RetryLaterException, Exception
	{

		LOGGER.info(String.format("Process: %s in step %s", process.getCode(), getClass().getSimpleName()));

		final OrderModel order = process.getOrder();

		boolean partialFulfillment = false;
		boolean failedFulfillment = true;

		final SourcingResults results = getSourcingService().sourceOrder(order);
		if (results != null)
		{
			results.getResults().forEach(result -> logSourcingInfo(result));

			final Collection<ConsignmentModel> consignments = getAllocationService().createConsignments(process.getOrder(),
					"cons" + process.getOrder().getCode(), results);

			// Enhance hybris consignment entry with sapOrderEntryRowNumber
			int counter = 0;
			for (ConsignmentModel consignment : consignments)
			{

				for (ConsignmentEntryModel consignmentEntry : consignment.getConsignmentEntries())
				{
					consignmentEntry.setSapOrderEntryRowNumber(++counter);
					getModelService().save(consignmentEntry);
				}

			}

			LOGGER.info(String.format("Number of consignments created during allocation: %s", consignments.size()));
			order.setStatus(OrderStatus.READY);

			partialFulfillment = order.getEntries().stream()
					.anyMatch(orderEntry -> ((OrderEntryModel) orderEntry).getQuantityUnallocated().longValue() > 0);
			failedFulfillment = order.getEntries().stream()
					.allMatch(orderEntry -> ((OrderEntryModel) orderEntry).getQuantityAllocated().longValue() == 0);
		}

		if (results == null || failedFulfillment)
		{
			LOGGER.info("Order failed to be sourced");
			order.setStatus(OrderStatus.SUSPENDED);
		}
		else if (partialFulfillment)
		{
			LOGGER.info("Order partially sourced");
			order.setStatus(OrderStatus.SUSPENDED);
		}
		else
		{
			LOGGER.info("Order was successfully sourced");
		}

		getModelService().save(order);
	}

	private void logSourcingInfo(final SourcingResult result)
	{
		if (result != null)
		{
			LOGGER.info(String.format("Sourcing from Location: %s", result.getWarehouse().getCode()));
			result.getAllocation().forEach((product, qty) -> LOGGER.info(String.format("\tProduct [%s]: %s \tQuantity: '%d'",
					product.getProduct().getCode(), product.getProduct().getName(getSessionLocale()), (long) qty)));

		}
		else
		{
			LOGGER.info("The sourcing result is null");
		}
	}

	protected Locale getSessionLocale()
	{
		return JaloSession.getCurrentSession().getSessionContext().getLocale();
	}

	protected SourcingService getSourcingService()
	{
		return sourcingService;
	}

	@Required
	public void setSourcingService(final SourcingService sourcingService)
	{
		this.sourcingService = sourcingService;
	}

	protected AllocationService getAllocationService()
	{
		return allocationService;
	}

	@Required
	public void setAllocationService(final AllocationService allocationService)
	{
		this.allocationService = allocationService;
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

}
