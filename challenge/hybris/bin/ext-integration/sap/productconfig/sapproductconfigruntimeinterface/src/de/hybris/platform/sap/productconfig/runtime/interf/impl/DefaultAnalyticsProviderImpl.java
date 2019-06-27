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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * Default implementation of the analytics provider
 */
public class DefaultAnalyticsProviderImpl implements AnalyticsProvider
{
	@Override
	public AnalyticsDocument getPopularity(final ConfigModel config)
	{
		throw new UnsupportedOperationException(
				"Analytics is not supported by default but requires specific runtime implementation");
	}

	@Override
	public boolean isActive()
	{
		return false;
	}

}
