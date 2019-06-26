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
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.data.ResponseData;
import com.sap.hybris.sec.eventpublisher.dto.customer.Customer;
import com.sap.hybris.sec.eventpublisher.event.DefaultSecValidateCustomerEvent;
import com.sap.hybris.sec.eventpublisher.publisher.Publisher;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;

public class DefaultSecCustomerValidateEventListener extends AbstractEventListener<DefaultSecValidateCustomerEvent> {

	private static final Logger LOGGER = LogManager.getLogger(DefaultSecCustomerValidateEventListener.class);
	private Publisher hciPublisher;

	@Override
	protected void onEvent(DefaultSecValidateCustomerEvent event) {
		try {
			createOrUpdateCustomer(event);
		} catch (URISyntaxException | IOException e) {
			LOGGER.error("Failed to replicate customer", e);
		} catch (final ModelLoadingException e) {
			LOGGER.error("Pk is not of itemModel: ", e);
		}
	}

	private void createOrUpdateCustomer(final DefaultSecValidateCustomerEvent event) throws URISyntaxException, IOException {

		final Customer customerJson = new Customer();
		customerJson.setHybrisCustomerId(event.getCustomerId());
		customerJson.setDefaultAddressId(event.getDefaultAddressId());
		final ResponseData resData = getHciPublisher().publishJson(customerJson.toString(), EventpublisherConstants.CUSTOMER);
		final String resStatus = resData.getStatus();
		if (EventpublisherConstants.HCI_PUBLICATION_STATUS_CREATED.equals(resStatus)
				|| EventpublisherConstants.HCI_PUBLICATION_STATUS_OK.equals(resStatus)) {
			LOGGER.info("Published Successfully");
		}

	}

	/**
	 * @return the hciPublisher
	 */
	public Publisher getHciPublisher() {
		return hciPublisher;
	}

	@Required
	public void setHciPublisher(final Publisher hciPublisher) {
		this.hciPublisher = hciPublisher;
	}

}
