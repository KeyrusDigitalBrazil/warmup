package com.sap.hybris.sec.eventpublisher.b2b.interceptor;

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

import com.sap.hybris.sec.eventpublisher.b2b.event.DefaultSecDeleteB2BUnitEvent;
import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

public class DefaultSECB2BUnitRemoveInterceptor implements RemoveInterceptor<B2BUnitModel>{
	
	private EventService eventService;
	private ConfigurationService configurationService;

	@Override
	public void onRemove(B2BUnitModel b2bUnit, InterceptorContext ctx)
			throws InterceptorException {
		boolean isIntegrationActive = getConfigurationService().getConfiguration().getBoolean(EventpublisherConstants.SEC_INTEGRATION_IS_ACTIVE, false);
		if(isIntegrationActive) {
			DefaultSecDeleteB2BUnitEvent event = new DefaultSecDeleteB2BUnitEvent();
			event.setB2bUnitUid(b2bUnit.getUid());
			event.setB2bUnitPk(b2bUnit.getPk().toString());
			getEventService().publishEvent(event);
		}
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
