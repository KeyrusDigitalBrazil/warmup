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
package de.hybris.platform.sap.sapcpiorderexchangeoms.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundOrderHelper;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class SapCpiOmsOrderCancellationPersistenceHook implements PrePersistHook
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmsOrderCancellationPersistenceHook.class);
	private DataHubInboundOrderHelper sapDataHubInboundOrderHelper;

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		if (item instanceof SAPOrderModel)
		{
			LOG.info("The persistence hook sapCpiOmsOrderCancellationPersistenceHook is called!");
			final SAPOrderModel sapOrderModel = (SAPOrderModel) item;
			try
			{
				getSapDataHubInboundOrderHelper().cancelOrder(sapOrderModel.getRejectionReason(), sapOrderModel.getCode());
			}
			catch (final ImpExException e) //NOSONAR
			{
				LOG.error("Cancelling order [{}] failed! {}", sapOrderModel.getCode(), e.getMessage());
			}
			return Optional.empty();
		}
		return Optional.of(item);
	}

	protected DataHubInboundOrderHelper getSapDataHubInboundOrderHelper()
	{
		return sapDataHubInboundOrderHelper;
	}

	@Required
	public void setSapDataHubInboundOrderHelper(DataHubInboundOrderHelper sapDataHubInboundOrderHelper)
	{
		this.sapDataHubInboundOrderHelper = sapDataHubInboundOrderHelper;
	}

}
