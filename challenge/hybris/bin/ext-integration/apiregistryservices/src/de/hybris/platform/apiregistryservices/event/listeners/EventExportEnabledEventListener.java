/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.apiregistryservices.event.listeners;

import static de.hybris.platform.apiregistryservices.utils.EventExportUtils.EXPORTING_PROP;

import de.hybris.platform.apiregistryservices.event.EventExportEnabledEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.util.Config;

import org.slf4j.LoggerFactory;

/**
 * Service-layer listener of @{@link EventExportEnabledEvent}
 * Switches event exporting parameter to true
 */
public class EventExportEnabledEventListener extends AbstractEventListener<EventExportEnabledEvent>
{
	@Override
	protected void onEvent(final EventExportEnabledEvent eventExportEvent)
	{
		Config.setParameter(EXPORTING_PROP, String.valueOf(true));
	}
}
