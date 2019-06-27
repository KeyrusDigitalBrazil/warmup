/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package com.hybris.datahub.core.facades.impl;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import com.hybris.datahub.core.event.DatahubAdapterImportEvent;

import java.util.ArrayList;
import java.util.List;

public class DatahubAdapterTestListener extends AbstractEventListener<DatahubAdapterImportEvent>
{
	private List<DatahubAdapterImportEvent> events;

	public List<DatahubAdapterImportEvent> getEvents()
	{
		return events;
	}

	public void addEvent(final DatahubAdapterImportEvent event)
	{
		if (events == null)
		{
			events = new ArrayList<>();
		}
		events.add(event);
	}

	@Override
	protected void onEvent(final DatahubAdapterImportEvent event)
	{
		addEvent(event);
	}
}
