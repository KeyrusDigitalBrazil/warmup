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
package de.hybris.platform.sap.saporderexchangeoms.outbound.impl;

import com.hybris.datahub.core.rest.DataHubOutboundException;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultSendOrderToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultSendToDataHubResult;
import de.hybris.platform.sap.sapmodel.enums.SapSystemType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class SapOmsSendOrderToDataHubHelper extends DefaultSendOrderToDataHubHelper {

	private static final Logger LOGGER = Logger.getLogger(SapOmsSendOrderToDataHubHelper.class);

	private static final String DEFAULT_S4HANA_FEED = "DEFAULT_FEED";
	private String sapS4HanaFeed = DEFAULT_S4HANA_FEED;

	@Override
	public String getFeed() {
		return super.getFeed();
	}

	@Override
	public SendToDataHubResult createAndSendRawItem(OrderModel orderModel) {

		if (SapSystemType.SAP_ERP.equals(orderModel.getSapSystemType())) {

			return super.createAndSendRawItem(orderModel);

		} else if (SapSystemType.SAP_S4HANA.equals(orderModel.getSapSystemType())) {

			try {

				getDataHubOutboundService().sendToDataHub(getSapS4HanaFeed(), getRawItemType(),
						getRawItemBuilder().rowsAsNameValuePairs(orderModel));

			} catch (DataHubOutboundException e) {

				LOGGER.error(e);
				return new DefaultSendToDataHubResult(SendToDataHubResult.SENDING_FAILED_CODE, e.getMessage());
			}

			return DefaultSendToDataHubResult.OKAY;

		} else {

			LOGGER.error(String.format(
					"Sending to data hub failed because the type of the logical system [%s] is not defined!",
					orderModel.getSapLogicalSystem()));

			return new DefaultSendToDataHubResult(SendToDataHubResult.MESSAGE_HANDLING_ERROR,
					String.format(
							"Sending to data hub failed because the type of the logical system [%s] is not defined!",
							orderModel.getSapLogicalSystem()));

		}
	}

	protected String getSapS4HanaFeed() {
		return sapS4HanaFeed;
	}

	@Required
	public void setSapS4HanaFeed(String sapS4HanaFeed) {
		this.sapS4HanaFeed = sapS4HanaFeed;
	}

}
