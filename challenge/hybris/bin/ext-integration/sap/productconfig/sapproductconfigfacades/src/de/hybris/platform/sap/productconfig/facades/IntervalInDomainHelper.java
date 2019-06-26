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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;


/**
 * Helper for validating and formatting intervals of characteristic domain.
 */
public interface IntervalInDomainHelper
{
	/**
	 * Retrieves concatenated string of intervals in characteristic domain.
	 *
	 * @param cstic
	 *           characteristic model
	 * @return concatenated string of intervals in characteristic domain
	 */
	String retrieveIntervalMask(final CsticModel cstic);

	/**
	 * Converts an interval of characteristic domain into an external format.
	 *
	 * @param interval
	 *           interval in internal format
	 * @return interval in external format
	 */
	String formatNumericInterval(final String interval);

	/**
	 * Retrieves error message.
	 *
	 * @param value
	 *           characteristic value in external format
	 * @param interval
	 *           interval in external format
	 * @return error message
	 */
	String retrieveErrorMessage(String value, String interval);

}
