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
package com.sap.hybris.ysaprcomsfulfillment.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.util.Assert;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.saporderexchangeoms.model.SapConsignmentProcessModel;
import com.sap.hybris.ysaprcomsfulfillment.constants.YsaprcomsfulfillmentConstants;
import de.hybris.platform.warehousing.shipping.service.impl.DefaultWarehousingShippingService;


/**
 * Child class to extend the DefaultWarehousingShippingService to include functionality required for integration with
 * SAP backends
 */
public class SapWarehousingShippingService extends DefaultWarehousingShippingService
{

	@Override
	public boolean isConsignmentConfirmable(final ConsignmentModel consignment)
	{
		getModelService().refresh(consignment);

		Assert.notNull(consignment.getOrder(),
				String.format("Order cannot be null for the SAP Consignment with code: [%s].", consignment.getCode()));
		final AbstractOrderModel order = consignment.getOrder();

		Assert.notNull(order.getStatus(),
				String.format("Order Status cannot be null for the Order with code: [%s].", order.getCode()));

		String sapConsignmentProcessCode = new StringBuilder(order.getCode())//
				.append(YsaprcomsfulfillmentConstants.SAP_CONS)//
				.append(consignment.getCode())//
				.toString();

		final Optional<SapConsignmentProcessModel> sapConsignmentProcess = consignment.getSapConsignmentProcesses().stream()
				.filter(sapConsProcess -> sapConsProcess.getCode().equals(sapConsignmentProcessCode)).findFirst();

		Assert.isTrue(sapConsignmentProcess.isPresent(),
				String.format("No process found for the SAP Consignment with the code: [%s].", consignment.getCode()));

		if (getValidConsConfirmConsignmentStatusList().contains(consignment.getStatus())
				&& getValidConsConfirmOrderStatusList().contains(order.getStatus()) && !sapConsignmentProcess.get().isDone()
				&& consignment.getConsignmentEntries().stream().filter(entry -> Objects.nonNull(entry.getQuantityPending()))
						.mapToLong(ConsignmentEntryModel::getQuantityPending).sum() > 0)
		{
			return true;
		}
		return false;
	}

}
