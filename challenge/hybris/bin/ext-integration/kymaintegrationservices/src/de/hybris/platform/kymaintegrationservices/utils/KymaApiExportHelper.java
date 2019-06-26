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

package de.hybris.platform.kymaintegrationservices.utils;

import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;


/**
 * Helper class for API export to kyma
 */
public class KymaApiExportHelper
{
	private KymaApiExportHelper()
	{
	}

	/**
	 * Method to get formatted destinationId for kyma
	 *
	 * @param exposedDestination ExposedDestination
	 * @return formatted destinationId
	 */
	public static String getDestinationId(final ExposedDestinationModel exposedDestination)
	{
		return exposedDestination.getId() + "-" + exposedDestination.getEndpoint().getVersion();
	}
}
