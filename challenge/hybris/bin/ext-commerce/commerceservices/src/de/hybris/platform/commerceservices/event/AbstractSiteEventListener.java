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
package de.hybris.platform.commerceservices.event;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

public abstract class AbstractSiteEventListener<T extends AbstractEvent> extends AbstractEventListener<T>
{
	protected abstract void onSiteEvent(final T event);

	protected abstract boolean shouldHandleEvent(final T event);

	@Override
	protected void onEvent(final T event)
	{
		if (shouldHandleEvent(event))
		{
			onSiteEvent(event);
		}
	}
}
