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
package de.hybris.platform.apiregistryservices.services;

import de.hybris.platform.apiregistryservices.exceptions.ApiRegistrationException;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;


/**
 * Service for exporting/registering APIs
 */
public interface ApiRegistrationService
{
	/**
	 * Register an ExposedDestination
	 *
	 * @param destination
	 *           ExposedDestination to be registered
	 *
	 * @throws ApiRegistrationException
	 *            in case when failed to register Destination
	 */
	void registerExposedDestination(ExposedDestinationModel destination) throws ApiRegistrationException;

	/**
	 * Unregister an ExposedDestination
	 *
	 * @param destination
	 *           ExposedDestination to be unregistered
	 * @throws ApiRegistrationException
	 *            in case when failed to unregister Destination
	 */
	void unregisterExposedDestination(ExposedDestinationModel destination) throws ApiRegistrationException;
}
