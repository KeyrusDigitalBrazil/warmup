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

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.handler.AfterSaveEventHandler;

import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.AfterSaveListener;


/**
 *
 */
public class DefaultSECSaveListener implements AfterSaveListener
{
	
	private ConfigurationService configurationService;
	private Map<Integer, AfterSaveEventHandler> handlers;

	@Override
	public void afterSave(final Collection<AfterSaveEvent> events)
	{
		boolean isIntegrationActive = getConfigurationService().getConfiguration().getBoolean(EventpublisherConstants.SEC_INTEGRATION_IS_ACTIVE, false);
		if(isIntegrationActive) {
			for (final AfterSaveEvent event : events)
			{
				handleAfterSaveEvent(event);

			}
		}
	}

	public void handleAfterSaveEvent(final AfterSaveEvent event)
	{
		final PK pk = event.getPk();
		final int typeCode= pk.getTypeCode();
		final AfterSaveEventHandler afterSaveEventHandler = handlers.get(Integer.valueOf(typeCode));
		if (afterSaveEventHandler != null){
			afterSaveEventHandler.handleEvent(event);
		}else if(shouldHandle(typeCode)){
			handlers.get(-1).handleEvent(event);
		}

	}

	private boolean shouldHandle(int typeCode) {
		String replicatedTypeCodes = getConfigurationService().getConfiguration().getString(EventpublisherConstants.REPLICATED_TYPE_CODES);
		if(replicatedTypeCodes !=null && !replicatedTypeCodes.trim().equals(EventpublisherConstants.EMPTY_STRING)) {
			String []typeCodes = getConfigurationService().getConfiguration().getString(EventpublisherConstants.REPLICATED_TYPE_CODES).split(EventpublisherConstants.COMMA);
			for(int i=0; i<typeCodes.length; i++) {
				if(typeCodes[i].equals(((Integer)typeCode).toString())) {
					return true;
				}
			}
		}
		return false;
	}

	@Required
	public void setHandlers(final Map<Integer, AfterSaveEventHandler> handlers)
	{
		this.handlers = handlers;
	}
	
	public void registerHandler(int typeCode, AfterSaveEventHandler handler){
		handlers.put(typeCode, handler);
	}
	
	public void removeHandler(int typeCode){
		handlers.remove(typeCode);
	}
	
	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
