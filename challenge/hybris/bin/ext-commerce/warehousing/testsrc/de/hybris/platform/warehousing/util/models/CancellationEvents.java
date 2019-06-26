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
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.warehousing.model.CancellationEventModel;
import de.hybris.platform.warehousing.util.builder.CancellationEventModelBuilder;
import de.hybris.platform.warehousing.util.dao.WarehousingDao;

import org.springframework.beans.factory.annotation.Required;


public class CancellationEvents extends AbstractItems<CancellationEventModel>
{
	private WarehousingDao<CancellationEventModel> cancellationEventDao;
	private Consignments consignments;

	public CancellationEventModel Camera_Cancellation(final Long quantity, final CancelReason cancelReason, final StockLevelModel stockLevel)
	{
		final ConsignmentModel consignment = getConsignments().Camera_ShippedFromMontrealToMontrealNancyHome(
				ConsignmentStatus.READY, quantity);
		return getOrSaveAndReturn(
				() -> getCancellationEventDao().getByCode(consignment.getCode()),
				() -> CancellationEventModelBuilder.aModel()
						.withConsignmentEntry(consignment.getConsignmentEntries().iterator().next())
						.withQuantity(quantity)
						.withReason(cancelReason)
						.withStockLevel(stockLevel)
						.build());
	}

	public WarehousingDao<CancellationEventModel> getCancellationEventDao()
	{
		return cancellationEventDao;
	}

	@Required
	public void setCancellationEventDao(final WarehousingDao<CancellationEventModel> cancellationEventDao)
	{
		this.cancellationEventDao = cancellationEventDao;
	}

	public Consignments getConsignments()
	{
		return consignments;
	}

	@Required
	public void setConsignments(final Consignments consignments)
	{
		this.consignments = consignments;
	}

}
