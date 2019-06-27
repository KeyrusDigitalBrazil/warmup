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

import org.apache.log4j.Logger;

import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.sap.saporderexchangeoms.model.SapConsignmentProcessModel;

/**
 * Class for setting the Hybris order and consignment status after receiving the
 * goods issue information from the SAP ERP backend
 * 
 */
public class SapCheckCompletionStatusAction extends SapOmsAbstractAction<OrderProcessModel> {
	private static final Logger LOG = Logger.getLogger(SapCheckCompletionStatusAction.class);

	@Override
	public String execute(final OrderProcessModel process) {
		
		LOG.info(String.format("Process: %s is in step %s",process.getCode(), getClass().getSimpleName()));

		final OrderModel order = process.getOrder();

		final boolean someEntriesShipped = order.getEntries().stream()
				.anyMatch(entry -> ((OrderEntryModel) entry).getQuantityShipped().longValue() > 0);
		
		if (!someEntriesShipped) {
			order.setDeliveryStatus(DeliveryStatus.NOTSHIPPED);
		
		} else {
			
			final boolean someEntriesWaiting = order.getEntries().stream()
					.anyMatch(entry -> ((OrderEntryModel) entry).getQuantityPending().longValue() > 0);
			
			if (someEntriesWaiting) {
				order.setDeliveryStatus(DeliveryStatus.PARTSHIPPED);
			} else {
				order.setDeliveryStatus(DeliveryStatus.SHIPPED);
			}
		}

		save(order);

		for (final SapConsignmentProcessModel subProcess : process.getSapConsignmentProcesses()) {
			
			if (!subProcess.isDone()) {
				
				LOG.info(String.format("Process: %s found an incomplete subprocess %s and has to wait!",
						process.getCode(), subProcess.getCode()));
				
				return Transition.WAIT.toString();
			}
			
			LOG.info(String.format("Process: %s found subprocess %s complete!", process.getCode(),
					subProcess.getCode()));
			
		}

		order.setStatus(OrderStatus.COMPLETED);
		order.setDeliveryStatus(DeliveryStatus.SHIPPED);
		save(order);

		return Transition.OK.toString();
	}

}
