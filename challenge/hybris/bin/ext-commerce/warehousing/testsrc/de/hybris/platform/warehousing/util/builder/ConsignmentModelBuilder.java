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
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

import com.google.common.collect.Sets;


public class ConsignmentModelBuilder
{
	private final ConsignmentModel model;

	private ConsignmentModelBuilder()
	{
		model = new ConsignmentModel();
	}

	private ConsignmentModel getModel()
	{
		return this.model;
	}

	public static ConsignmentModelBuilder aModel()
	{
		return new ConsignmentModelBuilder();
	}

	public ConsignmentModel build()
	{
		return getModel();
	}

	public ConsignmentModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public ConsignmentModelBuilder withWorkflowCode(final String code)
	{
		getModel().setTaskAssignmentWorkflow(code);
		return this;
	}

	public ConsignmentModelBuilder withDeliveryMode(final DeliveryModeModel deliveryMode)
	{
		getModel().setDeliveryMode(deliveryMode);
		return this;
	}

	public ConsignmentModelBuilder withShippingAddress(final AddressModel address)
	{
		getModel().setShippingAddress(address);
		return this;
	}

	public ConsignmentModelBuilder withStatus(final ConsignmentStatus status)
	{
		getModel().setStatus(status);
		return this;
	}

	public ConsignmentModelBuilder withWarehouse(final WarehouseModel warehouse)
	{
		getModel().setWarehouse(warehouse);
		return this;
	}

	public ConsignmentModelBuilder withEntries(final ConsignmentEntryModel... entries)
	{
		getModel().setConsignmentEntries(Sets.newHashSet(entries));
		return this;
	}

	public ConsignmentModelBuilder withOrder(final OrderModel order)
	{
		getModel().setOrder(order);
		return this;
	}

}
