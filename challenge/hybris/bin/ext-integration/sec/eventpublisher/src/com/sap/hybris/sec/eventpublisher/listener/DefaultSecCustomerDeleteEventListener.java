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
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.dto.customer.Customer;
import com.sap.hybris.sec.eventpublisher.event.DefaultSecDeleteCustomerEvent;
import com.sap.hybris.sec.eventpublisher.handler.impl.AfterCustomerSaveEventHandler;
import com.sap.hybris.sec.eventpublisher.publisher.Publisher;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;


public class DefaultSecCustomerDeleteEventListener extends AbstractEventListener<DefaultSecDeleteCustomerEvent>{

	private static final Logger LOGGER = LogManager.getLogger(DefaultSecCustomerDeleteEventListener.class);

	private Populator customerPopulator;
	private Publisher hciPublisher;

	@Override
	protected void onEvent(DefaultSecDeleteCustomerEvent event) {
		CustomerModel customerModel = new CustomerModel();
		customerModel.setCustomerID(event.getCustomerId());
		LOGGER.info("SEC Deleting Customer ID : "+event.getCustomerId());
		Customer customerJson = new Customer();
		getCustomerPopulator().populate(customerModel, customerJson);
		customerJson.setDelete(true);
		try {
			getHciPublisher().publishJson(customerJson.toString(), EventpublisherConstants.CUSTOMER);
		} catch (IOException e) {
				LOGGER.error("Failed to replicate customer", e);
			}	
		
	}


	public Populator getCustomerPopulator() {
		return customerPopulator;
	}

	public void setCustomerPopulator(Populator customerPopulator) {
		this.customerPopulator = customerPopulator;
	}

	public Publisher getHciPublisher() {
		return hciPublisher;
	}

	public void setHciPublisher(Publisher hciPublisher) {
		this.hciPublisher = hciPublisher;
	}

	
}
