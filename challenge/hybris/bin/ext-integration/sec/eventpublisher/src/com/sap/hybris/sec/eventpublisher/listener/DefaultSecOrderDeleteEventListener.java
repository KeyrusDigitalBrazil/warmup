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
package com.sap.hybris.sec.eventpublisher.listener;


import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.dto.order.OrderCustomer;
import com.sap.hybris.sec.eventpublisher.dto.order.OrderIndex;
import com.sap.hybris.sec.eventpublisher.event.DefaultSecDeleteOrderEvent;
import com.sap.hybris.sec.eventpublisher.publisher.Publisher;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;


public class DefaultSecOrderDeleteEventListener extends AbstractEventListener<DefaultSecDeleteOrderEvent>{

	private static final Logger LOGGER = LogManager.getLogger(DefaultSecAddressDeleteEventListener.class);

	private Publisher hciPublisher;
	private ConfigurationService configurationService;

	@Override
	protected void onEvent(DefaultSecDeleteOrderEvent event) {
		OrderIndex orderJson = new OrderIndex();
		orderJson.setOrderId(event.getOrderId());
		orderJson.setEventStatus(EventpublisherConstants.ORDER_DELETED);
		OrderCustomer orderCustomer=new OrderCustomer();
		orderCustomer.setId(event.getCustomerId());
		orderJson.setCustomer(orderCustomer);
		try {
			getHciPublisher().publishJson(orderJson.toString(), EventpublisherConstants.ORDER);
		} catch (IOException e) {
				LOGGER.error("Failed to delete Order", e);
			}	
		
	}

	public Publisher getHciPublisher() {
		return hciPublisher;
	}

	public void setHciPublisher(Publisher hciPublisher) {
		this.hciPublisher = hciPublisher;
	}


	public ConfigurationService getConfigurationService() {
		return configurationService;
	}


	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	
}
