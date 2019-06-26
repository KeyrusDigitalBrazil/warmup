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
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.orderexchange.constants.DataHubInboundConstants;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.SapDataHubInboundHelper;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class SapCpiOmsGoodsIssuePersistenceHook implements PrePersistHook
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmsGoodsIssuePersistenceHook.class);
	private final SimpleDateFormat SDF = new SimpleDateFormat(DataHubInboundConstants.IDOC_DATE_FORMAT);

	private SapDataHubInboundHelper sapDataHubInboundHelper;

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		if (item instanceof SAPOrderModel)
		{
			LOG.info("The persistence hook sapCpiOmsGoodsIssuePersistenceHook is called!");
			final SAPOrderModel sapOrderModel = (SAPOrderModel) item;
			if (sapOrderModel.getQuantity() > 0)
			{
				getSapDataHubInboundHelper().processGoodsIssueNotification(sapOrderModel.getCode(),
						sapOrderModel.getOrderEntryNumber().toString(),
						sapOrderModel.getQuantity().toString(),
						SDF.format(sapOrderModel.getGoodsIssueDate()));
			}
			else
			{
				LOG.error("Goods issue notification for order [{}] and entry number [{}] failed because of the missing quantity!",
						sapOrderModel.getCode(),
						sapOrderModel.getOrderEntryNumber());
			}
			return Optional.empty();
		}
		return Optional.of(item);
	}

	protected SapDataHubInboundHelper getSapDataHubInboundHelper()
	{
		return sapDataHubInboundHelper;
	}

	@Required
	public void setSapDataHubInboundHelper(SapDataHubInboundHelper sapDataHubInboundHelper)
	{
		this.sapDataHubInboundHelper = sapDataHubInboundHelper;
	}

}
