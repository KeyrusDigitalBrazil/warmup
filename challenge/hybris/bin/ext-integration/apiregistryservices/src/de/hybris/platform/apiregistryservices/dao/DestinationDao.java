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

package de.hybris.platform.apiregistryservices.dao;

import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;

import java.util.List;


/**
 * DAO for the {@link AbstractDestinationModel}
 * 
 * @param <T>
 *           the type parameter which extends the {@link AbstractDestinationModel} type
 *
 */
public interface DestinationDao<T extends AbstractDestinationModel>
{
	/**
	 * Find destinations for specific destinationTarget
	 *
	 * @param destinationTargetId
	 *           the id of the DestinationTarget
	 * @return List of destinations for the given DestinationTarget
	 */
	List<T> getDestinationsByDestinationTargetId(String destinationTargetId);

	/**
	 * Find destinations for specific channel
	 *
	 * @param channel
	 *           the DestinationChannel
	 * @return List of destinations for the given channel
	 */
	List<T> getDestinationsByChannel(DestinationChannel channel);

	/**
	 * Get the destination for a specific id
	 *
	 * @param id
	 *           The unique identifier of destination
	 * @return AbstractDestinationModel
	 */
	T getDestinationById(String id);

	/**
	 * Find all destinations
	 *
	 * @return the list of destinations
	 */
	List<T> findAllDestinations();

	/**
	 * Find the list of active destinations for specific clientId
	 *
	 * @param clientId
	 *           The clientId of OAuthClientDetails
	 * @return a List of Destinations by the ExposedOAuthCredential clientId
	 */
	List<ExposedDestinationModel> findActiveExposedDestinationsByClientId(String clientId);

	/**
	 * Find the list of active destinations for specific channel
	 *
	 * @param channel
	 *           The channel assigned to Destinations
	 * @return a List of Destinations by the credential
	 */
	List<ExposedDestinationModel> findActiveExposedDestinationsByChannel(DestinationChannel channel);
}
