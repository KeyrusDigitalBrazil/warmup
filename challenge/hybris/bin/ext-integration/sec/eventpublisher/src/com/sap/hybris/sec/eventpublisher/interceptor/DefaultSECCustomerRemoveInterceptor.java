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
package com.sap.hybris.sec.eventpublisher.interceptor;


import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.event.DefaultSecDeleteCustomerEvent;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

public class DefaultSECCustomerRemoveInterceptor implements RemoveInterceptor<CustomerModel>{
	
	private EventService eventService;
	private ConfigurationService configurationService;

	@Override
	public void onRemove(CustomerModel customer, InterceptorContext ctx)
			throws InterceptorException {
		boolean isIntegrationActive = getConfigurationService().getConfiguration().getBoolean(EventpublisherConstants.SEC_INTEGRATION_IS_ACTIVE, false);
		if(isIntegrationActive) {
			DefaultSecDeleteCustomerEvent event = new DefaultSecDeleteCustomerEvent();
			event.setCustomerId(getCustomerId(customer));
			getEventService().publishEvent(event);
		}
	}
	
	public String getCustomerId(CustomerModel customer) {
		return customer.getCustomerID();
		
	}

	public EventService getEventService() {
		return eventService;
	}

	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	
}
