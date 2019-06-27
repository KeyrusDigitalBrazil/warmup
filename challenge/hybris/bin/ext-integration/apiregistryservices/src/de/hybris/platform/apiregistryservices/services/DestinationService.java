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

import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;

import java.util.List;


/**
 * Service layer interface for destinations
 * 
 * @param <T>
 *           the type parameter which extends the {@link AbstractDestinationModel} type
 */
public interface DestinationService<T extends AbstractDestinationModel>
{
	/**
	 * Find destinations for specific channel
	 *
	 * @param channel
	 *           the channel of the destination
	 * @return List of destinations for the given channel
	 */
	List<T> getDestinationsByChannel(DestinationChannel channel);

	/**
	 * Find destinations for specific destinationTarget
	 * 
	 * @param destinationTargetId
	 *           the id of the DestinationTarget
	 * @return List of destinations for the given DestinationTarget
	 */
	List<T> getDestinationsByDestinationTargetId(String destinationTargetId);


	/**
	 * Find the list of active destinations for specific clientId
	 *
	 * @param clientId
	 *           The clientId of OAuthClientDetails
	 * @return a List of Destinations by the ExposedOAuthCredential clientId
	 */
	List<ExposedDestinationModel> getActiveExposedDestinationsByClientId(final String clientId);

	/**
	 * Find the list of destinations for specific channel
	 *
	 * @param channel
	 *           The channel assigned to Destinations
	 * @return a List of Destinations by the credential
	 */
	List<ExposedDestinationModel> getActiveExposedDestinationsByChannel(DestinationChannel channel);

	/**
	 * Find the destination for specific id
	 * 
	 * @param id
	 *           the id of the destination
	 * @return The destination for the given id
	 */
	T getDestinationById(String id);


	/**
	 * Get all destinations
	 *
	 * @return the list of destinations
	 */
	List<T> getAllDestinations();
}
