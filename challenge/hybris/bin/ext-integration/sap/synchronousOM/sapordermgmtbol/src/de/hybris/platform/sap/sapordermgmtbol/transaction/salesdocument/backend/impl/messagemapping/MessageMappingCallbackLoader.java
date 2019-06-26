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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCallbackProcessor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;





/**
 * Loads call backs during message mapping
 */
public class MessageMappingCallbackLoader
{

	@Autowired
	private ApplicationContext appContext;


	/**
	 * Loads call backs
	 * 
	 * @return Map of call backs with ID as key
	 */
	public Map<String, MessageMappingCallbackProcessor> loadCallbacks()
	{

		final Map<String, MessageMappingCallbackProcessor> callbackProcessors = new HashMap<String, MessageMappingCallbackProcessor>();

		final Map<String, MessageMappingCallbackProcessor> processors = appContext
				.getBeansOfType(MessageMappingCallbackProcessor.class);


		for (final Map.Entry<String, MessageMappingCallbackProcessor> entry : processors.entrySet())
		{
			callbackProcessors.put(entry.getValue().getId(), entry.getValue());
		}

		return callbackProcessors;

	}


}
