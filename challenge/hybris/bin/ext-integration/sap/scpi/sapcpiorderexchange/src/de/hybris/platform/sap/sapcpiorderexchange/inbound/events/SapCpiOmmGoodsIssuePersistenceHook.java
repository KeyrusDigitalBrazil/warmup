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
package de.hybris.platform.sap.sapcpiorderexchange.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.orderexchange.constants.DataHubInboundConstants;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundDeliveryHelper;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class SapCpiOmmGoodsIssuePersistenceHook implements PrePersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmmGoodsIssuePersistenceHook.class);

	private final SimpleDateFormat SDF = new SimpleDateFormat(DataHubInboundConstants.IDOC_DATE_FORMAT);
	private DataHubInboundDeliveryHelper sapDataHubInboundDeliveryHelper;

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		if (item instanceof OrderModel)
		{
			LOG.info("The persistence hook sapCpiOmmGoodsIssuePersistenceHook is called!");
			final OrderModel orderModel = (OrderModel) item;
			getSapDataHubInboundDeliveryHelper().processDeliveryAndGoodsIssue(orderModel.getCode(), orderModel.getSapPlantCode(),
					SDF.format(orderModel.getSapGoodsIssueDate()));
			return Optional.empty();
		}
		return Optional.of(item);
	}

	public DataHubInboundDeliveryHelper getSapDataHubInboundDeliveryHelper()
	{
		return sapDataHubInboundDeliveryHelper;
	}

	@Required
	public void setSapDataHubInboundDeliveryHelper(DataHubInboundDeliveryHelper sapDataHubInboundDeliveryHelper)
	{
		this.sapDataHubInboundDeliveryHelper = sapDataHubInboundDeliveryHelper;
	}

}