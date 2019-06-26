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
package de.hybris.platform.acceleratorservices.ordersplitting.impl;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.ConsignmentCreationException;
import de.hybris.platform.ordersplitting.impl.DefaultConsignmentService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * Extends the {@link DefaultConsignmentService} with functionality for pickup entries 
 */
public class DefaultPickupConsignmentService extends DefaultConsignmentService
{
	private ModelService modelService;
	private ConsignmentStatus initialConsignmentStatus = ConsignmentStatus.READY;

	@Override
	public ConsignmentModel createConsignment(final AbstractOrderModel order, final String code,
			final List<AbstractOrderEntryModel> orderEntries) throws ConsignmentCreationException
	{
		final ConsignmentModel cons = getModelService().create(ConsignmentModel.class);
		cons.setStatus(getInitialConsignmentStatus());
		cons.setConsignmentEntries(new HashSet<ConsignmentEntryModel>());
		cons.setCode(code);
		cons.setOrder(order);

		for (final AbstractOrderEntryModel orderEntry : orderEntries)
		{
			final ConsignmentEntryModel entry = getModelService().create(ConsignmentEntryModel.class);
			entry.setOrderEntry(orderEntry);
			entry.setQuantity(orderEntry.getQuantity());
			entry.setConsignment(cons);
			cons.getConsignmentEntries().add(entry);
		}

		return cons;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Override
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ConsignmentStatus getInitialConsignmentStatus()
	{
		return initialConsignmentStatus;
	}

	// Optional Configuration
	public void setInitialConsignmentStatus(final ConsignmentStatus initialConsignmentStatus)
	{
		this.initialConsignmentStatus = initialConsignmentStatus;
	}
}
