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

package de.hybris.platform.apiregistryservices.strategies;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;

/**
 * Strategy for finding the consumed destination
 */
public interface ConsumedDestinationLocatorStrategy
{
	/**
	 * Lookup the consumed destination for the given client type
	 *
	 * @param clientTypeName
	 *           the name of client type
	 * @return the consumed destination for the given client type
	 * @throws CredentialException
	 *            in case when failed to find the expected credential
	 */
	ConsumedDestinationModel lookup(String clientTypeName);
}
