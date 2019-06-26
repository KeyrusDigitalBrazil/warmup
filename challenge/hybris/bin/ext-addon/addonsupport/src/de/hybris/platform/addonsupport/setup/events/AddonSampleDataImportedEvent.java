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
package de.hybris.platform.addonsupport.setup.events;

import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.commerceservices.setup.events.SampleDataImportedEvent;
import de.hybris.platform.core.initialization.SystemSetupContext;

import java.util.List;


/**
 * Event indicating that sample data for an AddOn has been loaded.
 */
public class AddonSampleDataImportedEvent extends SampleDataImportedEvent
{
	public AddonSampleDataImportedEvent(final SystemSetupContext context, final List<ImportData> importData)
	{
		super(context, importData);
	}
}
