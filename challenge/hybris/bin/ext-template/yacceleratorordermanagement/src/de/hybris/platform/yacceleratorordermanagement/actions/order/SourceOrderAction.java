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
package de.hybris.platform.yacceleratorordermanagement.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.warehousing.allocation.AllocationService;
import de.hybris.platform.warehousing.constants.WarehousingConstants;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.sourcing.SourcingService;
import de.hybris.platform.yacceleratorordermanagement.constants.YAcceleratorOrderManagementConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.Locale;


/**
 * Action node responsible for sourcing the order and allocating the consignments. After the consignments are created,
 * the consignment sub-process is started for every consignment.
 */
public class SourceOrderAction extends AbstractProceduralAction<OrderProcessModel>
{

	private static final Logger LOGGER = LoggerFactory.getLogger(SourceOrderAction.class);

	private SourcingService sourcingService;
	private AllocationService allocationService;
	private BusinessProcessService businessProcessService;

	@Override
	public void executeAction(final OrderProcessModel process) throws RetryLaterException, Exception
	{
		LOGGER.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());

		final OrderModel order = process.getOrder();

		boolean partialFulfillment = false;
		boolean failedFulfillment = true;

		SourcingResults results = null;
		try
		{
			results = getSourcingService().sourceOrder(order);
		}
		catch (final IllegalArgumentException e) //NOSONAR
		{
			LOGGER.info("Could not create SourcingResults. Changing order status to SUSPENDED");
		}

		if (results != null)
		{
			results.getResults().forEach(this::logSourcingInfo);

			final Collection<ConsignmentModel> consignments = getAllocationService()
					.createConsignments(process.getOrder(), "cons" + process.getOrder().getCode(), results);
			LOGGER.debug("Number of consignments created during allocation: {}", consignments.size());
			startConsignmentSubProcess(consignments, process);
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

	/**
	 * Create and start a consignment process for each consignment in the collection.
	 *
	 * @param consignments
	 * 		- list of consignments; never <tt>null</tt>
	 * @param process
	 * 		- order process model
	 */
	protected void startConsignmentSubProcess(final Collection<ConsignmentModel> consignments, final OrderProcessModel process)
	{
		for (final ConsignmentModel consignment : consignments)
		{
			final ConsignmentProcessModel subProcess = getBusinessProcessService()
					.createProcess(consignment.getCode() + WarehousingConstants.CONSIGNMENT_PROCESS_CODE_SUFFIX,
							YAcceleratorOrderManagementConstants.CONSIGNMENT_SUBPROCESS_NAME);
			subProcess.setParentProcess(process);
			subProcess.setConsignment(consignment);
			save(subProcess);
			LOGGER.info("Start Consignment sub-process: '{}'", subProcess.getCode());
			getBusinessProcessService().startProcess(subProcess);
		}
	}

	protected void logSourcingInfo(final SourcingResult result)
	{
		if (result != null)
		{
			LOGGER.info("Sourcing from Location: '{}'", result.getWarehouse().getCode());
			result.getAllocation().forEach((product, qty) -> LOGGER
					.info("\tProduct [" + product.getProduct().getCode() + "]: '" + product.getProduct().getName(getSessionLocale())
							+ "'\tQuantity: '" + qty + "'"));
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
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

}
