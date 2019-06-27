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

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.warehousing.model.CancellationEventModel;


public class CancellationEventModelBuilder
{
	private final CancellationEventModel model;

	private CancellationEventModelBuilder()
	{
		model = new CancellationEventModel();
	}

	private CancellationEventModel getModel()
	{
		return this.model;
	}

	public static CancellationEventModelBuilder aModel()
	{
		return new CancellationEventModelBuilder();
	}

	public CancellationEventModel build()
	{
		return getModel();
	}

	public CancellationEventModelBuilder withConsignmentEntry(final ConsignmentEntryModel consignmentEntry)
	{
		getModel().setConsignmentEntry(consignmentEntry);
		getModel().setOrderEntry((OrderEntryModel) consignmentEntry.getOrderEntry());
		return this;
	}

	public CancellationEventModelBuilder withReason(final CancelReason reason)
	{
		getModel().setReason(reason.getCode());
		return this;
	}

	public CancellationEventModelBuilder withQuantity(final long quantity)
	{
		getModel().setQuantity(quantity);
		return this;
	}

	public CancellationEventModelBuilder withStockLevel(final StockLevelModel stockLevel)
	{
		getModel().setStockLevel(stockLevel);
		return this;
	}
}
