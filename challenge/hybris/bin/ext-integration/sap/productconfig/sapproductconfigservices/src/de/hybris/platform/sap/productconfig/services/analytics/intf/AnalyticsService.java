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
package de.hybris.platform.sap.productconfig.services.analytics.intf;

import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;


/**
 * The analytics service retrieves an analytics document.
 *
 */
public interface AnalyticsService
{

	/**
	 * Retrieves the analytic document
	 *
	 * @param configId
	 *           id of the configuration
	 * @return analytical data
	 */
	AnalyticsDocument getAnalyticData(String configId);

	/**
	 * Indicates whether the underlying analytical provider is active
	 *
	 * @return true if the underlying pricing provider is active
	 */
	boolean isActive();
}
